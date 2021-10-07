package com.sjl.device;

import android.app.Application;
import android.content.Context;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyApplication
 * @time 2020/12/1 17:21
 * @copyright(C) 2020 song
 */
public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
