package com.dataexpo.lwsyspda.adapter.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.R;

public class ChoiceListHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public TextView tv_bom_name;
    public TextView tv_bom_reg_time;
    public TextView tv_bom_reg_user;
    public TextView tv_bom_status;
    public TextView tv_bom_type;
    public TextView tv_device_rssi;
    public ConstraintLayout root;

    public ChoiceListHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        root = itemView.findViewById(R.id.item_choice_root);
        tv_bom_name = itemView.findViewById(R.id.tv_bom_name);
        tv_bom_reg_time = itemView.findViewById(R.id.tv_bom_reg_time);
        tv_bom_reg_user = itemView.findViewById(R.id.tv_bom_reg_user);
        tv_bom_status = itemView.findViewById(R.id.tv_bom_status);
        tv_bom_type = itemView.findViewById(R.id.tv_bom_type);
        tv_device_rssi = itemView.findViewById(R.id.tv_device_rssi);
    }
}
