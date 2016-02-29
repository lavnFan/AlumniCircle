package com.seu.wufan.alumnicircle.injector.module;

import android.app.Activity;

import com.seu.wufan.alumnicircle.injector.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity){
        this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity provideActivity(){
        return activity;
    }
}
