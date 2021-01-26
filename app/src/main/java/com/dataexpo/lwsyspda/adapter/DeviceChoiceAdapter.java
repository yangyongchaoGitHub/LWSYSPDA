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
        baseViewHolder.setText(R.id.tv_device_name_value, item.getClassName())
                .setText(R.id.tv_device_class_value, item.getName());
    }
}
