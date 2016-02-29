package com.seu.wufan.alumnicircle.injector.component;

import com.seu.wufan.alumnicircle.common.Navigator;
import com.seu.wufan.alumnicircle.injector.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Navigator navigator();
}
