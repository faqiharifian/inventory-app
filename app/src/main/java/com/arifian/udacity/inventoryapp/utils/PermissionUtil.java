package com.arifian.udacity.inventoryapp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by faqiharifian on 30/03/17.
 */

public class PermissionUtil {
    static String[] permissions = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET
    };

    @TargetApi(23)
    public static void askPermissions(Activity context) {
        if(PermissionUtil.shouldAskPermissions(context, permissions)){
            int requestCode = 200;
            context.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean isPermissionGranted(Context context, String[] permissions){
        boolean result = true;
        for(String permission : permissions){
            result = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            if(!result) break;
        }
        return result;
    }

    public static boolean shouldAskPermissions(Context context, String[] permissions) {
        boolean isPermissionGranted = isPermissionGranted(context, permissions);
        return !(isPermissionGranted(context, permissions)) &&
                (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
