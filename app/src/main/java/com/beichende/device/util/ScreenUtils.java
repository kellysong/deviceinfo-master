package com.beichende.device.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ScreenUtils.java
 * @time 2020/3/16 12:19
 * @copyright(C) 2020 song
 */
public class ScreenUtils {
    /**
     * 获取屏幕宽度
     *
     * @param context Context
     * @return 屏幕宽度（px）
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * 获取屏幕高度
     *
     * @param context Context
     * @return 屏幕高度（px）
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    /**
     * 获取SmallestWidthDP
     * @param context
     * @return
     */
    public static float getSmallestWidthDP(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int heightPixels =getScreenHeight(context);
        int widthPixels = getScreenWidth(context);
        float density = dm.density;
        float heightDP = heightPixels / density;
        float widthDP = widthPixels / density;
        float smallestWidthDP;
        if (widthDP < heightDP) {
            smallestWidthDP = widthDP;
        } else {
            smallestWidthDP = heightDP;
        }
        return smallestWidthDP;
    }



}
