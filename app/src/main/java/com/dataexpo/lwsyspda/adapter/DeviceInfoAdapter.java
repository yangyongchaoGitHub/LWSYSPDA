package com.dataexpo.lwsyspda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.holders.ChoiceListHolder;
import com.dataexpo.lwsyspda.adapter.holders.DeviceOprHolder;
import com.dataexpo.lwsyspda.common.Utils;
import com.dataexpo.lwsyspda.entity.DeviceUsingInfo;

import java.util.List;

public class DeviceInfoAdapter extends BaseAdapter {
    private Context mContext;
    List<DeviceUsingInfo> datas;
    public int type = 0;

    public DeviceInfoAdapter(Context context, List<DeviceUsingInfo> list) {
        this.mContext = context;
        this.datas = list;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DeviceOprHolder holder;
        if (convertView == null){
            holder = new DeviceOprHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_using, null);
            holder.tv_time = convertView.findViewById(R.id.tv_time);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_remark = convertView.findViewById(R.id.tv_remark);
            convertView.setTag(holder);
        } else {
            holder = (DeviceOprHolder) convertView.getTag();
        }
        DeviceUsingInfo dui = datas.get(position);
        holder.tv_time.setText( Utils.formatString(dui.getDate(), Utils.dateRulepit));
        holder.tv_name.setText(dui.getName());
        holder.tv_remark.setText(type == 0 ? dui.getBomName() : (
                type == 1 ? dui.getHouse() : dui.getRemark()
        ));
        return convertView;
    }
}
