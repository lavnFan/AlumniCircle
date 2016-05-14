package com.seu.wufan.alumnicircle.ui.activity.me;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.EditInformationPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IEditInformationView;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.CompanyActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.JobActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.NameActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.PersonIntroActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.ProfExperActivity;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.fragment.MyFragment;
import com.seu.wufan.alumnicircle.ui.widget.expandtabview.view.ViewMiddle;
import com.seu.wufan.alumnicircle.ui.widget.province.bean.ProvinceBean;
import com.seu.wufan.alumnicircle.ui.widget.province.model.CityModel;
import com.seu.wufan.alumnicircle.ui.widget.province.model.DistrictModel;
import com.seu.wufan.alumnicircle.ui.widget.province.model.ProvinceModel;
import com.seu.wufan.alumnicircle.ui.widget.province.service.XmlParserHandler;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class EditInformationActivity extends BaseSwipeActivity implements IEditInformationView {

    @Inject
    EditInformationPresenter editInformationPresenter;

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.me_edit_info_photo_tr)
    TableRow mPhotoTr;
    @Bind(R.id.me_edit_info_name_tr)
    TableRow mNameTr;
    @Bind(R.id.me_edit_info_sex_tr)
    TableRow mSexTr;
    @Bind(R.id.me_edit_info_birth_date_tr)
    TableRow mBirthDateTr;
    @Bind(R.id.me_edit_info_work_city_tr)
    TableRow mWorkCityTr;
    @Bind(R.id.me_edit_info_job_prof_tr)
    TableRow mJobProfTr;
    @Bind(R.id.me_edit_info_company_tr)
    TableRow mCompanyTr;
    @Bind(R.id.me_edit_info_prof_tr)
    TableRow mProfTr;
    @Bind(R.id.me_edit_info_person_intro_tr)
    TableRow mPersonIntroTr;
    @Bind(R.id.me_edit_info_prof_exper_tr)
    TableRow mProfExperTr;
    @Bind(R.id.me_edit_info_educ_exper_tr)
    TableRow mEducExperTr;

    @Bind(R.id.me_edit_info_photo_cv)
    CircleImageView mPhotoCv;
    @Bind(R.id.me_edit_info_name_tv)
    TextView mNameTv;
    @Bind(R.id.me_edit_info_sex_tv)
    TextView mSexTv;
    @Bind(R.id.me_edit_info_birth_date_tv)
    TextView mBirthDateTv;
    @Bind(R.id.me_edit_info_work_city_tv)
    TextView mWorkCityTv;
    @Bind(R.id.me_edit_info_job_prof_tv)
    TextView mJobProfTv;
    @Bind(R.id.me_edit_info_company_tv)
    TextView mCompanyTv;
    @Bind(R.id.me_edit_info_job_tv)
    TextView mJobTv;
    @Bind(R.id.me_edit_info_person_intro_tv)
    TextView mPersonIntroTv;
    @Bind(R.id.me_edit_info_prof_exper_tv)
    TextView mProfExperTv;
    @Bind(R.id.me_edit_info_educ_exper_tv)
    TextView mEducExperTv;

    public final static String EXTRA_PHOTO_PATH = "photo_path";

    public final static int REQUESTCODE_Person_Intro = 0;
    public final static int REQUESTCODE_Company = 1;
    public final static int REQUESTCODE_Name = 2;
    public final static int REQUESTCODE_Job = 3;
    public final static int REQUESTCODE_Photo = 4;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;
    private ArrayList<ProvinceBean> options1Items = new ArrayList<ProvinceBean>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();


    //选择城市数据源
    protected String[] mProvinceDatas;  // 所有省
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();//key - 省 value - 市
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();//key - 区 values - 邮编
    protected String mCurrentProviceName;//当前省的名称
    protected String mCurrentCityName;
    protected String mCurrentDistrictName = ""; //当前区的名称
    protected String mCurrentZipCode = "";//当前区的邮政编码


    private PopupWindow popupWindow;
    private ViewMiddle viewMiddle;
    private EditInformationActivity mContext;
    private int displayWidth;
    private int displayHeight;

    private String photoPath;

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_information;
    }

    @Override
    protected void prepareDatas() {
        setToolbarTitle(getResources().getString(R.string.edit_information));
        mContext = this;
        displayWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        displayHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();

        getApiComponent().inject(this);
        editInformationPresenter.attachView(this);

    }

    @Override
    protected void initViewsAndEvents() {
        editInformationPresenter.init();

        initOptions();

        viewMiddle = new ViewMiddle(this);
        viewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {
            @Override
            public void getValue(String showText) {
                mJobProfTv.setText(showText);
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick({R.id.me_edit_info_photo_tr, R.id.me_edit_info_name_tr, R.id.me_edit_info_sex_tr,
            R.id.me_edit_info_birth_date_tr, R.id.me_edit_info_work_city_tr, R.id.me_edit_info_job_prof_tr,
            R.id.me_edit_info_company_tr, R.id.me_edit_info_prof_tr, R.id.me_edit_info_person_intro_tr,
            R.id.me_edit_info_prof_exper_tr, R.id.me_edit_info_educ_exper_tr})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.me_edit_info_photo_tr:
                //选择头像
                PhotoPickerIntent intent = new PhotoPickerIntent(EditInformationActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUESTCODE_Photo);
                break;
            case R.id.me_edit_info_name_tr:
                //修改姓名
                bundle.putSerializable(NameActivity.EXTRA_NAME, editInformationPresenter.getUserInfo());
                readyGoForResult(NameActivity.class, REQUESTCODE_Name, bundle);
                break;
            case R.id.me_edit_info_sex_tr:
                final String[] sexs = new String[]{"男", "女", "保密"};
                new AlertView(null, null, "取消", null, sexs, this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position != AlertView.CANCELPOSITION) {
                            TLog.i("sex selected:", sexs[position]);
                            mSexTv.setText(sexs[position]);
                        }
                    }
                }).setCancelable(true).show();
                break;
            case R.id.me_edit_info_birth_date_tr:
                //选择出生年月
                pvTime.show();
                break;
            case R.id.me_edit_info_work_city_tr:
                pvOptions.show();
                //选择工作城市
                break;
            case R.id.me_edit_info_job_prof_tr:
                //行业职能
                showPopupWindow();
                break;
            case R.id.me_edit_info_company_tr:
                //公司
                bundle.putString(CompanyActivity.EXTRA_COMPANY, mCompanyTv.getText().toString());
                readyGoForResult(CompanyActivity.class, REQUESTCODE_Company, bundle);
                break;
            case R.id.me_edit_info_prof_tr:
                //职位
                bundle.putString(JobActivity.EXTRA_JOB, mJobTv.getText().toString());
                readyGoForResult(JobActivity.class, REQUESTCODE_Job, bundle);
                break;
            case R.id.me_edit_info_person_intro_tr:
                //个人简介
                bundle.putString(PersonIntroActivity.EXTRA_PERSON_INTRO, mPersonIntroTv.getText().toString());
                readyGoForResult(PersonIntroActivity.class, REQUESTCODE_Person_Intro, bundle);
                break;
            case R.id.me_edit_info_prof_exper_tr:
                //职业经历
                readyGo(ProfExperActivity.class);
                break;
            case R.id.me_edit_info_educ_exper_tr:
                //教育经历
                break;
        }
    }

    private void initOptions() {
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR) - 60, calendar.get(Calendar.YEAR));
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mBirthDateTv.setText(getTime(date));
            }
        });

        //选项选择器
        pvOptions = new OptionsPickerView(this);
        intiPvOptions();  //初始化城市数据
        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, null, true);
        //设置选择的三级单位
//        pwOptions.setLabels("省", "市", "区");
        pvOptions.setTitle("选择城市");
        pvOptions.setCyclic(false, true, true);
        pvOptions.setCancelable(true);
        //设置默认选中的三级项目
        pvOptions.setSelectOptions(1, 1, 0);
        //监听确定选择按钮
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(option2);
                mWorkCityTv.setText(tx);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUESTCODE_Company:
                String company = (data == null) ? null : data.getStringExtra(CompanyActivity.EXTRA_COMPANY);
                if (company == null) {
                    mCompanyTv.setHint(R.string.please_write);
                }
                mCompanyTv.setText(company);
                break;
            case REQUESTCODE_Job:
                String job = (data == null) ? null : data.getStringExtra(JobActivity.EXTRA_JOB);
                if (job == null) {
                    mJobTv.setHint(R.string.please_write);
                }
                mJobTv.setText(job);
                break;
            case REQUESTCODE_Name:
                String name = (data == null) ? null : data.getStringExtra(NameActivity.EXTRA_NAME);
                if (name == null && mNameTv.getText().toString().isEmpty()) {
                    mNameTv.setHint(R.string.please_write);
                }
                if (name != null) {      //修改过名字
                    mNameTv.setText(name);
                    Bundle bundle = new Bundle();
                    bundle.putString(NameActivity.EXTRA_NAME, name);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(MyFragment.REQUESTCODE_Name, intent);
                }
                break;
            case REQUESTCODE_Person_Intro:
                String personInfo = (data == null) ? null : data.getStringExtra(PersonIntroActivity.EXTRA_PERSON_INTRO);
                if (personInfo == null) {
                    mPersonIntroTv.setHint(R.string.please_write);
                }
                mPersonIntroTv.setText(personInfo);
                break;
            case RESULT_OK:
                switch (requestCode) {
                    case REQUESTCODE_Photo:
                        if (data != null) {
                            ArrayList<Photo> photos =
                                    data.getParcelableArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                            TLog.i("PHOTO-INFO", "PHOTO:" + photos.get(0));
                            photoPath = photos.get(0).getPath();
                            beginCrop(Uri.fromFile(new File(photoPath)));
                        }
//                    Log.i("PHOTO-INFO", "PHOTO:" + data.getData());
//                    beginCrop(data.getData());
                        break;
                    case Crop.REQUEST_CROP:
                        handleCrop(resultCode, data);
                        break;
                }
                break;
        }
    }


    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void showPopupWindow() {
        popupWindow = new PopupWindow(viewMiddle, displayWidth, displayHeight, true);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(mToolbar, displayHeight / 4, displayWidth / 4);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), String.valueOf(new Date().getTime())));//删除缓存图片
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            photoPath = Crop.getOutput(result).getPath();
            CommonUtils.showCircleImageWithGlide(this, mPhotoCv, photoPath);
            editInformationPresenter.savePhoto(photoPath);
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtils.showToast(Crop.getError(result).getMessage(), this);//  失败考虑默认头像
        }
    }

    /**
     * 初始化城市数据
     */
    private void intiPvOptions() {
        initProvinceDatas();
        for (int i = 0; i < mProvinceDatas.length; i++) {
            options1Items.add(new ProvinceBean(i, mProvinceDatas[i], null, null));

            String[] cities = mCitisDatasMap.get(mProvinceDatas[i]);
            ArrayList<String> arrayCity = new ArrayList<String>();

            if (mProvinceDatas[i].equals("北京市") || mProvinceDatas[i].equals("上海市")
                    || mProvinceDatas[i].equals("重庆市") || mProvinceDatas[i].equals("天津市")) {
                String[] dist = mDistrictDatasMap.get(mProvinceDatas[i]);
                for (int j = 0; j < dist.length; j++) {
                    arrayCity.add(dist[j]);
                }
            } else {
                for (int j = 0; j < cities.length; j++) {
                    arrayCity.add(cities[j]);
                }
            }
            options2Items.add(arrayCity);
        }
    }

    /**
     * 解析省市区的XML数据
     */
    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    public void showNetCantUse() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showToast(@NonNull String s) {

    }

    @Override
    public void initUserInfo(UserInfoRes res) {
        mNameTv.setText(res.getName());
        CommonUtils.showCircleImageWithGlide(this, mPhotoCv, res.getImage());
    }

    @Override
    public void initDetail(UserInfoDetailRes res) {

    }

    @Override
    public void setPhotoResult(String photo_path) {
        Bundle bundle = new Bundle();
        bundle.putString(EditInformationActivity.EXTRA_PHOTO_PATH, photo_path);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(MyFragment.REQUESTCODE_PHOTO, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editInformationPresenter.destroy();
    }



}
