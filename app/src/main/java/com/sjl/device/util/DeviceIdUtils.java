package com.sjl.device.util;

import android.os.Build;

import java.util.UUID;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename DeviceIdUtils.java
 * @time 2020/2/29 13:25
 * @copyright(C) 2020 song
 */
public class DeviceIdUtils {
    /**
     * 获得独一无二的Psuedo ID
     *c4-miui-ota-bd35.bj:ffffffff-a31b-4de9-ffff-ffffc3999f7d,小米5
     *
     * @return
     */
    public static String getUniquePsuedoID() {
        String serial;
        System.out.println("Build.BOARD:"+Build.BOARD);
        System.out.println("Build.BRAND:"+Build.BRAND);
        System.out.println("Build.DEVICE:"+Build.DEVICE);
        System.out.println("Build.DISPLAY:"+Build.DISPLAY);
        System.out.println("Build.HOST:"+Build.HOST);
        System.out.println("Build.ID:"+Build.ID);
        System.out.println("Build.MANUFACTURER:"+Build.MANUFACTURER);
        System.out.println("Build.MODEL:"+Build.MODEL);
        System.out.println("Build.PRODUCT:"+Build.PRODUCT);
        System.out.println("Build.TAGS:"+Build.TAGS);
        System.out.println("Build.TYPE:"+Build.TYPE);
        System.out.println("Build.USER:"+Build.USER);
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位

        LogUtils.i("m_szDevIDShort:"+m_szDevIDShort);
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            LogUtils.i("serial:"+serial);
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
