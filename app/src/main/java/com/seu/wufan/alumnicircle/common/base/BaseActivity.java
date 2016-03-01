package com.seu.wufan.alumnicircle.common.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.App;
import com.seu.wufan.alumnicircle.common.Navigator;
import com.seu.wufan.alumnicircle.injector.component.ApiComponent;
import com.seu.wufan.alumnicircle.injector.component.AppComponent;
import com.seu.wufan.alumnicircle.injector.module.ActivityModule;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @Bind(R.id.tl_custom)
    Toolbar mToolbar;

    @Nullable
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTitleTv;

    @Nullable
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;

    public Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getContentView()!=0){
            setContentView(getContentView());
        }
        ButterKnife.bind(this);
        navigator = ((App)getApplication()).getAppComponent().navigator();
        initToolBar();
        prepareData();
        initViewsAndEvents();
    }

    protected  abstract void prepareData();

    protected abstract @LayoutRes int getContentView();

    protected abstract void initViewsAndEvents();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initToolBar() {
        if(mToolbar==null) return;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void setToolbarTitle(String title){
        if(mToolbar == null){
            return;
        }
        mToolbarTitleTv.setText(title);
        mToolbarTitleTv.setVisibility(View.VISIBLE);
    }

    protected void setToolbarRightTitle(String rightTitle){
        if(mToolbar == null){
            return;
        }
        mToolbarRightTv.setText(rightTitle);
        mToolbarRightTv.setVisibility(View.VISIBLE);
    }

    protected void setToolbarBackHome(boolean enabled){
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(enabled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        getSupportActionBar().setDisplayShowTitleEnabled(enabled);
    }

    public AppComponent getAppComponent(){
        return ((App)getApplication()).getAppComponent();
    }

    public ApiComponent getApiComponent(){
        return ((App)getApplication()).getApiComponent();
    }

    public ActivityModule getActivityModule(){
        return new ActivityModule(this);
    }

    /**
     * startActivity
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    /**
     * startActivity then finish
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        finish();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
