package com.jiacw.t03mynews.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jiacw.t03mynews.cache.ImgSDCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Jiacw on 15:36 22/1/2016.
 * Email: 313133710@qq.com
 * Function:Bitmap 工具类
 */
public class BitmapUtil {

    /**
     * 根据图像URL，压缩图片质量，创建Bitmap
     *
     * @param url URL地址
     * @return bitmap
     */
    public Bitmap createImage(String url) {
        Bitmap bitmap;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(url);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;//色彩配置
            options.inTempStorage = new byte[100 * 1024];//临时内存100k
            //inBitmap重用问题！待会添加
            bitmap = BitmapFactory.decodeStream(fis, null, options);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.gc();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 保存缩放的位图到SDCard
     *
     * @param byteArrayOS 图片字节流
     * @param screen      屏幕宽高
     * @param url         图片网络路径
     * @param cachePath   本地缓存父路径
     * @param isJpg       是否是Jpg
     * @return 缩放后的图片bitmap
     */
    public static Bitmap saveZoomBitmapToSDCard(ByteArrayOutputStream byteArrayOS
            , ScreenUtil.Screen screen, String url, String cachePath, boolean isJpg) {
        Bitmap bitmap;
        byte[] byteArray = byteArrayOS.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[16 * 1024];
        //只加载图片的边界
        options.inJustDecodeBounds = true;
        //获取Bitmap信息
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        //获取屏幕的宽和高
        int screenWidth = screen.width;
        int screenHeight = screen.height;
        //屏幕最大像素个数
        int maxNumOfPixels = screenWidth * screenHeight;
        //计算采样率
        options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
        options.inJustDecodeBounds = false;
        //重新读入图片,此时为缩放后的图片
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        //压缩比例
        int quality = 100;
        //判断是否是Jpg,png是无损压缩,所以不用进行质量压缩
        if (bitmap != null && isJpg) {
            ByteArrayOutputStream saveBAOS = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, saveBAOS);
            while (saveBAOS.toByteArray().length / 1024 > 100) {
                //重置saveBAOS即清空saveBAOS
                saveBAOS.reset();
                quality -= 10;
                //这里压缩optionsNum%，把压缩后的数据存放到saveBAOS中
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, saveBAOS);
            }
            //把压缩后的数据ByteArrayOutputStream存放到ByteArrayInputStream中
            ByteArrayInputStream saveBAIS = new ByteArrayInputStream(saveBAOS.toByteArray());
            bitmap = BitmapFactory.decodeStream(saveBAIS, null, null);
        }
        //保存到SDCard
        ImgSDCache.getImageSDCache().saveBitmapToSDCard(bitmap, url, cachePath, isJpg, quality);
        return bitmap;
    }

    /**
     * 计算采样率
     *
     * @param options 设置
     * @param minSideLength -1
     * @param maxNumOfPixels 屏幕最大像素个数
     * @return 采样率
     */
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength
            , int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundSize;
        if (initialSize <= 8) {
            roundSize = 1;
            while (roundSize < initialSize) {
                roundSize <<= 1;
            }
        } else {
            roundSize = (initialSize + 7) / 8 * 8;
        }
        return roundSize;
    }

    /**
     * 计算初始采样率
     *
     * @param options 设置
     * @param minSideLength -1
     * @param maxNumOfPixels 屏幕最大像素个数
     * @return 初始采样率
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength
            , int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1
                : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength)
                , Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
