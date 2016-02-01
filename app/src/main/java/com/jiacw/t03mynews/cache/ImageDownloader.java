package com.jiacw.t03mynews.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.util.BitmapUtil;
import com.jiacw.t03mynews.util.LogUtil;
import com.jiacw.t03mynews.util.ScreenUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Jiacw on 20:31 21/1/2016.
 * Email: 313133710@qq.com
 * Function:从网络下载图片，和ImageView绑定的帮助类；
 * 本地缓存，保持在内部用来提高表现
 */
public class ImageDownloader {
    private static final int DELAY_BEFORE_PURGE = 30 * 1000;//30秒清空一次缓存
    private static final String DEFAULT_BITMAP_CACHE = "default_bitmap_cache";
    private final ScreenUtil.Screen mScreen;
    private final Context mContext;
    private final ImageSDCache mImageSDCache;
    private ImageView.ScaleType mScaleType;
    private final Handler purgeHandler = new Handler();
    private static final int HARD_CACHE_CAPACITY = 2;//缓存中Bitmap强引用的个数

    public static final String APP_FOLDER_ON_SD = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/MyNews/my_news";
    public static final String PHOTO_CACHE_FOLDER = APP_FOLDER_ON_SD + "/photo_cache";

    /**
     * created at 22/1/2016 10:46
     * function: 实例化ImageSDCache类，获取Screen对象
     */
    public ImageDownloader(Context context) {
        mImageSDCache = ImageSDCache.getImageSDCache();
        mContext = context;
        mScreen = ScreenUtil.getScreenPixel(context);
    }

    /**
     * 重载,可以根据给定的特殊类型,显示特定的默认图片;
     * 可能提供额外的本地数据，用来检索图片
     *
     * @param url       The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void download(String url, ImageView imageView) {
        mScaleType = ImageView.ScaleType.FIT_XY;
        resetPurgeTimer();//异步清理软引用和强引用
        Bitmap bitmap = getBitmapFromCache(url);//从缓存中获取Bitmap
        if (bitmap == null) {
            bitmap = loadFromSDCache(url);//从SD卡上获取
        }
        if (bitmap == null) {//从网上下载
            forceDownload(url, imageView, null);
        } else {//bitmap 非空
            if (mScaleType != null && imageView != null) {
                imageView.setScaleType(mScaleType);
            }
            if (imageView != null) {
                cancelPotentialDownload(url, imageView);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     *
     * @param url       图片地址
     * @param imageView 图片视图
     * @param cookie    A cookie String that will be used by the http connection.
     */
    private void forceDownload(String url, ImageView imageView, String cookie) {
        //当图片地址为空时
        if (url == null && imageView != null) {
            //设置默认图片
            imageView.setImageBitmap(getDefaultBitmap(mContext));
            return;
        }
        if (cancelPotentialDownload(url, imageView)) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, mContext);
            if (imageView != null) {
                imageView.setTag(downloadedDrawable);
            }
            task.execute(url, cookie);
        }
    }

    /**
     * 取消潜在的下载任务
     * @param url       图片地址
     * @param imageView 图片视图
     * @return true-当前下载被取消或没在下载；false-下载过了或没被停止
     */
    private boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                //The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * 返回默认的加载图片
     *
     * @param context 上下文
     * @return 默认图片的Bitmap
     */
    private Bitmap getDefaultBitmap(Context context) {
        // 返回默认图片的Bitmap
        return getBitmapByResId(context);
    }

    /**
     * 根据图片的资源ID创建Bitmap
     *
     * @param context  上下文
     * @return 硬缓存
     */
    private Bitmap getBitmapByResId(Context context) {
        //从缓存中取默认图片
        Bitmap resBitmap = mHardBitmapCache.get(DEFAULT_BITMAP_CACHE);
        try {
            //如果没取到
            if (resBitmap == null) {
                //创建图片的Bitmap
                resBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                //放到缓存中
                mHardBitmapCache.put(DEFAULT_BITMAP_CACHE, resBitmap);
            }
        } catch (OutOfMemoryError e) {
            System.gc();
        }
        return resBitmap;
    }

    /**
     * created at 22/1/2016 13:45
     * function: 从SD卡上获取Bitmap
     */
    private Bitmap loadFromSDCache(String url) {
        Bitmap bitmap = mImageSDCache.getBitmapByCachePath(url, PHOTO_CACHE_FOLDER);
        if (bitmap != null) {
            //添加到RAM　缓存
            synchronized (mHardBitmapCache) {
                mHardBitmapCache.put(url, bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 从缓存从获取Bitmap
     *
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url) {
        if (url == null || url.length() == 0) {
            return null;
        }
        // First try the hard reference cache
        synchronized (mHardBitmapCache) {
            final Bitmap bitmap = mHardBitmapCache.get(url);
            // Bitmap found in hard cache
            // Move element to first position, so that it is removed last
            if (bitmap != null) {
                mHardBitmapCache.remove(url);
                mHardBitmapCache.put(url, bitmap);
                return bitmap;
            }

        }
        //尝试软引用缓存
        SoftReference<Bitmap> bitmapReference=mSoftBitmapCache.get(url);
        if (bitmapReference!=null){
            final Bitmap bitmap=bitmapReference.get();
            if (bitmap!=null){
                return bitmap;
            }else {
                // Soft reference has been Garbage Collected
                mSoftBitmapCache.remove(url);
            }
        }
        return null;
    }

    /**
     * 重置清洗任务
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }

    /**
     * 清理缓存
     */
    private final Runnable purger = new Runnable() {
        @Override
        public void run() {
            clearCache();
        }
    };

    /**
     * created at 22/1/2016 12:14
     * function: 清除图片缓存，因为内存效率的原因，缓存会被自动清理，在一个确定的静态延迟后
     */
    private void clearCache() {
        mHardBitmapCache.clear();
        mSoftBitmapCache.clear();
    }

    /**
     * Bitmap 软引用移除硬引用;二级缓存
     */
    private static final HashMap<String, SoftReference<Bitmap>> mSoftBitmapCache = new LinkedHashMap
            <String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY) {
        @Override
        protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                LogUtil.d("jiacw",size()+"");
                System.gc();//通知垃圾收集
                return true;
            } else {
                return false;
            }
        }
    };
    /**
     * 硬引用，有固定的最大容量和一个生命周期；一级缓存；
     */
    private static final HashMap<String, Bitmap> mHardBitmapCache = new LinkedHashMap<String, Bitmap>
            (HARD_CACHE_CAPACITY, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
            // 如果强引用的缓存数量超过了规定的量,则转存到软引用中,避免内存使用过多
            if (size() > HARD_CACHE_CAPACITY) {
                mSoftBitmapCache.put(eldest.getKey(), new SoftReference<>(eldest.getValue()));
                LogUtil.d("jiacw",mSoftBitmapCache.size()+"");
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * 异步下载图片
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final static int IO_BUFFER_SIZE = 4 * 1024;
        private final WeakReference<ImageView> imageViewReference;

        BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];//获得URL
            Bitmap bitmap;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            //去网络端取图片
            try {
                URL httpUrl = new URL(url);
                httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                int statusCode = httpURLConnection.getResponseCode();
                if (statusCode != 200) {
                    return null;
                }
                inputStream = httpURLConnection.getInputStream();
                final ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                outputStream = new BufferedOutputStream(byteArrayOS, IO_BUFFER_SIZE);
                copy(inputStream, outputStream);
                outputStream.flush();
                boolean isJpg = url.contains(".jpg");
                bitmap = BitmapUtil.saveZoomBitmapToSDCard(byteArrayOS, mScreen, url
                        , PHOTO_CACHE_FOLDER, isJpg);
                return bitmap;
            } catch (IOException | OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        /**
         * 将输入流拷入到输出流中
         *
         * @param inputStream  输入流
         * @param outputStream 输出流
         * @throws IOException
         */
        private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
            byte[] b = new byte[IO_BUFFER_SIZE];
            int read;
            while ((read = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, read);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap != null) {
                synchronized (mHardBitmapCache) {
                    mHardBitmapCache.put(url, bitmap);
                }
            }
            ImageView imageView = imageViewReference.get();
            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
            // Change bitmap only if this process is still associated with it
            if (this == bitmapDownloaderTask && bitmap != null) {
                if (mScaleType != null) {
                    imageView.setScaleType(mScaleType);
                }
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 取得下载任务
     *
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Object objDrawable = imageView.getTag();
            if (objDrawable != null && objDrawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) objDrawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    /**
     * A fake Drawable that will be attached to the imageView while the download
     * is in progress.
     * <p>Contains a reference to the actual download task, so that a download task
     * can be stopped if a new binding is required, and makes sure that only the
     * last started download process can bind its result, independently of the
     * download finish order.
     * </p>
     */
    class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<BitmapDownloaderTask> mBitmapDTWR;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Context context) {
            super(context.getResources(), getDefaultBitmap(context));
            mBitmapDTWR = new WeakReference<>(bitmapDownloaderTask);
        }

        /**
         * 获取引用对象的引用
         * @return 引用
         */
        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return mBitmapDTWR.get();
        }
    }
}
