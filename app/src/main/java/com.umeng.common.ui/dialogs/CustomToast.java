package com.umeng.common.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.comm.core.utils.ResFinder;

import org.w3c.dom.Text;

/**
 * Created by wangfei on 16/3/22.
 */
public class CustomToast {
    public static void showTopicDefaultMsg(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(ResFinder.getLayout("umeng_comm_toast_topic_default"), null);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showTopicDefaultMsg(Context context, String tips) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(ResFinder.getLayout("umeng_comm_toast_topic_default"), null);
        TextView tipsTextView = (TextView)layout.findViewById(ResFinder.getId("umeng_comm_warn_msg"));
        tipsTextView.setText(tips);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
