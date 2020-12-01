package com.sjl.device;

import android.app.Application;
import android.webkit.WebView;

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
    static WebView webView;

    @Override
    public void onCreate() {
        super.onCreate();

        webView = new WebView(this);
    }

    public static WebView getWebView() {
        return webView;
    }
}
