package com.dataexpo.lwsyspda.rfid.listener;

import android.view.KeyEvent;

public interface OnKeyListener extends OnKeyDownListener {

    void onKeyUp(int keyCode, KeyEvent event);
}
