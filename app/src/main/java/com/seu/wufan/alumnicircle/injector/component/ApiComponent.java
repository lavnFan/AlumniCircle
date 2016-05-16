package com.seu.wufan.alumnicircle.injector.component;

import com.seu.wufan.alumnicircle.injector.module.ApiModule;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.activity.circle.PublishDynamicActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.RegisterActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.WelcomeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.SettingSwipeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.CompanyActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.EducationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.JobActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.NameActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.PersonIntroActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.ProfExperShowJobFragmentToActivity;
import com.seu.wufan.alumnicircle.ui.fragment.ContactsFragment;
import com.seu.wufan.alumnicircle.ui.fragment.MyFragment;
import com.seu.wufan.alumnicircle.ui.fragment.circle.CircleFragment;
import com.seu.wufan.alumnicircle.ui.fragment.me.EducationEditFragment;
import com.seu.wufan.alumnicircle.ui.fragment.me.ProfExperEditFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {

    void inject(LoginActivity loginActivity);

    void inject(RegisterActivity registerActivity);

    void inject(MainActivity mainActivity);

    void inject(WelcomeActivity welcomeActivity);

    void inject(SettingSwipeActivity settingActivity);

    void inject(PublishDynamicActivity publishDynamicActivity);

    void inject(MyInformationActivity myInformationActivity);

    void inject(EditInformationActivity editInformationActivity);

    void inject(NameActivity nameActivity);

    void inject(CompanyActivity companyActivity);

    void inject(JobActivity jobActivity);

    void inject(PersonIntroActivity personIntroActivity);

    void inject(ProfExperEditFragment profExperEditFragment);

    void inject(ProfExperShowJobFragmentToActivity profExperShowFragmentToActivity);

    void inject(EducationActivity educationActivity);

    void inject(EducationEditFragment educationEditFragment);

    void inject(CircleFragment circleFragment);

    void inject(ContactsFragment contactsFragment);

    void inject(MyFragment myFragment);


}
