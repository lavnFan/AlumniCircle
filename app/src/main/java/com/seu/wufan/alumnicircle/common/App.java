package com.seu.wufan.alumnicircle.common;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationEventHandler;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.injector.component.DaggerApiComponent;
import com.seu.wufan.alumnicircle.injector.component.DaggerAppComponent;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.CustomUserProvider;
import com.squareup.leakcanary.LeakCanary;

import com.seu.wufan.alumnicircle.injector.component.ApiComponent;
import com.seu.wufan.alumnicircle.injector.component.AppComponent;
import com.seu.wufan.alumnicircle.injector.module.AppModule;

public class App extends Application {
    private AppComponent appComponent;

    private ApiComponent apiComponent;

    private AppModule appModule;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Logger.init()
            .logLevel(LogLevel.FULL)
            .hideThreadInfo()
            .methodCount(0);

        appModule = new AppModule(this);
        initInjectorApp();
        initInjectorApi();
        App.context = getApplicationContext();

        initialLeanCloud();
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
        ThirdPartUserUtils.setThirdPartUserProvider(new CustomUserProvider());
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

}
