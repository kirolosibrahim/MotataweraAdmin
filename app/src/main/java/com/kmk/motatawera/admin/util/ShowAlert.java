package com.kmk.motatawera.admin.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;


public class ShowAlert {

    public static void SHOW_ALERT(Context context, String msg) {
        if (context != null)
            new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
    }
}
