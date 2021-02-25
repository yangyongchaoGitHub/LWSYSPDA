package com.dataexpo.lwsyspda.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dataexpo.lwsyspda.R;

/**
 * 配件添加弹窗
 */
public class AccessoriesDialog extends Dialog {
    private Context mContext;
    public Spinner sp_type;
    public EditText et_count;
    public TextView tv_confirm;

    public boolean initend = false;

    private OnDialogClickListener dialogClickListener;

    public AccessoriesDialog(Context context) {
        this(context, R.style.RfidDialogStyle);
    }

    public AccessoriesDialog(Context context, int themeResId) {
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
        final View view = inflater.inflate(R.layout.accessories_dialog, null);
        setContentView(view);
//
//        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(dialogClickListener != null) {
//                    dialogClickListener.onConfirmClick(view);
//                }
//            }
//        });
//
//        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dialogClickListener != null) {
//                    dialogClickListener.onModifierClick(view);
//                }
//            }
//        });

        sp_type = view.findViewById(R.id.sp_type);
        et_count = view.findViewById(R.id.et_count);
        tv_confirm = view.findViewById(R.id.confirm);
    }

    @Override
    public void show() {
        super.show();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.dialogClickListener = listener;
    }

    public interface OnDialogClickListener {
        void onConfirmClick(View view);

        void onModifierClick(View view);
    }
}
