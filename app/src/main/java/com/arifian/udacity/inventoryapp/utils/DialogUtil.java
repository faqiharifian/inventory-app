package com.arifian.udacity.inventoryapp.utils;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by faqih on 26/04/17.
 */

public class DialogUtil {
    public static AlertDialog create(Context context, String title, String message, boolean cancelable, String positiveButton, DialogInterface.OnClickListener positiveListener, String negativeButton, DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton(positiveButton, positiveListener);
        alertDialogBuilder.setNegativeButton(negativeButton, negativeListener);
        alertDialogBuilder.setCancelable(cancelable);
        return alertDialogBuilder.create();
    }

    public static void hideAlert(AlertDialog dialog){
        if(dialog != null){
            dialog.dismiss();
        }
    }
}
