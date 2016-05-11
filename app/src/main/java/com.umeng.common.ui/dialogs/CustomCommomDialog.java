package com.umeng.common.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.umeng.socialize.common.ResContainer;

/**
 * Created by wangfei on 16/3/24.
 */
public class CustomCommomDialog extends Dialog {
    public CustomCommomDialog(Context context, String strMessage) {
        this(context, ResContainer.getResourceId(context, "style", "CommonProgressDialog"), strMessage);
    }

    public CustomCommomDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(ResContainer.getResourceId(context,"layout","umeng_comm_waitdialog"));
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView tvMsg = (TextView) this.findViewById(ResContainer.getResourceId(context,"id","tv_loadingmsg"));
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus) {
            dismiss();
        }
    }

}
