package com.sjl.device.util;

import android.content.Context;

/**
 * 目前手机出厂下配置电池容量主要是通过修改 power_profile.xml 的电池容量参数，一般Google 默认配置为 1000 mAh

 故只要是出货的手机一般都需要修改该值。我们可以直接导出 frameworks\base\core\res\res\xml\power_profile.xml 进行查看与修改

 或者使用 Java 反射 PowerProfile.java 求出电池容量大小
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BatteryUtils.java
 * @time 2018/8/29 10:59
 * @copyright(C) 2018 song
 */
public class BatteryUtils {
    /**
     * 获取电池容量 mAh
     *
     * 源头文件:frameworks/base/core/res\res/xml/power_profile.xml
     *
     * Java 反射文件：frameworks\base\core\java\com\android\internal\os\PowerProfile.java
     */
    public static String getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(batteryCapacity + " mAh");
    }
}
