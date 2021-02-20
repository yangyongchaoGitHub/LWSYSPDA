package com.dataexpo.lwsyspda.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dataexpo.lwsyspda.entity.Device;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeviceInboundAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {
    public DeviceInboundAdapter(int layoutResId, @Nullable List<Device> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Device device) {

    }
}
