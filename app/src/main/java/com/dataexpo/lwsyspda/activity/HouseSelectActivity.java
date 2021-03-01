package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dataexpo.lwsyspda.R;

public class HouseSelectActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = HouseSelectActivity.class.getSimpleName();
    private Context mContext;

    private TextView tv_0;
    private TextView tv_1;
    private TextView tv_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_house_select);

        initView();
    }

    private void initView() {
        tv_0 = findViewById(R.id.tv_0);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);

        tv_0.setOnClickListener(this);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, InboundChoiceActivity.class);
        switch (v.getId()) {
            case R.id.tv_0:
                intent.putExtra("roomId", 0);
                break;

            case R.id.tv_1:
                intent.putExtra("roomId", 1);
                break;

            case R.id.tv_2:
                intent.putExtra("roomId", 2);
                break;

            default:
        }
        startActivity(intent);
    }
}
