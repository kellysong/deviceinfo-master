package com.sjl.device;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.opengl.GLSurfaceView;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.sjl.device.bean.NetworkInfo;
import com.sjl.device.util.BatteryUtils;
import com.sjl.device.util.CameraUtils;
import com.sjl.device.util.CpuUtils;
import com.sjl.device.util.DeviceIdUtils;
import com.sjl.device.util.LogUtils;
import com.sjl.device.util.NetworkInfoUtils;
import com.sjl.device.util.OsUtils;
import com.sjl.device.util.RamAndRomUtils;
import com.sjl.device.util.ScreenUtils;
import com.sjl.device.util.SensorUtils;
import com.sjl.device.util.SimulatorUtils;
import com.sjl.device.widget.GpuRenderer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BatInfoReceiver batInfoReceiver;

    private TelephonyManager telephonyManager;
    private WifiManager wifi;
    private Display display;
    private DisplayMetrics dm;
    //获取网络连接管理者
    private ConnectivityManager connectionManager;
    private int BatteryN;       //目前电量
    private String BatteryTechnology;//电池技术
    private int BatteryV;       //电池电压
    private double BatteryT;        //电池温度
    private String BatteryStatus;   //电池状态
    private String BatteryTemp;     //电池使用情况

    static ExecutorService executorService;
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isAdopt(this)) {
            Toast.makeText(this, "当前运行在模拟器，可能存在部分功能异常", Toast.LENGTH_SHORT).show();
        }
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(3);
        }


        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, permissions, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                try {
                    init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDenied(String permission) {
                LogUtils.i("拒绝权限：" + permission);
                if (permission.contains("SYSTEM_ALERT_WINDOW")) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                } else {
                    finish();
                }
            }
        });

    }

    /**
     * 禁止应用在模拟器上运行
     * 检测点：电池电量和温度（模拟器的电池伏数可以为0或者是1000，而温度一定是0。但是真机的是可变的）
     *
     * @param context
     * @return
     */
    public static boolean isAdopt(Context context) {
        if (SimulatorUtils.isSimulator(context)) {
            return true;
        }
        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = context.registerReceiver(null, intentFilter);
        int voltage = batteryStatusIntent.getIntExtra("voltage", 99999);
        int temperature = batteryStatusIntent.getIntExtra("temperature", 99999);
        if (((voltage == 0) && (temperature == 0))
                || ((voltage == 10000) && (temperature == 0))) {
            //这是通过电池的伏数和温度来判断是真机还是模拟器
            return true;
        } else {
            return false;
        }
    }


    private void init() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectionManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        display = getWindowManager().getDefaultDisplay();
        dm = getResources().getDisplayMetrics();
        //基本信息
        initBaseInfo();
        try {
            //CPU
            initCpuInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //存储（可用/总量）
        initMemoryInfo();

        //显示
        initShowInfo();

        //相机
        initCameraInfo();

        //传输
        initTransferInfo();
        //传感器
        initSensorInfo();
        //状态信息
        initStatusInfo();
        // 注册一个系统 BroadcastReceiver，作为访问电池信息之用，这个不能直接在AndroidManifest.xml中注册
        batInfoReceiver = new BatInfoReceiver();//不要用匿名内部类
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(batInfoReceiver, intentFilter);
    }

    /**
     * 初始化传感器信息
     */
    @DebugLog
    public void initSensorInfo() {
        String sensorInfo = SensorUtils.getDeviceSensorInfo(this);
        setTextViewScroll(R.id.sensorList);
        setEditText(R.id.sensorList, sensorInfo);


    }

    private void setTextViewScroll(int viewId) {
        TextView view = findViewById(viewId);
        view.setMovementMethod(ScrollingMovementMethod.getInstance());//textview滚动设置
        view.setOnTouchListener(new View.OnTouchListener() {//解决ScrollView与内部嵌套的TextView滚动冲突
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //通知父控件不要干扰
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    //通知父控件不要干扰
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

    @DebugLog
    public void initTransferInfo() {
        NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null) {
            setEditText(R.id.nfc, "支持");
        } else {
            setEditText(R.id.nfc, "不支持");
        }
    }

    /**
     * 初始化状态信息
     */
    @DebugLog
    public void initStatusInfo() {
        try {
            Class localClass = Class.forName("android.os.SystemProperties");
            Object localObject1 = localClass.newInstance();
            Object localObject3 = localClass.getMethod("get", new Class[]{String.class, String.class}).invoke(localObject1, new Object[]{"ro.build.display.id", ""});

            setEditText(R.id.osVersion, localObject3 + "");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //获取网络的状态信息，有下面三种方式
        android.net.NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            setEditText(R.id.lianwang, networkInfo.getType() + "");
            setEditText(R.id.lianwangname, networkInfo.getTypeName());
        } else {
            setEditText(R.id.lianwang, "无网络");
            setEditText(R.id.lianwangname, "--");
        }

        //下面只能获取正在使用的单卡信息
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            setEditText(R.id.imei, telephonyManager.getDeviceId());
            setEditText(R.id.imsi, telephonyManager.getSubscriberId());
            setEditText(R.id.number, telephonyManager.getLine1Number());//获取本机号码，不支持所有手机
            setEditText(R.id.simserial, telephonyManager.getSimSerialNumber());
            setEditText(R.id.simoperator, telephonyManager.getSimOperator());
            setEditText(R.id.simoperatorname, telephonyManager.getSimOperatorName());
            setEditText(R.id.simcountryiso, telephonyManager.getSimCountryIso());
            setEditText(R.id.workType, telephonyManager.getNetworkType() + "");
            setEditText(R.id.netcountryiso, telephonyManager.getNetworkCountryIso());
            setEditText(R.id.netoperator, telephonyManager.getNetworkOperator());
            setEditText(R.id.netoperatorname, telephonyManager.getNetworkOperatorName());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager  mSubscriptionManager = SubscriptionManager.from(this);
            List<SubscriptionInfo> mSubInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
            setTextViewScroll(R.id.other_sim);
            for (SubscriptionInfo info : mSubInfoList) {
                System.out.println(info.toString());
                if (!Objects.equals(info.getNumber(),telephonyManager.getLine1Number())){
                    setEditText(R.id.other_sim, info.toString());
                    break;
                }else {
                    setEditText(R.id.other_sim, "--");
                }
            }
        }


        setEditText(R.id.radiovis, android.os.Build.getRadioVersion());
        try {
            NetworkInfo deviceInfo = NetworkInfoUtils.getNetworkInfo(this);
            if (deviceInfo != null) {
                setEditText(R.id.ip, deviceInfo.getIp());
                setEditText(R.id.wifimac, deviceInfo.getMac());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setEditText(R.id.getssid, wifi.getConnectionInfo().getSSID());
        setEditText(R.id.getbssid, wifi.getConnectionInfo().getBSSID());
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            setEditText(R.id.bluemac, defaultAdapter
                    .getAddress());
            setEditText(R.id.bluname, defaultAdapter.getName());
        } else {
            setEditText(R.id.bluemac, "不支持");
            setEditText(R.id.bluname, "不支持");
        }

    }

    @DebugLog
    public void initShowInfo() {
        int densityDpi = dm.densityDpi;
        setEditText(R.id.content_wh, display.getWidth() + "*" + display.getHeight());
        setEditText(R.id.dpi, densityDpi + "");
        setEditText(R.id.density, dm.density + "");
        float smallestWidthDP = ScreenUtils.getSmallestWidthDP(this);
        setEditText(R.id.smallestWidth, smallestWidthDP + "(value-sw"+(int)Math.floor(smallestWidthDP)+"dp)");
        setEditText(R.id.resolutionQualifier, display.getWidth() + "x" + display.getHeight()+"(value-"+ display.getWidth() + "x" + display.getHeight()+")");
        setEditText(R.id.statusBarHeight, ScreenUtils.getStatusBarHeight(this) +"px");
        int navigationBarHeight = ScreenUtils.getNavigationBarHeight(this);
        setEditText(R.id.navigationBarHeight, navigationBarHeight > 0 ? navigationBarHeight +"px":"无");

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            setEditText(R.id.orientation, "横屏");
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            setEditText(R.id.orientation, "竖屏");
        }
        /**
         *        mdpi 120dpi~160dpi
         *         hdpi 160dpi~240dpi
         *         xhdpi 240dpi~320dpi
         *         xxhdpi 320dpi~480dpi
         *         xxxhdpi 480dpi~640dpi
         */
        String dir;
        if (densityDpi <= 120) {
            dir = "mipmap-ldpi(drawable-ldpi)";
        } else if (densityDpi <= 160) {
            dir = "mipmap-mdpi(drawable-mdpi)";
        } else if (densityDpi <= 240) {
            dir = "mipmap-hdpi(drawable-hdpi)";
        } else if (densityDpi <= 320) {
            dir = "mipmap-xhdpi(drawable-xhdpi)";
        } else if (densityDpi <= 480) {
            dir = "mipmap-xxhdpi(drawable-xxhdpi)";
        } else if (densityDpi <= 640) {
            dir = "mipmap-xxxhdpi(drawable-xxxhdpi)";
        } else {
            dir = "mipmap-xxxxhdpi(drawable-xxxxhdpi)";
        }
        setEditText(R.id.imgDir, dir);


        /**
         * getRealMetrics - 屏幕的原始尺寸，即包含状态栏。
         * version >= 4.2.2
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        }
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        setEditText(R.id.resolution, width + "*" + height);
        double x = Math.pow(width, 2);
        double y = Math.pow(height, 2);
        double diagonal = Math.sqrt(x + y);
        float xdpi = dm.xdpi;
        double screenInches = diagonal / (double) xdpi;
        DecimalFormat df = new DecimalFormat("#.0");
        setEditText(R.id.screenInches, df.format(screenInches));





        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        /*

        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
         */
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            //设置OpenGL ES实现GLSurfaceView背景透明
            GLSurfaceView mGLSurfaceView = findViewById(R.id.gl_view);
            mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            mGLSurfaceView.setZOrderOnTop(true);
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            final GpuRenderer gpuRenderer = new GpuRenderer();
            gpuRenderer.setGpuRendererCallback(new GpuRenderer.GpuRendererCallback() {
                @Override
                public void info(final String renderer, final String vendor, final String version, final String extensions) {

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTextViewScroll(R.id.gl_renderer);
                            setTextViewScroll(R.id.gl_extensions);

                            setEditText(R.id.gl_renderer, renderer);
                            setEditText(R.id.gl_vendor, vendor);
                            setEditText(R.id.gl_version, version);
                            setEditText(R.id.gl_extensions, extensions);
                        }
                    });
                }
            });
            mGLSurfaceView.setRenderer(gpuRenderer);
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since we're
             * not doing anything, the app will crash if the device doesn't
             * support OpenGL ES 2.0. If we publish on the market, we should
             * also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
        }

    }

    @DebugLog
    public void initMemoryInfo() {
        setEditText(R.id.ram, RamAndRomUtils.formatFileSize(RamAndRomUtils.getAvailableMemory(this), false, false) + " / " + RamAndRomUtils.formatFileSize(RamAndRomUtils.getTotalMemorySize(this), false, true));
        setEditText(R.id.rom, RamAndRomUtils.formatFileSize(RamAndRomUtils.getAvailableInternalMemorySize(), false, false) + " / " + RamAndRomUtils.formatFileSize(RamAndRomUtils.getTotalInternalMemorySize(), false, true));
        setEditText(R.id.sd_rom, RamAndRomUtils.formatFileSize(RamAndRomUtils.getAvailableExternalMemorySize(), false, false) + " / " + RamAndRomUtils.formatFileSize(RamAndRomUtils.getTotalExternalMemorySize(), false, true));
        setEditText(R.id.app_rom, RamAndRomUtils.getMemoryClass(this) + "M");

    }

    @DebugLog
    public void initCpuInfo() {
        setEditText(R.id.curCoreNum, Runtime.getRuntime().availableProcessors() + "");
        setEditText(R.id.cpu, CpuUtils.getCpuName());
        String abis;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            abis = Arrays.toString(Build.SUPPORTED_ABIS);
            setEditText(R.id.cpuabi, abis);
        } else {
            setEditText(R.id.cpuabi, "--");
        }
        setEditText(R.id.cpuFramework, CpuUtils.getCpuFramework());
        setEditText(R.id.cpuFreq, CpuUtils.getMinCpuFreq() + " ~ " + CpuUtils.getMaxCpuFreq());
        setEditText(R.id.curCpuFreq, CpuUtils.getCurCpuFreq());


    }

    @DebugLog
    public void initBaseInfo() {
        setEditText(R.id.brand, android.os.Build.BRAND);
        setEditText(R.id.model, android.os.Build.MODEL);
        setEditText(R.id.hardware, android.os.Build.HARDWARE);
        setEditText(R.id.board, android.os.Build.BOARD);
        setEditText(R.id.changshang, android.os.Build.MANUFACTURER);
        setEditText(R.id.android_id, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        setEditText(R.id.b_id, Build.ID);
        setEditText(R.id.gjtime, android.os.Build.TIME + "");
        setEditText(R.id.sdk_INT, Build.VERSION.SDK_INT + "");
        setEditText(R.id.serial, android.os.Build.SERIAL);
        setEditText(R.id.device, android.os.Build.DEVICE);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String archType = CpuUtils.getArchType(MainActivity.this);
                final String kernelVersion = OsUtils.getKernelVersion();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setEditText(R.id.release, "Android " + Build.VERSION.RELEASE + "(" + archType + "位)");
                        setTextViewScroll(R.id.kernelVersion);
                        setEditText(R.id.kernelVersion, kernelVersion);

                    }
                });
            }
        });

        final WebView webView = mWebView;
        if (webView != null) {
            showWebViewInfo(webView);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView = new WebView(MyApplication.getContext());
                    showWebViewInfo(mWebView);
                }
            }, 1500);
        }

        String uniquePsuedoID = DeviceIdUtils.getUniquePsuedoID();
        System.out.println(uniquePsuedoID);
        LogUtils.i("uniquePsuedoID:" + uniquePsuedoID);
        setEditText(R.id.uuid, uniquePsuedoID);

    }

    private void showWebViewInfo(WebView webView) {
        WebSettings settings = webView.getSettings();
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript，否则显示空白页面
        webView.getSettings().setJavaScriptEnabled(true);
        // 获取到UserAgentString
        String userAgent = settings.getUserAgentString();
        //可以自己修改UserAgent
//        webView.getSettings().setUserAgentString(userAgent+";beichende/paf");
//        userAgent = settings.getUserAgentString();
        setTextViewScroll(R.id.userAgent);
        setEditText(R.id.userAgent, userAgent);
    }

    static WebView mWebView;

    @DebugLog
    public void initCameraInfo() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String cameraPixels = CameraUtils.getCameraPixels(CameraUtils.hasBackCamera());
                final String cameraPixels1 = CameraUtils.getCameraPixels(CameraUtils.hasFrontCamera());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setEditText(R.id.back_camera, cameraPixels);
                        setEditText(R.id.front_camera, cameraPixels1);
                    }
                });
            }
        });


    }

    private void setEditText(int id, String s) {
        ((TextView) this.findViewById(id)).setText(s);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                init();
            }
        }
    }

    /* 创建广播接收器 */
    private final class BatInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()

            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                BatteryN = intent.getIntExtra("level", 0);    //目前电量（0~100）
                BatteryTechnology = intent.getStringExtra("technology");//电池技术
                BatteryV = intent.getIntExtra("voltage", 0);  //电池电压(mv)
                BatteryT = intent.getIntExtra("temperature", 0);  //电池温度(数值)
                double T = BatteryT / 10.0; //电池摄氏温度，默认获取的非摄氏温度值，需做一下运算转换

                switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        BatteryStatus = "充电状态";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        BatteryStatus = "放电状态";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        BatteryStatus = "未充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        BatteryStatus = "充满电";

                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        BatteryStatus = "未知道状态";
                        break;
                }

                switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        BatteryTemp = "未知错误";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        BatteryTemp = "状态良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        BatteryTemp = "电池没有电";

                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        BatteryTemp = "电池电压过高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        BatteryTemp = "电池过热";
                        break;
                }
                //初始化电池电信
                initBatteryInfo();


            }
        }
    }


    private void initBatteryInfo() {
        setEditText(R.id.batteryCapacity, BatteryUtils.getBatteryCapacity(this));
        setEditText(R.id.BatteryTechnology, BatteryTechnology);
        setEditText(R.id.BatteryV, String.valueOf((float) BatteryV / 1000) + "v");
        setEditText(R.id.BatteryN, BatteryN + "%");//电量
        setEditText(R.id.BatteryT, String.valueOf((float) BatteryT / 10) + "°C");//温度
        setEditText(R.id.BatteryStatus, BatteryTemp + "---" + BatteryStatus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i("===========onDestroy");
        if (batInfoReceiver != null) {
            this.unregisterReceiver(batInfoReceiver);
            batInfoReceiver = null;
        }
    }
}