package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.utils.LocalCacheUtils;
import com.avoscloud.leanchatlib.utils.PathUtils;
import com.avoscloud.leanchatlib.view.PlayButton;

/**
 * Created by wli on 15/9/17.
 */
public class ChatItemAudioHolder extends ChatItemHolder {

  protected PlayButton playButton;
  protected TextView durationView;

  public ChatItemAudioHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    if (isLeft) {
      conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_audio_layout, null));
    } else {
      conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_right_audio_layout, null));
    }
    playButton = (PlayButton) itemView.findViewById(R.id.chat_item_audio_play_btn);
    durationView = (TextView) itemView.findViewById(R.id.chat_item_audio_duration_view);
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    if (o instanceof  AVIMAudioMessage) {
      AVIMAudioMessage audioMessage = (AVIMAudioMessage)o;
      durationView.setText(String.format("%.0f\"", audioMessage.getDuration()));
      String localFilePath = audioMessage.getLocalFilePath();
      if (!TextUtils.isEmpty(localFilePath)) {
        playButton.setPath(localFilePath);
      } else {
        String path = PathUtils.getChatFilePath(getContext(), audioMessage.getMessageId());
        playButton.setPath(path);
        LocalCacheUtils.downloadFileAsync(audioMessage.getFileUrl(), path);
      }
    }
  }
}