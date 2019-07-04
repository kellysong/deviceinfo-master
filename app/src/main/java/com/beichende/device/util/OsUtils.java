package com.beichende.device.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
//        try {
//            if (info != "") {
//                final String keyword = "version ";
//                int index = info.indexOf(keyword);
//                line = info.substring(index + keyword.length());
//                index = line.indexOf(" ");
//                kernelVersion = line.substring(0, index);
//            }
//        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }

        return kernelVersion;
    }

}
