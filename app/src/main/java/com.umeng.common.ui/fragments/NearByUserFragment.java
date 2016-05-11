package com.umeng.common.ui.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.FollowedUserAdapter;
import com.umeng.common.ui.adapters.NearByUserAdapter;
import com.umeng.common.ui.presenter.impl.NearbyUserPresenter;


/**
 * Created by pei on 16/3/22.
 */
public class NearByUserFragment extends FollowedUserFragment implements View.OnClickListener{

    private DialogInterface.OnDismissListener mBackListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_nearby_user"));
        initTitleView(mRootView);

    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_nearby_user_layout");
    }

    @Override
    protected FollowedUserAdapter createAdapter() {
        NearByUserAdapter adapter = new NearByUserAdapter(getActivity());
        adapter.setTargetClassName(mTargetClassName);
        return adapter;
    }

    @Override
    protected NearbyUserPresenter createPresenters() {
        String uid = getArguments().getString(Constants.USER_ID_KEY);
        return new NearbyUserPresenter(this, uid);
    }

    public static NearByUserFragment newNearbyUserFragment() {
        NearByUserFragment fragment = new NearByUserFragment();
        Bundle bundle = new Bundle();
        String loginedUId = CommConfig.getConfig().loginedUser.id;
        bundle.putString(Constants.USER_ID_KEY, loginedUId);
        fragment.mUserId = loginedUId;
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initTitleView(View rootView) {
        View saveBtn = rootView.findViewById(ResFinder.getId("umeng_comm_save_bt"));
        saveBtn.setVisibility(View.GONE);
        rootView.findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(this);
        TextView textView = (TextView) rootView.findViewById(ResFinder
                .getId("umeng_comm_setting_title"));
        textView.setText(ResFinder.getString("umeng_comm_nearby_user"));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        rootView.findViewById(ResFinder.getId("umeng_comm_title_bar_root"))
                .setBackgroundColor(Color.WHITE);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mBackListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ResFinder.getId("umeng_comm_setting_back")){
            if(mBackListener != null){
                mBackListener.onDismiss(null);
            }
        }
    }
}
