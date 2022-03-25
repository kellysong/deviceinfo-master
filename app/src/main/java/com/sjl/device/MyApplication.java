package com.sjl.device;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

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
        //Bugly_v3.4.4
        CrashReport.initCrashReport(getApplicationContext(), "e6ee273eff", false);
    }

    public static Context getContext() {
        return context;
    }
}
