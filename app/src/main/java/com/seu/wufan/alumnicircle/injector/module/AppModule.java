package com.seu.wufan.alumnicircle.injector.module;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.App;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.UploadImageUntil;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App app;

    public AppModule(App app){
        this.app = app;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideAppContext(){
        return app;
    }

    @Provides
    @Singleton
    PreferenceUtils providePreferenceUtils(@ForApplication Context context){
        return new PreferenceUtils(context);
    }

    @Provides
    @Singleton
    UploadImageUntil provideUploadImageUntil(){
        return new UploadImageUntil();
    }
}
