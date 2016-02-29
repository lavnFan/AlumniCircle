package com.seu.wufan.alumnicircle.common;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Navigator {

    @Inject
    public Navigator(){}

    public void navigateToLoginActivity(@NonNull Context context){
        Intent intent = LoginActivity.getCallingIntent(context);
        context.startActivity(intent);
    }

}
