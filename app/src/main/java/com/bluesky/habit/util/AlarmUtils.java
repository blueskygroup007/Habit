package com.bluesky.habit.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.bluesky.habit.data.Alarm;


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
    private static final String TAG = AlarmUtils.class.getSimpleName();

    /**
     * 设置闹钟的静态方法
     * todo PendingIntent 的区分主要是使用了getXXX()方法的第二个参数requestCode.用以区分alarm
     *
     * @param context
     * @param clsReceiver
     * @param alarm
     */
    public static void setAlarm(Context context, Class<?> clsReceiver, Alarm alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, clsReceiver);
        intent.setAction(ALARM_ACTION);
        intent.putExtra("alarm", alarm);
        //使用hashCode作为请求码,以区分alarm
        PendingIntent pi = PendingIntent.getBroadcast(context, alarm.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long interval = alarm.getAlarmInterval();
        /**
         *
         * SystemClock.elapsedRealtime()  系统消逝的时间
         * SystemClock.currentThreadTimeMillis() 系统当前线程启动毫秒数
         */
        Log.d(TAG, "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "setExactAndAllowWhileIdle方法");
            am.setExactAndAllowWhileIdle(ALARM_TYPE_DEFAULT, System.currentTimeMillis() + interval, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e(TAG, "setExact方法");
            am.setExact(ALARM_TYPE_DEFAULT, System.currentTimeMillis() + interval, pi);
        } else {
            Log.e(TAG, "set方法");
            am.set(ALARM_TYPE_DEFAULT, System.currentTimeMillis() + interval, pi);
        }
    }

    /**
     * 取消 alarm 使用 AlarmManager.cancel() 函数，传入参数是个 PendingIntent 实例。
     * <p>
     * 该函数会将所有跟这个 PendingIntent 相同的 Alarm 全部取消，怎么判断两者是否相同，android 使用的是 intent.filterEquals()，具体就是判断两个 PendingIntent 的 action、data、type、class 和 category 是否完全相同。
     *
     * @param context
     * @param clsReceiver
     */
    public static void cancelAlarm(Context context, Class<?> clsReceiver, Alarm alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, clsReceiver);
        intent.setAction(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarm.hashCode(), intent, 0);
        am.cancel(pi);
    }
}
