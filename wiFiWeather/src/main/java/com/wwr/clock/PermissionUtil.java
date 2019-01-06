package com.wwr.clock;

import android.content.pm.PackageManager;

/**
 * 对于6.0以上的系统用来检查权限
 */
public abstract class PermissionUtil {

    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1){
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}