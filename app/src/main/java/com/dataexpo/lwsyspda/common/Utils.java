package com.dataexpo.lwsyspda.common;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final int INPUT_SUCCESS = 0;
    public static final int INPUT_HAVE_NET_ADDRESS = 1;
    public static final int INPUT_ONLY_NUM = 2;
    public static final int INPUT_CHECK_NET_ADDRESS = 3;
    public static final int INPUT_NULL = 4;
    public static final int INPUT_NO_CHECK = 99;

    public static final String dateRule = "yyyy-MM-dd HH:mm:ss";
    public static final String dateRulepit = "yyyy.MM.dd HH:mm";

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

    public static Date formatStringtoDate(String strDate) {
        try {
            Date d = null;
            if (!"".equals(strDate)) {
                d = new SimpleDateFormat(dateRule).parse(strDate);
            }
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDatetoString(Date date) {
        try {
            return new SimpleDateFormat(dateRule).format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date formatDate(String strDate, String rule) {
        try {
            Date d = null;
            if (!"".equals(strDate)) {
                d = new SimpleDateFormat(rule).parse(strDate);
            }
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatString(Date date, String rule) {
        try {
            return new SimpleDateFormat(rule).format(date);
        } catch (Exception e) {
            return null;
        }
    }
}
