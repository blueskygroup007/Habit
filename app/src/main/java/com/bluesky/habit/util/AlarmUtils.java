package com.bluesky.habit.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * @author BlueSky
 * @date 2019/5/8
 * Description:
 */
public class AlarmUtils {

    /**
     * AlarmManager中一共提供了四种闹钟类型，
     * 前两种(RTC)对应的System.currentTimeMillis()（系统当前时间）时间，
     * 后两种(ELAPSED)对应SystemClock.elapsedRealtime()（系统运行时间）
     */
    public static final int ALARM_TYPE_DEFAULT = AlarmManager.RTC_WAKEUP;
    public static final String ALARM_ACTION = "com.bluesky.alarm.timeup";
    private static final String TAG = AlarmUtils.class.getSimpleName().toString();

    public static void setAlarm(Context context, Class<?> clsReceiver, long interval) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, clsReceiver);
        intent.setAction(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        /**
         *
         * SystemClock.elapsedRealtime()  系统消逝的时间
         * SystemClock.currentThreadTimeMillis() 系统当前线程启动毫秒数
         */
        Log.d(TAG, "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(ALARM_TYPE_DEFAULT, System.currentTimeMillis() + interval, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(ALARM_TYPE_DEFAULT, System.currentTimeMillis() + interval, pi);
        } else {
            am.set(ALARM_TYPE_DEFAULT, System.currentTimeMillis() + interval, pi);
        }
    }

    /**
     * 取消 alarm 使用 AlarmManager.cancel() 函数，传入参数是个 PendingIntent 实例。
     *
     * 该函数会将所有跟这个 PendingIntent 相同的 Alarm 全部取消，怎么判断两者是否相同，android 使用的是 intent.filterEquals()，具体就是判断两个 PendingIntent 的 action、data、type、class 和 category 是否完全相同。
     * @param context
     * @param clsReceiver
     */
    public static void cancelAlarm(Context context, Class<?> clsReceiver) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, clsReceiver);
        intent.setAction(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pi);
    }
}
