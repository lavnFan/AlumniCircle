package com.avoscloud.leanchatlib.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.avoscloud.leanchatlib.R;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.LogUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wli on 15/9/18.
 */
public class AVChatActivity extends AVBaseActivity {

  protected ChatFragment chatFragment;
  protected AVIMConversation conversation;
  protected Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    chatFragment = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
    initByIntent(getIntent());

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    initByIntent(intent);
  }

  private void initByIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (null != extras) {
      if (extras.containsKey(Constants.MEMBER_ID)) {
        getConversation(extras.getString(Constants.MEMBER_ID));
      } else if (extras.containsKey(Constants.CONVERSATION_ID)) {
        String conversationId = extras.getString(Constants.CONVERSATION_ID);
        updateConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId));
      } else {}
    }
  }

  protected void initActionBar(String title) {
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      if (title != null) {
        actionBar.setTitle(title);
      }
      actionBar.setDisplayUseLogoEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(true);
    } else {
      LogUtils.i("action bar is null, so no title, please set an ActionBar style for activity");
    }
  }

  protected void updateConversation(AVIMConversation conversation) {
    if (null != conversation) {
      this.conversation = conversation;
      chatFragment.setConversation(conversation);
      chatFragment.showUserName(ConversationHelper.typeOfConversation(conversation) != ConversationType.Single);
      initActionBar(ConversationHelper.titleOfConversation(conversation));
    }
  }

  /**
   * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
   * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
   */
  private void getConversation(final String memberId) {
    Map<String, Object> attrs = new HashMap<>();
    attrs.put(ConversationType.TYPE_KEY, ConversationType.Single.getValue());
    ChatManager.getInstance().createSingleConversation(memberId, new AVIMConversationCreatedCallback() {
      @Override
      public void done(AVIMConversation avimConversation, AVIMException e) {
        if (filterException(e)) {
          ChatManager.getInstance().getRoomsTable().insertRoom(avimConversation.getConversationId());
          updateConversation(avimConversation);
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}