package com.seu.wufan.alumnicircle.injector.component;

import com.seu.wufan.alumnicircle.injector.module.ApiModule;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    void inject(LoginActivity loginActivity);

}
