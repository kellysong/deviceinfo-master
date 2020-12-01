package com.sjl.device.util;

import android.content.Context;
import android.text.TextUtils;

import com.sjl.device.bean.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 设备信息处理类
 * 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
 * @author Kelly
 * @version 1.0.0
 * @filename NetworkInfoUtils.java
 * @time 2017年12月1日 上午11:00:28
 * @copyright(C) 2017 song
 */
public class NetworkInfoUtils {

    /**
     * 获取设备的ip和mac地址
     *
     * @return
     * @throws Exception
     */
    public static NetworkInfo getNetworkInfo(Context context) throws Exception {
        NetworkInfo deviceInfo = null;
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {//不是回环地址且是ipv4地址
                    String mac = MacUtils.getMac(context);
                    if (!TextUtils.isEmpty(mac)) {
                        mac = mac.toUpperCase().replaceAll(":","-");
                    }
                    deviceInfo = new NetworkInfo(inetAddress.getHostAddress(), mac);
                    return deviceInfo;
                }
            }
        }
        return deviceInfo;
    }

}
