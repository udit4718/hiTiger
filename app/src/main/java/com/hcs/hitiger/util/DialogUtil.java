package com.hcs.hitiger.util;

import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {

    private static final String LOG_TAG = DialogUtil.class.getSimpleName();


    // generic confirmation dialog
    public static void showConfirmationDialog(Context context, String title, String message, DialogInterface.OnClickListener positiveButtonClickListener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", positiveButtonClickListener).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }


}
