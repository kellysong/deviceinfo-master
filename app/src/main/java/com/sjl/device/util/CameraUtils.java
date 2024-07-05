package com.sjl.device.util;

import android.hardware.Camera;

import java.util.List;

/**
 * 获取摄像头像素值
 * 获取摄像头的像素值，通过获取相机设置中支持拍照的最大宽度和最大高度就可算到摄像头的像素值了，当然跟手机厂商标称的还是有差别的。比如标称200W的通过计算1200*1600=1920000=192W≈200W
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CameraUtils.java
 * @time 2018/8/29 9:44
 * @copyright(C) 2018 song
 */
public class CameraUtils {
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_NONE = 2;

    public static int hasBackCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 2;
    }

    public static int hasFrontCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return 2;
    }

    public static String getCameraPixels(int paramInt) {
        try {
            if (paramInt == 2)
                return "-";
            Camera localCamera = Camera.open(paramInt);
            Camera.Parameters localParameters = localCamera.getParameters();
            localParameters.set("camera-id", 1);
            List<Camera.Size> localList = localParameters.getSupportedPictureSizes();
            if (localList != null) {
                int heights[] = new int[localList.size()];
                int widths[] = new int[localList.size()];
                for (int i = 0; i < localList.size(); i++) {
                    Camera.Size size = localList.get(i);
                    int sizeHeight = size.height;
                    int sizeWidth = size.width;
                    heights[i] = sizeHeight;
                    widths[i] = sizeWidth;
                }
                int maxH = getMaxNumber(heights);
                int maxW = getMaxNumber(widths);
                int pixels = maxH * maxW;
                localCamera.release();
                return String.format("%.1f", (pixels / 10000 * 1.0)) + " 万像素 " + (maxH > maxW ? maxH : maxW) + "x" + (maxH > maxW ? maxW : maxH);

            } else return "-";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-";
    }

    public static int getMaxNumber(int[] paramArray) {
        int temp = paramArray[0];
        for (int i = 0; i < paramArray.length; i++) {
            if (temp < paramArray[i]) {
                temp = paramArray[i];
            }
        }
        return temp;
    }

}
