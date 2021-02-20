package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dataexpo.lwsyspda.R;

public class SelectActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = SelectActivity.class.getSimpleName();
    private Context mContext;

    private TextView tv_choice;
    private TextView tv_inbound;
    private TextView tv_call_repairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_model_select);

        initView();
    }

    private void initView() {
        tv_choice = findViewById(R.id.tv_model_choice);
        tv_inbound = findViewById(R.id.tv_model_inbound);
        tv_call_repairs = findViewById(R.id.tv_model_call_repairs);

        tv_choice.setOnClickListener(this);
        tv_inbound.setOnClickListener(this);
        tv_call_repairs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_model_choice:
                //备货
                startActivity(new Intent(mContext, ChoiceActivity.class));
                break;

            case R.id.tv_model_inbound:
                startActivity(new Intent(mContext, InboundChoiceActivity.class));
                break;

            case R.id.tv_model_call_repairs:
                break;

            default:
        }
    }
}
