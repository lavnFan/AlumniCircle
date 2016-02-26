package com.seu.wufan.alumnicircle.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.activity.circle.PublishDynamicActivity;
import com.seu.wufan.alumnicircle.ui.activity.contacts.AddFriendsActivity;
import com.seu.wufan.alumnicircle.ui.fragment.circle.CircleFragment;
import com.seu.wufan.alumnicircle.ui.fragment.ContactsFragment;
import com.seu.wufan.alumnicircle.ui.fragment.MyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.main_view_pager)
    ViewPager mViewPager;
    @Bind(R.id.circle_ll)
    LinearLayout mCircleLl;
    @Bind(R.id.circle_cv)
    CircleImageView mCircleCv;
    @Bind(R.id.circle_tv)
    TextView mCircleTv;
    @Bind(R.id.contacts_ll)
    LinearLayout mContactsLl;
    @Bind(R.id.contacts_cv)
    CircleImageView mContactsCv;
    @Bind(R.id.contacts_tv)
    TextView mContactsTv;
    @Bind(R.id.me_ll)
    LinearLayout mMyLl;
    @Bind(R.id.me_cv)
    CircleImageView mMyCv;
    @Bind(R.id.me_tv)
    TextView mMyTv;

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentStatePagerAdapter mAdapter;
    private int mCurrentIndex = 0;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        initView();
        initDatas();
        initListeners();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    private void initView() {
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);


    }

    private void initDatas() {
        mFragments.add(new CircleFragment());
        mFragments.add(new ContactsFragment());
        mFragments.add(new MyFragment());
        mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        if (null != mFragments) {
            mViewPager.setOffscreenPageLimit(mFragments.size());
            mViewPager.setAdapter(mAdapter);
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        setCircleAlpha(1);
                        setContactsAlpha(0);
                        setMyAlpha(0);
                        break;
                    case 1:
                        setCircleAlpha(0);
                        setContactsAlpha(1);
                        setMyAlpha(0);
                        break;
                    case 2:
                        setCircleAlpha(0);
                        setContactsAlpha(0);
                        setMyAlpha(1);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mCurrentIndex = 0;
                        break;
                    case 1:
                        mCurrentIndex = 1;
                        break;
                    case 2:
                        mCurrentIndex = 2;
                        break;
                }
                invalidateOptionsMenu();  //重新加载menu item
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initListeners() {
        mCircleLl.setOnClickListener(listeners);
        mContactsLl.setOnClickListener(listeners);
        mMyLl.setOnClickListener(listeners);
    }

    private View.OnClickListener listeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.circle_ll:
                    mViewPager.setCurrentItem(0, false);
                    setCircleAlpha(1);
                    setContactsAlpha(0);
                    setMyAlpha(0);
                    break;
                case R.id.contacts_ll:
                    mViewPager.setCurrentItem(1, false);
                    setCircleAlpha(0);
                    setContactsAlpha(1);
                    setMyAlpha(0);
                    break;
                case R.id.me_ll:
                    mViewPager.setCurrentItem(2, false);
                    setCircleAlpha(0);
                    setContactsAlpha(0);
                    setMyAlpha(1);
                    break;
            }
        }
    };

    public void setCircleAlpha(int i) {
        if (i == 1) {
            mCircleCv.setImageResource(R.drawable.circle_selected);
            mCircleTv.setTextColor(getResources().getColor(R.color.main_text_selected));
        } else {
            mCircleCv.setImageResource(R.drawable.circle_default);
            mCircleTv.setTextColor(getResources().getColor(R.color.main_text_normal));
        }
    }

    private void setContactsAlpha(int i) {
        if (i == 1) {
            mContactsCv.setImageResource(R.drawable.contacts_selected);
            mContactsTv.setTextColor(getResources().getColor(R.color.main_text_selected));
        } else {
            mContactsCv.setImageResource(R.drawable.contacts_default);
            mContactsTv.setTextColor(getResources().getColor(R.color.main_text_normal));
        }
    }

    private void setMyAlpha(int i) {
        if (i == 1) {
            mMyCv.setImageResource(R.drawable.my_selected);
            mMyTv.setTextColor(getResources().getColor(R.color.main_text_selected));
        } else {
            mMyCv.setImageResource(R.drawable.my_default);
            mMyTv.setTextColor(getResources().getColor(R.color.main_text_normal));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addItem = menu.findItem(R.id.add);
        MenuItem editItem = menu.findItem(R.id.edit);
        switch (mCurrentIndex) {
            case 0:
                editItem.setVisible(true);
                addItem.setVisible(false);
                mToolbarTv.setText(getResources().getText(R.string.alumni_circle));
                break;
            case 1:
                editItem.setVisible(false);
                addItem.setVisible(true);
                mToolbarTv.setText(getResources().getText(R.string.contacts));
                break;
            case 2:
                editItem.setVisible(false);
                addItem.setVisible(false);
                mToolbarTv.setText(getResources().getText(R.string.alumni_circle));
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                readyGo(AddFriendsActivity.class);
                break;
            case R.id.edit:
                readyGo(PublishDynamicActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
