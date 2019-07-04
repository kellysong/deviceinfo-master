package com.beichende.device.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SensorUtils.java
 * @time 2018/8/29 15:16
 * @copyright(C) 2018 song
 */
public class SensorUtils {
    /**
     * 获取设备支持的所有传感器
     *
     * @param context
     */
    public static String getDeviceSensorInfo(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        if (sensorList != null && !sensorList.isEmpty()) {
            LogUtils.i("传感器个数：" + sensorList.size());
            StringBuilder sb = new StringBuilder();
            for (Sensor sensor : sensorList) {
                String type;
                switch (sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        type = "加速度传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        type = "磁场传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        type = "方向传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        type = "陀螺仪传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_LIGHT:
                        type = "光线传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_PRESSURE:
                        type = "压力传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_TEMPERATURE:
                        type = "温度传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        type = "接近传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_GRAVITY:
                        type = "重力传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        type = "线性加速度传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        type = "旋转矢量传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_RELATIVE_HUMIDITY:
                        type = "相对湿度传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_AMBIENT_TEMPERATURE:
                        type = "环境温度传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                        type = "磁场传感器(未经校准)";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        type = "游戏旋转矢量传感器";
                        break;
                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                        type = "陀螺仪传感器(未经校准)";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_SIGNIFICANT_MOTION:
                        type = "特殊动作触发传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_STEP_DETECTOR:
                        type = "步数探测传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_STEP_COUNTER:
                        type = "步数计数传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                        type = "地磁旋转矢量传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_HEART_RATE:
                        type = "心率传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_POSE_6DOF:
                        type = "POSE_6DOF传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_STATIONARY_DETECT:
                        type = "静止检测传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_MOTION_DETECT:
                        type = "运动检测传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_HEART_BEAT:
                        type = "心跳传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                        type = "低延迟身体检测传感器";
                        sb.append(type + "、");
                        break;
                    case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                        type = "加速度传感器(未经校准)";
                        sb.append(type + "、");
                        break;
                    default:
                        type = sensor.getName();
                        sb.append(type + "、");
                        break;
                }
            }
           return  sb.deleteCharAt(sb.length() - 1).toString();
        }
        return "无";
    }
}
