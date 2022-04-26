package com.sjl.device.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

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
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取扩展SDCARD剩余存储空间
     *
     * @param context
     * @return
     */
    public static long getAvailableExternalMemorySize(Context context) {
        try {
            String storagePath = getStoragePath(context, true);
            if (!TextUtils.isEmpty(storagePath)) {
                StatFs stat = new StatFs(storagePath);
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return availableBlocks * blockSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    /**
     * 获取扩展SDCARD总的存储空间
     *
     * @param context
     * @return
     */
    public static long getTotalExternalMemorySize(Context context) {
        try {
            String storagePath = getStoragePath(context, true);
            if (!TextUtils.isEmpty(storagePath)) {
                StatFs stat = new StatFs(storagePath);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                return totalBlocks * blockSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    /**
     * 获取存储路径
     *
     * @param context
     * @param isRemovable 为false时得到的是内置sd卡路径，为true则为外置sd卡路径。
     * @return
     */
    public static String getStoragePath(Context context, boolean isRemovable) throws Exception {

        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0以上
            List<StorageVolume> storageVolumes = mStorageManager.getStorageVolumes();
            Log.i(TAG, "storageVolumes："+storageVolumes.size());
            for (StorageVolume storageVolume : storageVolumes) {
                boolean removable = storageVolume.isRemovable();
                Class<?> storageVolumeClazz = storageVolume.getClass();
                Method getPath = storageVolumeClazz.getMethod("getPath");
                String path = (String) getPath.invoke(storageVolume);
                if (isRemovable == removable) {
                    return path;
                }
            }
        } else {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method is_Removable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            Log.i(TAG, "storageVolumes："+length);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                if (isRemovable == (Boolean) is_Removable.invoke(storageVolumeElement)) {
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * 获取系统总内存
     *
     * @param context 可传入应用程序上下文。
     * @return 总内存大单位为B。
     */
    public static long getTotalMemorySize(Context context) {
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

    /**
     * 单位换算
     *
     * @param size       单位为B
     * @param isInteger  是否返回取整的单位
     * @param appendUnit 是否追加单位
     * @return 转换后的单位
     */
    public static String formatFileSize(long size, boolean isInteger, boolean appendUnit) {
        DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
        String fileSizeString = "0M";
        if (size < 1024 && size > 0) {
            if (appendUnit) {
                fileSizeString = df.format((double) size) + "B";
            } else {
                fileSizeString = df.format((double) size);
            }
        } else if (size < 1024 * 1024) {
            if (appendUnit) {
                fileSizeString = df.format((double) size / 1024) + "K";
            } else {
                fileSizeString = df.format((double) size / 1024);

            }
        } else if (size < 1024 * 1024 * 1024) {
            if (appendUnit) {
                fileSizeString = df.format((double) size / (1024 * 1024)) + "M";
            } else {
                fileSizeString = df.format((double) size / (1024 * 1024));
            }
        } else {
            if (appendUnit) {
                fileSizeString = df.format((double) size / (1024 * 1024 * 1024)) + "G";
            } else {
                fileSizeString = df.format((double) size / (1024 * 1024 * 1024));
            }
        }
        return fileSizeString;
    }

    /**
     * 返回的进程分配的最大物理内存，包含native heap 和java heap，单位是M
     * @param context
     * @return
     */
    public static long getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memorySize = activityManager.getMemoryClass();
        return memorySize;
    }
}
