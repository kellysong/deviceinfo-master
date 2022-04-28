package com.sjl.device.util;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.sjl.device.bean.StorageInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.RequiresApi;

/**
 * 存储信息工具类
 */
public class StorageInfoUtils {


    /**
     * 查询存储基本信息（适配所有系统版本）
     *
     * @param context
     * @return
     */
    public static Map<Integer, StorageInfo> queryStorageInfo(Context context) {
        //5.0 查外置存储
        Map<Integer, StorageInfo> storageInfos = new LinkedHashMap<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> aClass = storageManager.getClass();
        int version = Build.VERSION.SDK_INT;
        if (version < Build.VERSION_CODES.M) {//小于6.0
            try {
                Method getVolumeList = aClass.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        StorageInfo storageInfo = getStorageSize(StorageInfo.STORAGE_TYPE_INTERNAL, file.getPath());
                        storageInfos.put(storageInfo.storageType, storageInfo);
                       /* totalSize += file.getTotalSpace();
                        availableSize += file.getUsableSpace();*/
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e);
                queryStorageInfoOnLowVersion(storageInfos);
            }
        } else {

            try {
                Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");//6.0
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                for (Object obj : getVolumeInfo) {
                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
                    LogUtils.i("type: " + type);
                    if (type == 1) {//TYPE_PRIVATE
                        long total = 0L, used = 0L, systemSize = 0L, free = 0L;
                        long totalSize = 0L;
                        //获取内置内存总大小
                        if (version >= Build.VERSION_CODES.O) {//8.0
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(context, fsUuid);//8.0 以后使用
                        } else if (version >= Build.VERSION_CODES.N_MR1) {//7.1.1
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");//5.0 6.0 7.0没有
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
                        }

                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);

                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            systemSize = totalSize - f.getTotalSpace();
                            used += totalSize - f.getFreeSpace();
                            total += totalSize;
                            free = total - used;
                            StorageInfo storageInfo = null;
//                            storageInfo= getStorageSize(StorageInfo.STORAGE_TYPE_INTERNAL, f.getPath());
                            storageInfo = new StorageInfo(StorageInfo.STORAGE_TYPE_INTERNAL, f.getPath(), total, free, used, systemSize);
                            storageInfos.put(storageInfo.storageType, storageInfo);

                        }
                        LogUtils.i("type:" + type + ",totalSize = " + RamAndRomUtils.formatFileSize(total, false, true)
                                + " ,used(with system) = " + RamAndRomUtils.formatFileSize(used, false, true)
                                + " ,free = " + RamAndRomUtils.formatFileSize(free, false, true)
                                + ",systemSize = " + RamAndRomUtils.formatFileSize(systemSize, false, true));

                    } else if (type == 0) {//TYPE_PUBLIC
                        long total = 0L, used = 0L, free = 0L;
                        //外置存储
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            used += f.getTotalSpace() - f.getFreeSpace();
                            total += f.getTotalSpace();
                            free = total - used;
                            StorageInfo storageInfo = new StorageInfo(StorageInfo.STORAGE_TYPE_EXTERNAL, f.getPath(), total, free, used, 0L);
                            storageInfos.put(storageInfo.storageType, storageInfo);
                        }
                        LogUtils.i("type:" + type + ",totalSize = " + RamAndRomUtils.formatFileSize(total, false, true)
                                + " ,used = " + RamAndRomUtils.formatFileSize(used, false, true)
                                + " ,free = " + RamAndRomUtils.formatFileSize(free, false, true)
                        );

                    } else if (type == 2) {//TYPE_EMULATED emulated

                    }

                }

            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return storageInfos;
    }

    public static void queryStorageInfoOnLowVersion(Map<Integer, StorageInfo> storageInfos) {
        //内部存储
        StorageInfo internalStorageInfo = getStorageSize(StorageInfo.STORAGE_TYPE_INTERNAL, Environment.getDataDirectory().getPath());
        //外部存储
        StorageInfo externalStorageInfo = getStorageSize(StorageInfo.STORAGE_TYPE_EXTERNAL, Environment.getExternalStorageDirectory().getPath());
        storageInfos.put(internalStorageInfo.storageType, internalStorageInfo);
        storageInfos.put(externalStorageInfo.storageType, externalStorageInfo);
    }

    private static StorageInfo getStorageSize(int type, String path) {
        StatFs statFs = new StatFs(path);
        //块大小
        long blockSize = statFs.getBlockSize();
        //存储块
        long blockCount = statFs.getBlockCount();

        //可用块数量
        long availableCount = statFs.getAvailableBlocks();
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        long freeBlocks = statFs.getFreeBlocks();

        //level 18
//        long totalSize = statFs.getTotalBytes();
//        long availableSize = statFs.getAvailableBytes();
        return new StorageInfo(type, path, blockSize * blockCount, blockSize * availableCount);
    }


    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public static long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 获取存储路径
     *
     * @param context
     * @param isRemovable 为false时得到的是内置sd卡路径，为true则为外置sd卡路径。
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static String getStoragePath(Context context, boolean isRemovable) throws Exception {

        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0以上
            List<StorageVolume> storageVolumes = mStorageManager.getStorageVolumes();
            LogUtils.i("storageVolumes：" + storageVolumes.size());
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
            LogUtils.i("storageVolumes：" + length);
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
}