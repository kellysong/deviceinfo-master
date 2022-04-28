package com.sjl.device.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename OsUtils.java
 * @time 2018/8/29 11:08
 * @copyright(C) 2018 song
 */
public class OsUtils {

    /**
     * 获取内核版本
     * @return
     */
    public static String getKernelVersion() {
        String kernelVersion = "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/version");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return kernelVersion;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        String info = "";
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        kernelVersion = info;

        return kernelVersion;
    }

    /**
     * BASEBAND-VER
     * 基带版本
     * return String
     */

    public static String getBaseBandVersion(){
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[] { String.class,String.class });
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String)result;
        } catch (Exception e) {
        }
        return Version;
    }



    /**
     * INNER-VER
     * 内部版本
     * return String
     */

    public static String getInnerVersion(){
        String ver = "" ;
        if(android.os.Build.DISPLAY .contains(android.os.Build.VERSION.INCREMENTAL)){
            ver = android.os.Build.DISPLAY;
        }else{
            ver = android.os.Build.VERSION.INCREMENTAL;
        }
        return ver;
    }


}
