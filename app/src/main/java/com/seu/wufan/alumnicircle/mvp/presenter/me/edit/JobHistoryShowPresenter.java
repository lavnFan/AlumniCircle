package com.seu.wufan.alumnicircle.mvp.presenter.me.edit;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.JobRes;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IJobShowView;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.ProfExperShowJobFragmentToActivity;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/15
 */
public class JobHistoryShowPresenter implements IJobHistoryShowPresenter {
    private IJobShowView iJobShowView;
    private Subscription subscription;
    private Subscription updateSubscription;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private Context appContext;

    @Inject
    public JobHistoryShowPresenter(@ForApplication Context context, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void saveJob(final Job job) {
        if (NetUtils.isNetworkConnected(appContext)) {
            subscription = userModel.addJobHistory(job)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<JobRes>() {
                        @Override
                        public void call(JobRes jobRes) {
                            iJobShowView.backEdit(job, ProfExperShowJobFragmentToActivity.REQUEST_ADD);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iJobShowView.showToast(exception.getMessage());
                            } else {
                                iJobShowView.showNetError();
                            }
                        }
                    });
        } else {
            iJobShowView.showNetCantUse();
        }
    }

    @Override
    public void updateJob(String id, final Job job) {
        if (NetUtils.isNetworkConnected(appContext)) {
            updateSubscription = userModel.updateJobHistory(id, job)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            TLog.i("TAG","update job success!");
                            iJobShowView.backEdit(job, ProfExperShowJobFragmentToActivity.REQUEST_UPDATE);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iJobShowView.showToast(exception.getMessage());
                            } else {
                                iJobShowView.showNetError();
                            }
                        }
                    });
        } else {
            iJobShowView.showNetCantUse();
        }
    }

    @Override
    public void attachView(IView v) {
        iJobShowView = (IJobShowView) v;
    }

    @Override
    public void destroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}