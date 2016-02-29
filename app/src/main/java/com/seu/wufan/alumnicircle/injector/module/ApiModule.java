package com.seu.wufan.alumnicircle.injector.module;

import com.seu.wufan.alumnicircle.common.provider.GuestTokenProvider;
import com.seu.wufan.alumnicircle.model.TokenModel;
import com.seu.wufan.alumnicircle.model.UserModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = AppModule.class)
public class ApiModule {
    @Provides
    @Singleton
    TokenModel provideTokenModel(){
        return new TokenModel(new GuestTokenProvider());
    }

    @Provides
    @Singleton
    UserModel provideUserModel(){
        return new UserModel(new GuestTokenProvider());
    }

}
