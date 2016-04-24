package com.seu.wufan.alumnicircle.injector.component;

import android.app.Activity;

import com.seu.wufan.alumnicircle.injector.module.ActivityModule;
import com.seu.wufan.alumnicircle.injector.scope.PerActivity;

import dagger.Component;

@PerActivity   //自定义的范围注解
@Component(
        dependencies = AppComponent.class,
        modules = ActivityModule.class
)
public interface ActivityComponent {
    Activity activity();
}
