package com.sjl.device.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename RamAndRomUtils.java
 * @time 2018/8/29 9:08
 * @copyright(C) 2018 song
 */
public class RamAndRomUtils {
    private static final int ERROR = 0;
    private static final String TAG = "RamAndRomUtils";

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间(适用于5.0以下)
     * {@link StorageInfoUtils#queryStorageInfo(Context)}
     *
     * @return
     */
    @Deprecated
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getRootDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间(适用于5.0以下)
     * {@link StorageInfoUtils#queryStorageInfo(Context)}
     *
     * @return
     */
    @Deprecated
    public static long getTotalInternalMemorySize() {
        File path = Environment.getRootDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    /**
     * 获取扩展SDCARD剩余存储空间
     * {@link StorageInfoUtils#queryStorageInfo(Context)}
     *
     * @param context
     * @return
     */
    @Deprecated
    public static long getAvailableExternalMemorySize(Context context) {
        try {
            String storagePath = StorageInfoUtils.getStoragePath(context, true);
            LogUtils.i("getAvailableExternalMemorySize:" + storagePath);
            if (!TextUtils.isEmpty(storagePath)) {
                StatFs stat = new StatFs(storagePath);
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return availableBlocks * blockSize;
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return ERROR;
    }

    /**
     * 获取扩展SDCARD总的存储空间
     * {@link StorageInfoUtils#queryStorageInfo(Context)}
     *
     * @param context
     * @return
     */
    @Deprecated
    public static long getTotalExternalMemorySize(Context context) {
        try {
            String storagePath = StorageInfoUtils.getStoragePath(context, true);
            LogUtils.i("getTotalExternalMemorySize:" + storagePath);
            if (!TextUtils.isEmpty(storagePath)) {
                StatFs stat = new StatFs(storagePath);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                return totalBlocks * blockSize;
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return ERROR;
    }


    /**
     * 获取系统总内存
     *
     * @param context 可传入应用程序上下文。
     * @return 总内存大单位为B。
     */
    public static long getTotalMemorySize(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memoryInfo);
            return memoryInfo.totalMem;
        } else {
            String dir = "/proc/meminfo";
            try {
                FileReader fr = new FileReader(dir);
                BufferedReader br = new BufferedReader(fr, 2048);
                String memoryLine = br.readLine();
                String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
                br.close();
                return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context 可传入应用程序上下文。
     * @return 当前可用内存单位为B。
     */
    public static long getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
    private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.#");


    public static String formatFileSize(long size, boolean isInteger, boolean appendUnit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return formatFileSize(size, isInteger, appendUnit, false);
        } else {
            return formatFileSize(size, isInteger, appendUnit, true);
        }
    }

    /**
     * 单位换算
     *
     * @param size       单位为B
     * @param isInteger  是否返回取整的单位
     * @param appendUnit 是否追加单位
     * @param flag       true 使用1024转换，false 1024
     * @return 转换后的单位
     */
    public static String formatFileSize(long size, boolean isInteger, boolean appendUnit, boolean flag) {
        DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
        final int unit = flag ? 1024 : 1000;

        String fileSizeString = "0M";
        if (size < unit && size > 0) {
            if (appendUnit) {
                fileSizeString = df.format((double) size) + "B";
            } else {
                fileSizeString = df.format((double) size);
            }
        } else if (size < unit * unit) {
            if (appendUnit) {
                fileSizeString = df.format((double) size / unit) + "K";
            } else {
                fileSizeString = df.format((double) size / unit);

            }
        } else if (size < unit * unit * unit) {
            if (appendUnit) {
                fileSizeString = df.format((double) size / (unit * unit)) + "M";
            } else {
                fileSizeString = df.format((double) size / (unit * unit));
            }
        } else {
            if (appendUnit) {
                fileSizeString = df.format((double) size / (unit * unit * unit)) + "G";
            } else {
                fileSizeString = df.format((double) size / (unit * unit * unit));
            }
        }
        return fileSizeString;
    }

    /**
     * 返回的进程分配的最大物理内存，包含native heap 和java heap，单位是M
     *
     * @param context
     * @return
     */
    public static long getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memorySize = activityManager.getMemoryClass();
        return memorySize;
    }
}
