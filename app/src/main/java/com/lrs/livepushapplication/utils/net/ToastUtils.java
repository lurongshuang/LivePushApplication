package com.lrs.livepushapplication.utils.net;

import android.widget.Toast;

import com.lrs.livepushapplication.application.Application;


public class ToastUtils {
    private static boolean isToastShow = true;

    public static void makeToast(String text) {
        if (isToastShow) {
            Toast.makeText(Application.getApplication(), text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void makeToast(String text, boolean isShort) {
        if (isToastShow) {
            if (isShort) {
                Toast.makeText(Application.getApplication(), text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Application.getApplication(), text, Toast.LENGTH_LONG).show();
            }
        }
    }
}
