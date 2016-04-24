package com.seu.wufan.alumnicircle.common;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.injector.component.DaggerApiComponent;
import com.seu.wufan.alumnicircle.injector.component.DaggerAppComponent;
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
}
