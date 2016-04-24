package com.seu.wufan.alumnicircle.injector.component;

import com.seu.wufan.alumnicircle.injector.module.ApiModule;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.RegisterActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.WelcomeActivity;
import com.seu.wufan.alumnicircle.ui.fragment.circle.CircleFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    void inject(LoginActivity loginActivity);

    void inject(RegisterActivity registerActivity);

    void inject(MainActivity mainActivity);

    void inject(WelcomeActivity welcomeActivity);

}
