package com.dataexpo.lwsyspda.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.entity.Device;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeviceChoiceAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {

    public DeviceChoiceAdapter(int layoutResId, @Nullable List<Device> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Device item) {
        baseViewHolder.setText(R.id.tv_device_serics, item.getSeriesName())
                .setText(R.id.tv_device_number, item.getCode())
                .setText(R.id.tv_device_class, item.getClassName())
                .setText(R.id.tv_room_name, item.getHouseName())
                .setText(R.id.tv_series_name, item.getName())
                .setText(R.id.tv_room_remark, item.getRequestStatus() == 1 ? "正在请求服务器" :
                        (item.getRequestStatus() == 2 ? "服务器查询设备失败" :
                                item.getRequestStatus() == 4 ? "编码未绑定设备" : "备注：" + item.getRemark()))
                .setText(R.id.tv_device_rssi, item.getRssi())
                .setText(R.id.tv_room_status, item.getHouseType() == null ? "未知" :
                        (item.getHouseType().equals(0) ? "在仓" : "出仓"))
                .setText(R.id.tv_device_status, item.getHouseType() == null ? "未知" :
                        (item.getRepairType().equals(0) ? "正常" :
                        (item.getRepairType().equals(0) ? "待维修" : "返厂")))
                .setText(R.id.tv_series_scan_count, item.getScanCount() + "");
        if (item.isbAddWait()) {
            baseViewHolder.findView(R.id.iv_selector).setBackgroundResource(R.drawable.select_slod_blue);
        } else {
            baseViewHolder.findView(R.id.iv_selector).setBackgroundResource(R.drawable.select_hollow_blue);
        }
    }
}
