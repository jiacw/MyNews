package com.jiacw.t03mynews.util;

import android.os.Environment;
import android.os.StatFs;

import com.jiacw.t03mynews.cache.ImgDownloadCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jiacw on 16:49 23/1/2016.
 * Email: 313133710@qq.com
 * Function:SD空间检查相关
 */
public class MiscUtils {
    /**SD上的可用容量
     * @return 可用的空间大小/M
     */
    public static int freeSpaceOnSD() {
        int freeSize = 0;
        if (hasStorage()) {
            //获取文件系统统计信息
            StatFs statFs = new StatFs(ImgDownloadCache.APP_FOLDER_ON_SD);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                long blockSize = statFs.getBlockSizeLong();
                long availableBlock=statFs.getAvailableBlocksLong();
                freeSize= (int) ((blockSize*availableBlock)/1024/1024);
            }
        }
        return freeSize;
    }

    /**
     * 检查是否有可用空间
     * @return true-有剩余空间；false-无剩余空间
     */
    private static boolean hasStorage() {
        boolean hasStorage = false;
        String str = Environment.getExternalStorageState();
        if (str.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            hasStorage = checkFsWritable();
        }
        return hasStorage;
    }

    /**
     * 建立临时文件查看盘是否可写，不要放根目录，也许会有文件数量限制
     *
     * @return true-目录可写;false-不是目录、不能创建文件
     */
    private static boolean checkFsWritable() {
        File directory = new File(ImgDownloadCache.APP_FOLDER_ON_SD);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        File file = new File(ImgDownloadCache.APP_FOLDER_ON_SD, ".probe");
        if (file.exists()) {
            file.delete();
        }
        try {
            if (!file.createNewFile()) {
                return false;
            }
            file.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
