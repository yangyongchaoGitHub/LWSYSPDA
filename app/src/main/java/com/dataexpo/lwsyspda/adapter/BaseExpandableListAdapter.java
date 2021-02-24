package com.dataexpo.lwsyspda.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;

import java.util.ArrayList;

public class BaseExpandableListAdapter extends android.widget.BaseExpandableListAdapter {
    private static final String TAG = BaseExpandableListAdapter.class.getName();
    private ExpandableListView expandableListView;
    private ArrayList<BomHouseInfo> gData;
    private ArrayList<ArrayList<Device>> iData;
    private Context mContext;
    Handler handler;

    public BaseExpandableListAdapter(ArrayList<BomHouseInfo> gData,ArrayList<ArrayList<Device>> iData, Context mContext,
                                     ExpandableListView expandableListView) {
        this.expandableListView = expandableListView;
        this.gData = gData;
        this.iData = iData;
        this.mContext = mContext;
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };
    }

    public void ref(ArrayList<BomHouseInfo> timeList, ArrayList<ArrayList<Device>> mSystemMsgInfos){
        gData = timeList;
        iData = mSystemMsgInfos;
    }

    /*供外界更新数据的方法*/
    public void refresh(ExpandableListView mExpandableListView, ArrayList<?> timeList){
        handler.sendMessage(new Message());
        //必须重新伸缩之后才能更新数据
        for (int i = 0; i < timeList.size(); i++) {
            mExpandableListView.collapseGroup(i);
        }
        for (int i = 0; i < timeList.size(); i++) {
            mExpandableListView.expandGroup(i);
        }
    }

    @Override
    public int getGroupCount() {
        return gData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (iData.size() > groupPosition) {
            return iData.get(groupPosition).size();
        }
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return gData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return iData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.expd_device_group, parent, false);
            groupHolder = new ViewHolderGroup();
            groupHolder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_group_name);
            groupHolder.tv_group_selector = (TextView) convertView.findViewById(R.id.tv_group_selector);
            groupHolder.iv_group_icon = convertView.findViewById(R.id.iv_group_icon);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        BomHouseInfo bomHouseInfo = gData.get(groupPosition);
        groupHolder.tv_group_name.setText(bomHouseInfo.getClassName());
        groupHolder.tv_group_selector.setText(iData.get(groupPosition).size() + "/" + bomHouseInfo.getClassNum());

        if (isExpanded) {
            groupHolder.iv_group_icon.setImageResource(R.drawable.expanding_icon);
        } else {
            groupHolder.iv_group_icon.setImageResource(R.drawable.collapsing_icon);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.expd_device_item, parent, false);
            itemHolder = new ViewHolderItem();
            //itemHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            itemHolder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            itemHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            itemHolder.tv_serial = (TextView) convertView.findViewById(R.id.tv_serial);
            itemHolder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            itemHolder.tv_show = (TextView) convertView.findViewById(R.id.tv_show);
            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ViewHolderItem) convertView.getTag();
        }
        Device device = iData.get(groupPosition).get(childPosition);
        itemHolder.tv_number.setText(childPosition + "");
        itemHolder.tv_name.setText(device.getName());
        if (device.getRepairType().equals(0)) {
            itemHolder.tv_serial.setTextColor(mContext.getResources().getColor(R.color.bg_black));
        } else {
            itemHolder.tv_serial.setTextColor(mContext.getResources().getColor(R.color.font_red));
        }
        itemHolder.tv_serial.setText(device.getCode());

        itemHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BaseExpa", "onClick " + groupPosition + " || " + childPosition);
                iData.get(groupPosition).remove(childPosition);
                refresh(expandableListView, gData);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolderGroup{
        private TextView tv_group_name;
        private TextView tv_group_selector;
        private ImageView iv_group_icon;
    }

    private static class ViewHolderItem{
        private ImageView img_icon;
        private TextView tv_number;
        private TextView tv_name;
        private TextView tv_serial;
        private TextView tv_delete;
        private TextView tv_show;
    }
}