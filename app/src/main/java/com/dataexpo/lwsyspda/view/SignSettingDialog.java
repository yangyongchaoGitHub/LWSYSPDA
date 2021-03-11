package com.dataexpo.lwsyspda.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dataexpo.lwsyspda.R;

/**
 * 设置频率弹窗
 */
public class SignSettingDialog extends Dialog {
    private Context mContext;
    public SeekBar SK1;
    public TextView confirm;
    public TextView tv_sv;

    public boolean initend = false;

    private OnDialogClickListener dialogClickListener;

    public SignSettingDialog(Context context) {
        this(context, R.style.RfidDialogStyle);
    }

    public SignSettingDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.sign_setting_dialog, null);
        setContentView(view);
        tv_sv = view.findViewById(R.id.tv_sv);
        tv_sv.setText("3");

        SK1 = view.findViewById(R.id.SK1);
        SK1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_sv.setText(progress + 1 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClickListener != null) {
                    dialogClickListener.onSSDConfirmClick(v);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.dialogClickListener = listener;
    }

    public interface OnDialogClickListener {
        void onSSDConfirmClick(View view);

        void onSSDModifierClick(View view);
    }
}
