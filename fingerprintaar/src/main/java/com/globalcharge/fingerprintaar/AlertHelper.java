package com.globalcharge.fingerprintaar;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by GlobalCharge on 25/10/2018.
 */
public class AlertHelper {

    public static void showSecuritySettings(final Activity activity, String message, final SecuritySettingsSelectedListener listener){


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        listener.openSecuritySettings(true);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        listener.openSecuritySettings(false);
                        dialog.dismiss();
                        break;
                }
            }
        };
        builder.setTitle("").setMessage(message).setPositiveButton("Open Settings", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();

    }
}
