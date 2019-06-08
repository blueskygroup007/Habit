package com.bluesky.habit.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * @author BlueSky
 * @date 2019/5/20
 * Description:
 */
public class ActivityManagerUtils {

    public static final String TAG = ActivityManagerUtils.class.getSimpleName();

    /**
     * 查找service是否在运行
     * todo getShortClassName获取service的name,并contains,是否合适
     * serviceName最好改为全名,方法中的getShortClassName也改为getClassName
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = manager.getRunningServices(200);
        if (serviceInfoList.size() <= 0) {
            LogUtils.e(TAG, "---------->后台服务数量为空");
            return false;
        }
        for (ActivityManager.RunningServiceInfo info : serviceInfoList) {
            LogUtils.e(TAG, "---------->后台服务的name=" + info.service.getShortClassName());
            if (info.service.getShortClassName().contains(serviceName)) {
                LogUtils.e(TAG, "---------->后台服务ForeService找到啦-------------------!!!");

                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个Activity是否在前台(只限于5.0以下)
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }
}
