package com.seu.wufan.alumnicircle.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.avos.avoscloud.AVOSCloud;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.injector.component.DaggerApiComponent;
import com.seu.wufan.alumnicircle.injector.component.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;

import com.seu.wufan.alumnicircle.injector.component.ApiComponent;
import com.seu.wufan.alumnicircle.injector.component.AppComponent;
import com.seu.wufan.alumnicircle.injector.module.AppModule;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.socialize.PlatformConfig;

public class App extends Application {
    private AppComponent appComponent;

    private ApiComponent apiComponent;

    private AppModule appModule;

    private static Context context;

    CommunitySDK mCommSDK = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
//        Logger.init()
//            .logLevel(LogLevel.FULL)
//            .hideThreadInfo()
//            .methodCount(0);

        appModule = new AppModule(this);
        initInjectorApp();
        initInjectorApi();
        App.context = getApplicationContext();

        initialLeanCloud();
        initUmeng();
    }

    private void initUmeng() {
        // 1、初始化友盟微社区
        mCommSDK = CommunityFactory.getCommSDK(this);
        mCommSDK.initSDK(this);

        //设置分享与授权
        PlatformConfig.setWeixin("wxbe28d07d528214c7", "3195ff722f13374da86c7a5b490909f8");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        // QQ和Qzone appid appkey
    }

    private void initInjectorApp(){
        appComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .build();
    }

    private void initInjectorApi(){
        apiComponent = DaggerApiComponent.builder()
                .appModule(appModule)
                .build();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }

    public ApiComponent getApiComponent(){
        return apiComponent;
    }

    public static Context getContext(){
        return context;
    }

    private void initialLeanCloud() {
        AVOSCloud.initialize(this, "B7C5Nlqk7d3rWuEQpbOi1SQf-gzGzoHsz", "sE2U68oiN4ItV3pNCan53F1Y");
        ChatManager.setDebugEnabled(true);// tag leanchatlib
        AVOSCloud.setDebugLogEnabled(true);  // set false when release
        initImageLoader(this);
        ChatManager.getInstance().init(this);
//        ThirdPartUserUtils.setThirdPartUserProvider(new CustomUserProvider());
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                //.memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}
