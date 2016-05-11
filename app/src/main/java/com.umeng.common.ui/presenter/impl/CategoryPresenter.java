package com.umeng.common.ui.presenter.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Category;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.CategoryResponse;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpCategoryView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 15/11/24.
 */
public class CategoryPresenter extends BaseFragmentPresenter<List<Category>> {
    protected MvpCategoryView mvpCategoryView;

    private String mNextPageUrl = "";

    public CategoryPresenter(MvpCategoryView mvpCategoryView) {
        this.mvpCategoryView = mvpCategoryView;
    }

    @Override
    public void attach(Context context) {
        super.attach(context);

    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void loadDataFromServer() {

        mCommunitySDK.fetchCategory(new Listeners.FetchListener<CategoryResponse>() {

            @Override
            public void onStart() {
                mvpCategoryView.onRefreshStart();
            }

            @Override
            public void onComplete(final CategoryResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    mvpCategoryView.onRefreshEnd(); // [注意]:不可移动，该方法的回调会决定是否显示空视图
                    return;
                }
                final List<Category> results = response.result;
                updateNextPageUrl(response.nextPageUrl);
                dealNextPageUrl(response.nextPageUrl, true);
                fetchCategoryComplete(results, true);
                mvpCategoryView.onRefreshEnd();
            }
        });
    }

    protected void updateNextPageUrl(String newUrl) {
        if (TextUtils.isEmpty(CommConfig.getConfig().loginedUser.id)) {
            return;
        }
        List<Category> dataSource = mvpCategoryView.getBindDataSource();

        for (Category category : dataSource) {
            category.nextPage = newUrl;
        }
        mvpCategoryView.notifyDataSetChanged();
    }
    protected void dealNextPageUrl(String url,boolean fromRefresh){
        if ( fromRefresh && TextUtils.isEmpty(mNextPageUrl) ) {
            mNextPageUrl = url;
        } else if (!fromRefresh){
            mNextPageUrl = url;
        }
    }
    /**
     * 移除重复的话题</br>
     *
     * @param dest 目标话题列表。
     * @return
     */
    private List<Category> filterCategory(List<Category> dest) {
        List<Category> src = mvpCategoryView.getBindDataSource();
        src.removeAll(dest);
        return dest;
    }
    /**
     * 移除重复的话题</br>
     *
     * @param topics 目标话题列表。
     * @return
     */
//    private List<Topic> filterTopics(List<Topic> dest) {
//        List<Topic> src = mvpCategoryView.getBindDataSource();
//        src.removeAll(dest);
//        return dest;
//    }
    protected void fetchCategoryComplete(List<Category> topics, boolean fromRefersh) {
//        parseNextpageUrl(topics, fromRefersh);
        // 过滤已经存在的数据
        final List<Category> newTopics = filterCategory(topics);
        if (newTopics != null && newTopics.size() > 0) {
            // 添加新话题
            List<Category> dataSource = mvpCategoryView.getBindDataSource();
            if (fromRefersh) {
                dataSource.addAll(0, newTopics);
            } else {
                dataSource.addAll(newTopics);// 加载更多的数据追加到尾部
            }
            mvpCategoryView.notifyDataSetChanged();
            // 将新的话题数据插入到数据库中
            insertCategoryToDB(newTopics);
        }

    }
    /**
     * 检测是否登录并执行关注/取消关注操作</br>
     *
     * @param topic
     * @param toggleButton
     * @param isFollow
     */
    public void checkLoginAndExecuteOp(final Topic topic, final ToggleButton toggleButton,
                                       final boolean isFollow) {
        toggleButton.setClickable(false);
        CommonUtils.checkLoginAndFireCallback(mContext, new Listeners.SimpleFetchListener<LoginResponse>() {

            @Override
            public void onComplete(LoginResponse response) {
                if (response.errCode != ErrorCode.NO_ERROR) {
                    toggleButton.setChecked(!toggleButton.isChecked());
                    toggleButton.setClickable(true);
                    return;
                }
                if (isFollow) {
                    followTopic(topic, toggleButton);
                } else {
                    cancelFollowTopic(topic, toggleButton);
                }
            }
        });
    }
    /**
     * 取消关注某个话题</br>
     *
     * @param topic
     */
    public void cancelFollowTopic(final Topic topic, final ToggleButton toggleButton) {
        mCommunitySDK.cancelFollowTopic(topic,
                new Listeners.SimpleFetchListener<Response>() {

                    @Override
                    public void onComplete(Response response) {
                        toggleButton.setClickable(true);
                        if (NetworkUtils.handleResponseComm(response)) {
                            return;
                        }
                        if (response.errCode == ErrorCode.ORIGIN_TOPIC_DELETE_ERR_CODE) {
                            // 在数据库中删除该话题并Toast
                            deleteTopic(topic);
                            ToastMsg.showShortMsgByResName("umeng_comm__topic_has_deleted");
                            return;
                        }

                        if (response.errCode == ErrorCode.NO_ERROR) {
                            topic.isFocused = false;
                            // 将该记录从数据库中移除
                            DatabaseAPI.getInstance().getTopicDBAPI().deleteFollowedTopicByTopicId(topic.id);
                            BroadcastUtils.sendTopicCancelFollowBroadcast(mContext, topic);
                            List<Topic> topics = new ArrayList<Topic>();
                            topics.add(topic);
                            insertTopicsToDB(topics);
                        } else if (response.errCode == ErrorCode.ERROR_TOPIC_NOT_FOCUSED) {
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_has_not_focused");
                            toggleButton.setChecked(false);
                        } else {
                            toggleButton.setChecked(true);
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_cancel_failed");
                        }
                    }
                });
    }
    /**
     * 保存话题列表到数据库中,首先将话题本身的内容保存到topic表中,然后将话题与用户的关系( 关注与否 ) 插入到topic_user表中.
     *
     * @param topics 要保存的话题列表.
     */
    private void insertTopicsToDB(final List<Topic> topics) {
        DatabaseAPI.getInstance().getTopicDBAPI().saveTopicsToDB(topics);
        // 如果是已经关注那么将该条记录放到关系表中
        saveFollowedTopicInRelativeDB(topics);
    }
    /**
     * 保存已经关注的话题到关系表中
     */
    private void saveFollowedTopicInRelativeDB(List<Topic> topics) {
        List<Topic> tempList = new ArrayList<Topic>();
        for (Topic topicItem : topics) {
            if (topicItem.isFocused) {
                // 保存话题和用户之间的关系
                tempList.add(topicItem);
            }
        }

        CommUser user = CommConfig.getConfig().loginedUser;
        DatabaseAPI.getInstance().getTopicDBAPI().saveFollowedTopicsToDB(user.id, tempList);
    }
    /**
     * 删除话题。包括删除关系表跟话题本身，以及从adapter中删除</br>
     *
     * @param topic
     */
    private void deleteTopic(Topic topic) {
        DatabaseAPI.getInstance().getTopicDBAPI().deleteTopicFromDB(topic.id);
        // 从adapter删除该条topic
//        mRecommendTopicView.getBindDataSource().remove(topic);
//        mRecommendTopicView.notifyDataSetChanged();
    }

    /**
     * 关注某个话题</br>
     *
     * @param topic 话题的id
     */
    public void followTopic(final Topic topic, final ToggleButton toggleButton) {
        mCommunitySDK.followTopic(topic, new Listeners.SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if (NetworkUtils.handleResponseComm(response)) {
                    return;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    topic.isFocused = true;
                    // 存储到数据
                    List<Topic> topics = new ArrayList<Topic>();
                    topics.add(topic);
                    insertTopicsToDB(topics);
                    BroadcastUtils.sendTopicFollowBroadcast(mContext, topic);
                } else if (response.errCode == ErrorCode.ORIGIN_TOPIC_DELETE_ERR_CODE) {
                    // 在数据库中删除该话题并Toast
                    deleteTopic(topic);
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_has_deleted");
                } else if (response.errCode == ErrorCode.ERROR_TOPIC_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_has_focused");
                    toggleButton.setChecked(true);
                } else {
                    toggleButton.setChecked(false);
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_follow_failed");
                }
                toggleButton.setClickable(true);
            }
        });
    }
    protected void fetchTopicComplete(List<Topic> topics, boolean fromRefersh) {
//        parseNextpageUrl(topics, fromRefersh);
        // 过滤已经存在的数据
        final List<Topic> newTopics = topics;
       mvpCategoryView.ChangeAdapter(newTopics);

    }
    /**
     * 保存话题列表到数据库中,首先将话题本身的内容保存到topic表中,然后将话题与用户的关系( 关注与否 ) 插入到topic_user表中.
     *
     * @param topics 要保存的话题列表.
     */
    private void insertCategoryToDB(final List<Category> topics) {
//        DatabaseAPI.getInstance().getTopicDBAPI().saveTopicsToDB(Category);
//        // 如果是已经关注那么将该条记录放到关系表中
//        saveFollowedTopicInRelativeDB(topics);
    }
    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(mNextPageUrl))  {
            mvpCategoryView.onRefreshEnd();
            return ;
        }
        mCommunitySDK.fetchNextPageData(mNextPageUrl,CategoryResponse.class, new Listeners.FetchListener<CategoryResponse>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(CategoryResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    mvpCategoryView.onRefreshEnd();
                    return;
                }
                final List<Category> results = response.result;
                updateNextPageUrl(response.nextPageUrl);
                dealNextPageUrl(response.nextPageUrl, false);
                fetchCategoryComplete(results, false);
                mvpCategoryView.onRefreshEnd();
            }
        });
    }
    private Category findCategoryById(String id) {
        List<Category> dataSource = mvpCategoryView.getBindDataSource();
        for (Category category : dataSource) {
            if (category.id.equals(id)) {
                return category;
            }
        }
        return new Category();
    }
}
