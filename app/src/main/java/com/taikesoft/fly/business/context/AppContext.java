package com.taikesoft.fly.business.context;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.os.StatFs;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.taikesoft.fly.BuildConfig;
import com.taikesoft.fly.business.base.BaseApplication;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.retrofit.Engine;
import com.taikesoft.fly.business.retrofit.MyInterceptor;
import com.taikesoft.fly.business.retrofit.ResetInterceptor;
import com.taikesoft.fly.business.storage.ACache;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.webapi.ApiHttpClient;

import org.json.JSONObject;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContext extends BaseApplication implements UncaughtExceptionHandler {

    private static AppContext instance;
    private static String PREF_NAME = "fly_pref";
    private static ACache mCache;
    private UncaughtExceptionHandler mDefaultHandler;
    private AsyncHttpClient client;
    private Engine apiService;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 20;
    private static final int WRITE_TIMEOUT = 20;
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        initWebApi();
        initCache();
        initDefaultDate();
        //URI问题
        initUri();
    }

    private void initUri() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
    // 封装rxjava
    public void doHttpRequest(Observable pObservable, Observer observer) {
        Observable observable = pObservable ;
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);                       // unchecked generify
    }
    public void reinitWebApi() {
        ApiHttpClient.resetHttpClient();
    }
    public void initRetrofit() {
        //OKHttp进行超时设置
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS); // 连接超时时间阈值
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);   // 数据获取时间阈值
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);  // 写数据超时时间阈值

        builder.retryOnConnectionFailure(true);              //错误重连

        // Debug时才设置Log拦截器，才可以看到
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                    // 添加json数据拦截
                    new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            if (TextUtils.isEmpty(message)) return;
                            //如果收到响应是json才打印
                            String s = message.substring(0, 1);
                            if ("{".equals(s) || "[".equals(s)) {
                                Log.i("收到响应:", message);
                            }
                        }
                    }
            );
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        String loginStatus = SharedPreferencesManager.getString(ResultStatus.LOGIN_STATUS);
        if (loginStatus == null || loginStatus.equals(ResultStatus.UNLOGIN)) {
            builder.addInterceptor(new MyInterceptor());
        } else {
            // 设置请求头 也是通过拦截器
            builder.addInterceptor(new ResetInterceptor());
        }

        // 创建okhttpClient 将builder建立
        OkHttpClient okHttpClient = builder.build();

        // 2 创建 Retrofit实例
        String baseUrl = AppConfig.WEBSERVICE_URL;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)         //  *** baseUrl 中的路径(baseUrl)必须以 / 结束 ***
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        // 3 创建接口的代理对象
        apiService = retrofit.create(Engine.class);
    }

    private void initDefaultDate() {

    }
    public Engine getApiService() {
        return apiService;
    }
    @Override
    protected void init() {
        super.init();
        instance = this;
        AppConfig.init(this);
    }

    public void initCache() {
        mCache = ACache.get(this);
    }

    private void initWebApi() {
        client = new AsyncHttpClient();
        ApiHttpClient.setHttpClient(client);
    }

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    private void initImageLoader(Context context) {
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(displayOptions).build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static AppContext getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref;
    }

    public static ACache getACache() {
        return mCache;
    }


    /**
     * 检查当前网络是否可用
     *
     * @return
     */

    public static boolean isNetworkAvailable() {
        Context context = instance.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication
                .context().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 根据路径获取内存状态
     *
     * @param path
     * @return
     */
    public static String getMemoryInfo(File path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSizeLong(); // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCountLong(); // 获得扇区的总数

        long availableBlocks = stat.getAvailableBlocksLong(); // 获得可用的扇区数量

        // 总空间
        String totalMemory = Formatter.formatFileSize(AppContext.mContext,
                totalBlocks * blockSize);
        // 可用空间
        String availableMemory = Formatter.formatFileSize(AppContext.mContext,
                availableBlocks * blockSize);

        return "手机剩余空间 " + availableMemory + "/ " + totalMemory;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) instance.getSystemService(Context.TELEPHONY_SERVICE);
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            String imei = telephonyManager.getDeviceId();
            JSONObject obj = new JSONObject();
            try {
    			/*obj.put("imei", imei);
    			HttpEntity entity = new StringEntity(WebUtils.produceRequestParams(obj.toString()), "UTF-8");
    			UserApi.postLogin(instance, entity,
    					new AsyncHttpResponseHandler() {
    						@Override
    						public void onSuccess(int arg0, Header[] arg1,byte[] arg2) {
    							String httpResult = new String(arg2);
    							TLog.log("loginRes:" + httpResult);
    							try {
    								RetVal retVal = new Gson().fromJson(httpResult,RetVal.class);
    								if (StringUtils.equalsIgnoreCase(retVal.getReturnCode(), "S000A000")) {
    									if (StringUtils.equalsIgnoreCase(retVal.getReturnDesc(), "ok")) {
    										Intent intent = new Intent(instance,CollectSysHomePageActivity.class);// HomePageFragmentActivity
    										startActivity(intent);
    									} else {
    										AppContext.showToast(retVal.getReturnDesc());
    									}
    								} else {
    									AppContext.showToast(retVal.getReturnDesc());
    								}
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
    						@Override
    						public void onFailure(int arg0, Header[] arg1,
    								byte[] arg2, Throwable arg3) {
    							AppContext.showToast("网路异常");
    						}
    					});*/
            } catch (Exception e) {
                e.printStackTrace();
                android.os.Process.killProcess(android.os.Process.myPid());//干掉当前程序
            }
        }

    }
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        return true;
    }
}
