package com.dataexpo.lwsyspda.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.dataexpo.lwsyspda.R;

public class RfidDialog extends Dialog {
    private Context mContext;
    public EditText et_pswd;

    private OnDialogClickListener dialogClickListener;

    public RfidDialog(Context context) {
        this(context, R.style.RfidDialogStyle);
    }

    public RfidDialog(Context context, int themeResId) {
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
        final View view = inflater.inflate(R.layout.rfid_dialog, null);
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

        et_pswd = (EditText) view.findViewById(R.id.et_login_pswd);
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
