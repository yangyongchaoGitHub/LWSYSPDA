package com.dataexpo.lwsyspda.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.activity.SelectActivity;
import com.dataexpo.lwsyspda.adapter.holders.ChoiceListHolder;
import com.dataexpo.lwsyspda.common.Utils;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.listener.OnItemClickListener;

import java.util.List;

public class ChoiceListAdapter extends RecyclerView.Adapter<ChoiceListHolder> implements View.OnClickListener{
    private static final String TAG = ChoiceListAdapter.class.getSimpleName();
    private List<Bom> mList;
    private OnItemClickListener mItemClickListener;
    private Context mContext;

    public ChoiceListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<Bom> list) {
        mList = list;
    }

    @Override
    public void onClick(View v) {
//            if (mItemClickListener != null) {
//                mItemClickListener.onItemClick(v, (Integer) v.getTag());
//            }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ChoiceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_choice, parent, false);
        ChoiceListHolder viewHolder = new ChoiceListHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceListHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceListHolder holder, final int position) {
        holder.itemView.setTag(position);
        // 添加数据
        Bom bom = mList.get(position);
        holder.tv_bom_name.setText(bom.getName());
        holder.tv_bom_reg_time.setText("下单时间：" + Utils.formatDatetoString(bom.getRegTime()));
        holder.tv_bom_reg_user.setText("下单人：" + bom.getRegName());

        Log.i(TAG, "status is ： " + bom.getStatus());

        if (bom.getType().equals(0)) {
            holder.tv_bom_type.setText("项目单");
            holder.tv_bom_type.setTextColor(mContext.getResources().getColor(R.color.font_blue));
        } else if (bom.getType().equals(1)) {
            holder.tv_bom_type.setText("借货单");
            holder.tv_bom_type.setTextColor(mContext.getResources().getColor(R.color.font_green));
        } else if (bom.getType().equals(2)) {
            holder.tv_bom_type.setText("维修单");
            holder.tv_bom_type.setTextColor(mContext.getResources().getColor(R.color.font_red));
        }

        if (bom.getStatus().equals(0)) {
            holder.tv_bom_status.setText("未选设备");
            holder.tv_bom_status.setTextColor(mContext.getResources().getColor(R.color.font_blue));
        } else if (bom.getStatus().equals(1)) {
            holder.tv_bom_status.setText("未备货");
            holder.tv_bom_status.setTextColor(mContext.getResources().getColor(R.color.font_org));
        } else if (bom.getStatus().equals(2)) {
            holder.tv_bom_status.setText("已备货");
            holder.tv_bom_status.setTextColor(mContext.getResources().getColor(R.color.font_green));
        } else if (bom.getStatus().equals(3)) {
            holder.tv_bom_status.setText("已修改");
            holder.tv_bom_status.setTextColor(mContext.getResources().getColor(R.color.font_red));
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!= null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }
}
