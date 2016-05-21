package com.seu.wufan.alumnicircle.ui.widget.leancloud.event;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.PushService;
import com.seu.wufan.alumnicircle.ui.activity.login.WelcomeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzw on 15/6/11.
 */
public class PushManager {
  public final static String AVOS_ALERT = "alert";

  private final static String AVOS_PUSH_ACTION = "action";
  public static final String INSTALLATION_CHANNELS = "channels";
  private static PushManager pushManager;
  private Context context;

  public synchronized static PushManager getInstance() {
    if (pushManager == null) {
      pushManager = new PushManager();
    }
    return pushManager;
  }

  public void init(Context context) {
    this.context = context;
    PushService.setDefaultPushCallback(context, WelcomeActivity.class); //EntrySplashActivity
    subscribeCurrentUserChannel();
  }

  private void subscribeCurrentUserChannel() {
    String currentUserId = LeanchatUser.getCurrentUserId();
    if (!TextUtils.isEmpty(currentUserId)) {
      PushService.subscribe(context, currentUserId, WelcomeActivity.class);
    }
  }

  public void unsubscribeCurrentUserChannel() {
    String currentUserId = LeanchatUser.getCurrentUserId();
    if (!TextUtils.isEmpty(currentUserId)) {
      PushService.unsubscribe(context, currentUserId);
    }
  }

  public void pushMessage(String userId, String message, String action) {
    AVQuery query = AVInstallation.getQuery();
    query.whereContains(INSTALLATION_CHANNELS, userId);
    AVPush push = new AVPush();
    push.setQuery(query);

    Map<String, Object> dataMap = new HashMap<String, Object>();
    dataMap.put(AVOS_ALERT, message);
    dataMap.put(AVOS_PUSH_ACTION, action);
    push.setData(dataMap);
    push.sendInBackground();
  }
}