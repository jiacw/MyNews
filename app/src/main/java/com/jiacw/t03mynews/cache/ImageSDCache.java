package com.jiacw.t03mynews.cache;

import android.graphics.Bitmap;

import com.jiacw.t03mynews.util.BitmapUtil;
import com.jiacw.t03mynews.util.MiscUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Jiacw on 20:42 21/1/2016.
 * Email: 313133710@qq.com
 * Function:图片SD卡缓存
 */
public class ImageSDCache {
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 50;//mb
    private static final String JPG = ".jpg";
    private static final int MAX_CACHE_SIZE_NEEDED = 30;//mb
    private String filePic;
    private final BitmapUtil mBitmapUtil = new BitmapUtil();
    private static ImageSDCache imageSDCache;

    /**
     * 获取ImageSDCache实例
     * @return 实例
     */
    public static ImageSDCache getImageSDCache() {
        if (imageSDCache==null){
            imageSDCache=new ImageSDCache();
        }
        return imageSDCache;
    }

    /**
     * 根据图片缓存路径返回Bitmap
     *
     * @param url       图片缓存路径
     * @param cachePath 本地缓存父路径
     * @return bitmap
     */
    public Bitmap getBitmapByCachePath(String url, String cachePath) {
        Bitmap bitmap = null;
        if (isImageSDCachedByPath(url, cachePath)) {
            bitmap = mBitmapUtil.createImage(filePic);
        }
        return bitmap;
    }

    /**
     * 判断图片是否存在
     *
     * @param url       图片链接
     * @param cachePath 本地缓存父路径
     * @return 是否存在
     */
    private boolean isImageSDCachedByPath(String url, String cachePath) {
        if (url == null || url.trim().length() <= 0) {
            return false;
        }
        //url非空，长度大于0
        String filename = url.startsWith("http://") ? convertUrlToName(url) : url;
        File fileDir = new File(cachePath + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        if (filename != null) {
            filePic = url.startsWith("http://") ? (cachePath + File.separator + filename) : filename;
            File file = new File(filePic);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将url转换为文件名
     *
     * @param url 图片地址
     * @return 文件名
     */
    private String convertUrlToName(String url) {
        String filename;
        if (url.contains(".png")) {
            filename = String.valueOf(url.hashCode() + ".png");
        } else {
            filename = String.valueOf(url.hashCode() + ".jpg");
        }
        return filename;
    }

    /**
     * 保存Bitmap到指定的目录下
     *
     * @param bitmap    保存的bitmap
     * @param url       图片网络路径
     * @param cachePath 本地缓存父路径
     * @param isJpg     是否是JPG
     * @param quality   缩放比
     */
    public  void saveBitmapToSDCard(Bitmap bitmap, String url, String cachePath
            , boolean isJpg, int quality) {
        boolean result;
        if (bitmap == null) {
            return;
        }
        if (url == null || url.equals("")) {
            return;
        }
        //如果可用容量不足50兆的话
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > MiscUtils.freeSpaceOnSD()) {
            return;
        }
        File makeDirectoryPathFile = new File(cachePath);
        if (!makeDirectoryPathFile.isDirectory()) {
            makeDirectoryPathFile.mkdir();
        }
        String filename = convertUrlToName(url);
        File file = new File(cachePath + File.separator + filename);
        try {
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            if (isJpg) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //清理缓存
        removeCache(cachePath);
    }

    /**
     * 清理缓存
     *
     * @param cachePath 本地缓存父路径
     */
    private void removeCache(String cachePath) {
        File dir = new File(cachePath);
        File[] files = dir.listFiles();//返回目录文件的文件列表
        if (files == null) {
            return;
        }
        int dirSize = 0;//文件的字节大小
        for (File file : files) {
            if (file.getName().contains(JPG)) {
                dirSize += file.length();
            }
        }
        //如果文件大小大于30兆或可用小于50兆
        if (dirSize > MAX_CACHE_SIZE_NEEDED * 1024 * 1024 || FREE_SD_SPACE_NEEDED_TO_CACHE
                > MiscUtils.freeSpaceOnSD()) {
            //删除40%最近没有被使用的文件
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifySort());//顺序排列
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(JPG)) {
                    files[i].delete();
                }
            }
        }
    }

    /**
     * 根据文件的最后修改时间进行排序
     */
    private class FileLastModifySort implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() > rhs.lastModified()) {
                return 1;
            } else if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
