package com.dataexpo.lwsyspda.common;

import android.text.TextUtils;

public class Utils {
    public static final int INPUT_SUCCESS = 0;
    public static final int INPUT_HAVE_NET_ADDRESS = 1;
    public static final int INPUT_ONLY_NUM = 2;
    public static final int INPUT_CHECK_NET_ADDRESS = 3;
    public static final int INPUT_NULL = 4;
    public static final int INPUT_NO_CHECK = 99;

    public static int checkInput(String input) {
        return checkInput(input, INPUT_CHECK_NET_ADDRESS);
    }

    public static int checkInput(String input, int target) {
        if (TextUtils.isEmpty(input)) {
            return INPUT_NULL;
        }

        if (input.contains("http")) {
            return INPUT_HAVE_NET_ADDRESS;
        }

        if (input.contains("www")) {
            return INPUT_HAVE_NET_ADDRESS;
        }

        if (INPUT_ONLY_NUM == target) {

        }
        //TODO: check int or order code
        return INPUT_SUCCESS;
    }
}
