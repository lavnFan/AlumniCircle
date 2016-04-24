package com.seu.wufan.alumnicircle.injector.module;

import com.seu.wufan.alumnicircle.common.provider.GuestTokenProvider;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//所有带有@Provides注解的方法都需要被封装到带有@Module注解的类中
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

    @Provides
    @Singleton
    CircleModel provideCircleModel(){
        return new CircleModel(new GuestTokenProvider());
    }

    @Provides
    @Singleton
    ContactsModel providerContactsModel(){
        return new ContactsModel(new GuestTokenProvider());
    }
}
