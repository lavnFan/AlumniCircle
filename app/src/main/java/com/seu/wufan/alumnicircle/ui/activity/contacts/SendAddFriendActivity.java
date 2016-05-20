package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.FriendReq;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.SendFriendRequestPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISendFriendRequestView;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/5/20
 */
public class SendAddFriendActivity extends BaseSwipeActivity implements ISendFriendRequestView{

    @Bind(R.id.send_add_friend_msg_et)
    EditText mMsgEt;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;

    @Inject
    SendFriendRequestPresenter sendFriendRequestPresenter;

    public static final String EXTRA_OTHER_ID="other_id";
    public static final String EXTRA_MY_ITEM = "my_item";
    private String other_id = null;
    private FriendReq req = new FriendReq();

    @Override
    protected void prepareDatas() {
        mToolbarRightTv.setText(R.string.send);
        mToolbarRightTv.setVisibility(View.VISIBLE);
        getApiComponent().inject(this);
        sendFriendRequestPresenter.attachView(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_send_add_friend_message;
    }

    @Override
    protected void initViewsAndEvents() {
        other_id = getIntent().getExtras().getString(EXTRA_OTHER_ID);
        String name = getIntent().getExtras().getString(EXTRA_MY_ITEM);
        String sendMsg = "你好，我是"+name+"，请求添加你为好友！";
        mMsgEt.setText(sendMsg);

        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                req.setAdd_friend_text(mMsgEt.getText().toString());
                sendFriendRequestPresenter.sendFriendRequest(other_id,req);
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(this);
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(this);
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s,this);
    }

    @Override
    public void sendRequstSuccess() {
        showToast("发送成功，等待对方验证");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendFriendRequestPresenter.destroy();
    }
}
