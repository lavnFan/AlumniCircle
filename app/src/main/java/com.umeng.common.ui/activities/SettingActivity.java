/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.common.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.dialogs.ClipImageDialog;
import com.umeng.common.ui.mvpview.MvpUserProfileSettingView;
import com.umeng.common.ui.presenter.impl.UserSettingPresenter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.widgets.RoundImageView;
import com.umeng.common.ui.widgets.SwitchButton;


/**
 * 设置页面 注意：此Activity的名字不能修改，数据层需要回调此Activity
 */
//// TODO: 16/1/25 第一次登录逻辑跳转到话题界面有问题 
public class SettingActivity extends BaseFragmentActivity implements OnClickListener,MvpUserProfileSettingView {

    private TextView mTitleTextView;
//    private SettingFragment mSettingFragment = new SettingFragment();
//    private UserSettingFragment mUserSettingFragment;
//    private PushSettingFragment mPushSettingFragment;
    private Button mSaveButton;
//    private Bundle mExtra;
//    public Fragment mCurrentFragment;
    private ClipImageDialog mClipImageDialog;
//    protected FragmentManager mFragmentManager = null;
//    public boolean isRegisterUserNameInvalid = false;//注册时，昵称是否无效
//    // 由于开发者可能直接使用Fragment，在退出登录的时候，我们需要回到该Activity
//    private String mContainerClass = null;
    private CommUser user;
    private EditText nikcnameEdit;
    private TextView genderEdit;
    private SwitchButton switchButton;
    private Button logoutButton;
    private RoundImageView headicon;
    private CommUser.Gender mGender;
    private Dialog mDialog;
    private CommConfig mSDKConfig = CommConfig.getConfig();
    private ProgressDialog mProgressDialog;
    private UserSettingPresenter mPresenter;
    private boolean isFirst = false;
    private boolean isRegisterUserNameInvalid = false;
    /**
     * Fragment的parent view,即Fragment的容器
     */
    protected int mFragmentContainer;
    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        setContentView(ResFinder.getLayout("umeng_comm_setting_activity"));
//        mContainerClass = getIntent().getExtras().getString(Constants.TYPE_CLASS);
//        mSettingFragment.setContainerClass(mContainerClass);
//        mFragmentManager = getSupportFragmentManager();

        mPresenter = new UserSettingPresenter(this,this);
        judgeIsFirst();
        mProgressDialog = new ProgressDialog(this);
        initViews();
//        initFragment();
//        switchHeaderView();
    }
    public void judgeIsFirst(){
        Bundle mExtra =  getIntent().getExtras();
        if (mExtra != null && mExtra.containsKey(Constants.USER_SETTING)) {
            isFirst = true;
            user = mExtra.getParcelable(Constants.USER);
            isRegisterUserNameInvalid = mExtra.getBoolean(Constants.REGISTER_USERNAME_INVALID);
            mPresenter.setFirstSetting(true);
        } else {
           isFirst = false;
            user = CommonUtils.getLoginUser(this);
        }
    }
    /**
     * 初始化相关View</br>
     */
    private void initViews() {
        findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(this);
        nikcnameEdit = (EditText)findViewById(ResFinder.getId("setting_username"));
        genderEdit = (TextView)findViewById(ResFinder.getId("setting_gender"));
        switchButton = (SwitchButton)findViewById(ResFinder.getId("umeng_common_switch_button"));
        logoutButton = (Button)findViewById(ResFinder.getId("setting_loginout"));
        nikcnameEdit.setHint(user.name);
        nikcnameEdit.setText(user.name);
        headicon = (RoundImageView)findViewById(ResFinder.getId("user_head_icon"));
//        if (TextUtils.isEmpty(user.iconUrl)){
//            headicon.setImageDrawable(ResFinder.getDrawable(""));
//        }else {
        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
            headicon.setImageUrl(user.iconUrl,option);
//        }

        headicon.setOnClickListener(this);
        genderEdit.setHint(user.gender.toString().equals("male") ? ResFinder.getString("umeng_comm_male") :ResFinder.getString("umeng_comm_female"));
        genderEdit.setOnClickListener(this);
        switchButton.setChecked(mSDKConfig.isPushEnable(this));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 保存配置
                CommConfig.getConfig().setSDKPushable(SettingActivity.this, isChecked);
            }
        });
        mTitleTextView = (TextView) findViewById(ResFinder.getId("umeng_comm_setting_title"));
        mTitleTextView.setText(ResFinder.getString("umeng_comm_setting"));
        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunitySDKImpl.getInstance().logout(SettingActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        BroadcastUtils.sendUserLogoutBroadcast(getApplication());
                        finish();
                    }
                });
            }
        });
        if(isFirst){
            logoutButton.setVisibility(View.GONE);
        }
        mSaveButton = (Button) findViewById(ResFinder.getId("umeng_comm_save_bt"));
        mSaveButton.setVisibility(View.VISIBLE);
        mSaveButton.setText(ResFinder.getString("umeng_comm_save"));
        mSaveButton.setOnClickListener(this);
        mGender = user.gender;
//        mSaveButton = (Button) findViewById(ResFinder.getId("umeng_comm_save_bt"));
//        mSaveButton.setText(ResFinder.getString("umeng_comm_save"));
//        mSaveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//        mSaveButton.setOnClickListener(this);
    }

    /**
     * 从相册中选择头像</br>
     */
    private void selectProfile() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/png;image/jpeg");
        startActivityForResult(pickImageIntent, Constants.PIC_SELECT);
    }
    /**
     * 根据性别切换用户默认头像。该行为仅仅发生在用户没有头像的情况下</br>
     *
     * @param gender 用户性别
     */
    private void changeDefaultIcon(CommUser.Gender gender) {
        mGender = gender;

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_setting_back")) { // 返回事件
            hideInputMethod();
            dealBackLogic();
        } else if (id == ResFinder.getId("umeng_comm_save_bt")) { // 保存事件
            dealSaveLogic();
        } else if (id == ResFinder.getId("user_head_icon")){
            if (isRegisterUserNameInvalid) {
                ToastMsg.showShortMsgByResName("umeng_comm_before_save");
            } else {
                selectProfile();
            }
        }else if (id == ResFinder.getId("setting_gender")){
            showGenderDialog();
        }else if (id == ResFinder.getId("setting_gender")){
            showGenderDialog();
        }else if (id == ResFinder.getId("setting_gender")){
            showGenderDialog();
        }else if(id == ResFinder.getId("umeng_comm_gender_textview_male")){
            String maleStr = ResFinder.getString("umeng_comm_male");
            genderEdit.setText(maleStr);
            changeDefaultIcon(CommUser.Gender.MALE);
            closeDialog();
        }else if(id == ResFinder.getId("umeng_comm_gender_textview_femal")){
            String femalStr = ResFinder.getString("umeng_comm_female");
            genderEdit.setText(femalStr);
            closeDialog();
            changeDefaultIcon(CommUser.Gender.FEMALE);

        }
    }
    private void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
    /**
     * 显示选择性别的Dialog</br>
     */
    private void showGenderDialog() {
        int style = ResFinder.getStyle("customDialog");
        int layout = ResFinder.getLayout("umeng_comm_gender_select");
        int femalResId = ResFinder.getId("umeng_comm_gender_textview_femal");
        int maleResId = ResFinder.getId("umeng_comm_gender_textview_male");
        mDialog = new Dialog(this, style);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(layout,
                null, false);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        view.findViewById(femalResId).setOnClickListener(this);
        view.findViewById(maleResId).setOnClickListener(this);
        mDialog.show();
    }
    public void hideInputMethod() {
     hideInputMethod(nikcnameEdit);
    }

    @Override
    protected void onDestroy() {
        hideInputMethod();
        super.onDestroy();
    }

    /**
     * 处理保存事件的逻辑。如果当前fragment是用户设置页面，则执行更新用户接口</br>
     */
    private void dealSaveLogic() {
       registerOrUpdateUserInfo();
        // showFragment(mSettingFragment);
    }

    /**
     * 处理back事件的逻辑。如果当前页面已经是设置页面，则直接返回(finish);否则回退到设置页面</br>
     */
    private void dealBackLogic() {
        finish();
    }
    /**
     * 检查检查昵称、年龄数据是否正确</br>
     *
     * @return
     */
    private boolean checkData() {
        String name = nikcnameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)&&isFirst&&!isRegisterUserNameInvalid){
            name = user.name;
        }
        if (TextUtils.isEmpty(name)) {
            ToastMsg.showShortMsgByResName("umeng_comm_user_center_no_name");
            return false;
        }
            return true;
//        boolean result = CommonUtils.isUserNameValid(name);
//        if (!result) {
//            ToastMsg.showShortMsgByResName("umeng_comm_user_name_tips");
//        }
//        return result;
    }
    public void registerOrUpdateUserInfo() {
        boolean flag = checkData();
        if (!flag) {
            return;
        }

        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(ResFinder.getString("umeng_comm_update_user_info"));
        if (isRegisterUserNameInvalid) {
            register();
        } else {
            updateUserInfo();
        }


    }
    private void register() {
        user.name = nikcnameEdit.getText().toString().trim();
        user.gender = mGender;
        mPresenter.register(user);
    }
    /**
     * 更新用户信息</br>
     */
    private void updateUserInfo() {

        final CommUser newUser = CommConfig.getConfig().loginedUser;
        if (TextUtils.isEmpty(nikcnameEdit.getText().toString())){
            newUser.name = nikcnameEdit.getHint().toString();
        }else {
            newUser.name = nikcnameEdit.getText().toString();
        }


        newUser.gender = mGender;

        mPresenter.updateUserProfile(newUser);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 防止在选择图片的时候按返回键
        if (data == null) {
            return;
        }
        // 从相册中选择图片
        if (requestCode == Constants.PIC_SELECT) {
            int style = ResFinder.getStyle("umeng_comm_dialog_fullscreen");
            // 显示剪切图片的Dialog
            mClipImageDialog = new ClipImageDialog(this, data.getData(), style);
            mClipImageDialog.setOnClickSaveListener(mOnSaveListener);
            mClipImageDialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 剪切图片dialog页面，点击保存时的回调，更新UI</br>
     */
    private ClipImageDialog.OnClickSaveListener mOnSaveListener = new ClipImageDialog.OnClickSaveListener() {

        @Override
        public void onClickSave(Bitmap bitmap) {
            headicon.setImageBitmap(bitmap);
        }
    };

    @Override
    public void showLoading(boolean isShow) {
        if (isShow) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }
}
