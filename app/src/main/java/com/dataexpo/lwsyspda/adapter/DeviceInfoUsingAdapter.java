package com.dataexpo.lwsyspda.adapter;

import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.common.Utils;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.DeviceUsingInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeviceInfoUsingAdapter extends BaseQuickAdapter<DeviceUsingInfo, BaseViewHolder> {

    public int type = 0;

    public DeviceInfoUsingAdapter(int layoutResId, @Nullable List<DeviceUsingInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, DeviceUsingInfo item) {
        Log.i("convert", item.getName());

        baseViewHolder.setText(R.id.tv_time, Utils.formatString(item.getDate(), Utils.dateRulepit))
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_remark, type == 0 ? item.getBomName() : (
                        type == 1 ? item.getHouse() : item.getRemark()
                        ));
    }
}
