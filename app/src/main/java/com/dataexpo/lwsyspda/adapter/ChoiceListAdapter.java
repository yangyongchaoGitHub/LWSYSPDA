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
        holder.tv_bom_name.setText(mList.get(position).getName());
        holder.tv_bom_reg_time.setText(Utils.formatDatetoString(mList.get(position).getRegTime()));
        holder.tv_bom_reg_user.setText(mList.get(position).getRegName());

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
