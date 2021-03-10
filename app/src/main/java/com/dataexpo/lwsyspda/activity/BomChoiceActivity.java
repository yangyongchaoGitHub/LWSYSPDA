package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.ChoiceListAdapter;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.listener.OnItemClickListener;
import com.dataexpo.lwsyspda.retrofitInf.BomService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BomChoiceActivity extends BascActivity implements OnItemClickListener, View.OnClickListener {
    private static final String TAG = SelectActivity.class.getSimpleName();
    private Context mContext;

    private TextView tv_0;
    private TextView tv_1;
    private TextView tv_2;
    private RecyclerView r_centerView;
    private ChoiceListAdapter adapter;
    private List<Bom> dataList = new ArrayList<>();

    Retrofit mRetrofit;

    private int type = 0;

    String currCall = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        initView();
        initData();
    }

    private void initData() {
        adapter = new ChoiceListAdapter(mContext);
        r_centerView.setAdapter(adapter);

        adapter.setItemClickListener(this);
        getBomList(null, 1);
        tv_0.setBackgroundResource(R.drawable.edittext_rect_dark_blue);
        tv_0.setTextColor(mContext.getResources().getColor(R.color.bg_white));
        type = 0;
    }

    private void getBomList(Integer loginId, Integer overStatus) {
        tv_0.setBackgroundResource(R.drawable.edittext_rect_gray);
        tv_1.setBackgroundResource(R.drawable.edittext_rect_gray);
        tv_2.setBackgroundResource(R.drawable.edittext_rect_gray);
        tv_0.setTextColor(mContext.getResources().getColor(R.color.bg_black));
        tv_1.setTextColor(mContext.getResources().getColor(R.color.bg_black));
        tv_2.setTextColor(mContext.getResources().getColor(R.color.bg_black));

        BomService bomService = mRetrofit.create(BomService.class);

        //查询项目单
        Call<NetResult<List<Bom>>> call = bomService.getBomList(1, 10, null, type, null, loginId, overStatus);

        currCall = call.hashCode() + "";

        call.enqueue(new Callback<NetResult<List<Bom>>>() {
            @Override
            public void onResponse(Call<NetResult<List<Bom>>> call, Response<NetResult<List<Bom>>> response) {
                //已经不是当前的请求
                if (!currCall.equals(call.hashCode() + "")) {
                    return;
                }

                NetResult<List<Bom>> result = response.body();
                if (result == null) {
                    return;
                }
                Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " +
                        result.getErrcode() + " " + result.getData().size());
                if (result.getErrcode() != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dataList = result.getData();
                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NetResult<List<Bom>>> call, Throwable t) {
                //已经不是当前的请求
                if (!currCall.equals(call.hashCode() + "")) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "获取数据失败,请检查网络或服务器数据异常",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        r_centerView.setLayoutManager(layoutManager);
        tv_0 = findViewById(R.id.tv_0);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);

        tv_0.setOnClickListener(this);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG, "onItemClick " + position + " " + dataList.get(position).getId());
        Intent intent = null;
        if (type == 0) {
            intent = new Intent(mContext, BomInfoActivity.class);
        } else if (type == 1) {
            intent = new Intent(mContext, InboundChoiceActivity.class);
        } else if (type == 2) {
            intent = new Intent(mContext, DeviceChoiceActivity.class);
        }
        if (intent != null) {
            Bundle bundle = new Bundle();
            //传递name参数为tinyphp
            bundle.putSerializable("bom", dataList.get(position));
            bundle.putInt("bomId", dataList.get(position).getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_0:
                //type = 0;
                getBomList(null, 1);
                tv_0.setBackgroundResource(R.drawable.edittext_rect_dark_blue);
                tv_0.setTextColor(mContext.getResources().getColor(R.color.bg_white));
//                tv_0.setBackgroundResource(R.drawable.edittext_rect_white);
//                tv_1.setBackground(null);
//                tv_2.setBackground(null);
                break;
            case R.id.tv_1:
                //type = 1;
                getBomList(null, 0);
                tv_1.setBackgroundResource(R.drawable.edittext_rect_dark_blue);
                tv_1.setTextColor(mContext.getResources().getColor(R.color.bg_white));
//                tv_1.setBackgroundResource(R.drawable.edittext_rect_white);
//                tv_0.setBackground(null);
//                tv_2.setBackground(null);
                break;
            case R.id.tv_2:
                //type = 2;
                getBomList(MyApplication.getMyApp().getCallContext().getLoginId(), null);
                tv_2.setBackgroundResource(R.drawable.edittext_rect_dark_blue);
                tv_2.setTextColor(mContext.getResources().getColor(R.color.bg_white));
//                tv_2.setBackgroundResource(R.drawable.edittext_rect_white);
//                tv_1.setBackground(null);
//                tv_0.setBackground(null);
                break;
            default:
        }
    }
}
