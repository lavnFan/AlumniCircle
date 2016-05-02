package com.seu.wufan.alumnicircle.ui.widget.qrcode.dwj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultActivity extends AppCompatActivity {

    @Bind(R.id.result_content)
    TextView mResultContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_result);
        ButterKnife.bind(this);
        setTitle("扫描结果");
        Bundle extras = getIntent().getExtras();
        mResultContent.setText(extras.getString("result"));
    }
}
