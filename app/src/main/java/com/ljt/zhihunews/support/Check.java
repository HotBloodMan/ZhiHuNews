package com.ljt.zhihunews.support;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.ljt.zhihunews.MyApplicaion;

/**
 * Created by Administrator on 2017/10/6/006.
 */

public final class Check {

    private Check() {

    }

    public static boolean isZhihuClientInstalled() {
        try {
            return preparePackageManager().getPackageInfo(Constants.Information.ZHIHU_PACKAGE_ID, PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    public static boolean isIntentSafe(Intent intent) {
        return preparePackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    private static PackageManager preparePackageManager() {
        return MyApplicaion.getInstance().getPackageManager();
    }
}
