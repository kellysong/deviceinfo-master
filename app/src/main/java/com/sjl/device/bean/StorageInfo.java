package com.sjl.device.bean;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename StorageInfo
 * @time 2022/4/27 21:40
 * @copyright(C) 2022 song
 */
public class StorageInfo {
    public static final int STORAGE_TYPE_EXTERNAL=0;
    public static final int STORAGE_TYPE_INTERNAL=1;

    public int storageType;
    public String path;
    /**总大小**/
    public long total;
    /**可用**/
    public long free;
    /**已使用**/
    public long used;
    /**系统大小**/
    public long systemSize;


    public StorageInfo(int storageType, String path, long total, long free) {
        this.storageType = storageType;
        this.path = path;
        this.total = total;
        this.free = free;
    }

    public StorageInfo(int storageType, String path, long total, long free,long used, long systemSize) {
        this.storageType = storageType;
        this.path = path;
        this.total = total;
        this.free = free;
        this.used = used;
        this.systemSize = systemSize;
    }

    @Override
    public String toString() {
        return "StorageInfo{" +
                "storageType=" + storageType +
                ", path='" + path + '\'' +
                ", total=" + total +
                ", used=" + used +
                '}';
    }
}
