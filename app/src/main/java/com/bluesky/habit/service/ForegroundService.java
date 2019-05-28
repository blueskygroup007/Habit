package com.bluesky.habit.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.bluesky.habit.R;
import com.bluesky.habit.activity.MainActivity;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.habit_list.HabitListContract;
import com.bluesky.habit.habit_list.HabitListPresenter;
import com.bluesky.habit.util.Injection;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @author BlueSky
 * @date 2019/5/20
 * Description:
 */

/**
 * todo 修改方案:
 * 1.service需要一个无参构造方法.
 * 2.service的启动方式改为绑定.这样就有iBinder可以操控service
 * 2.1 绑定方式启动,更符合需求:(看音乐播放器的service的使用方法)
 * 2.2 可以先startService(),然后谁需要操作它,就bindService()
 * 3.service应该在P中被启动
 * <p>
 * todo 前台服务的必要性是什么?不容易被回收,有通知图标.
 * todo IntentService的使用(因为service是运行在主线程中,所以耗时任务容易产生ANR)
 */
public class ForegroundService extends Service {
    private static final String TAG = ForegroundService.class.getSimpleName();
    public static final int ID = 12346;
    private static final String ACTION_DISMISS = "action_dismiss";
    private static boolean isRunning = false;
    private ForeAlarmPresenter mPresenter;

    public class MyBinder extends Binder {
        public ForegroundService getInstance() {
            return ForegroundService.this;
        }

        public void doStartHabit(Habit habit) {
            Log.e(TAG, "启动一个习惯....." + habit.getTitle());
        }

        public void doStopHabit(Habit habit) {
            Log.e(TAG, "停止一个习惯....." + habit.getTitle());

        }

        public void doPauseHabit(Habit habit) {
            Log.e(TAG, "暂停一个习惯....." + habit.getTitle());

        }

        /**
         * todo 该方法执行时机是Bind(绑定)ForeService,
         * todo 分为第一次创建,或者Activity Resume.
         * todo 即--Activity和Service建立联系.同时也是V和P建立联系.
         * todo 此时的工作应该是:将V给P,将P也给V.使双方能互相操作
         *
         */
        public void start() {
            if (mPresenter == null) {
                mPresenter = new ForeAlarmPresenter(Injection.provideTasksRepository(ForegroundService.this));
            }
            mPresenter.start();

        }
    }

    private MyBinder mBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            Log.e(TAG, "前台任务被重复启动...................");
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_DISMISS)) {
                //true代表顺便移除通知,但service应该还在
                stopForeground(true);
                Log.e(TAG, "service被停止前台了...................");
            }
        } else {
            Log.e(TAG, "前台服务首次启动了.onStartCommand 和startForeground.....");

            startForeground(ID, createNoti());
            isRunning = true;
        }
        return START_STICKY;
    }


    public Notification createNoti() {
        //点击通知栏的PI
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //关闭通知栏的PI
        Intent dismissIntent = new Intent(this, ForegroundService.class);
        dismissIntent.setAction(ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(
                this, 0, dismissIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("新版Noti")
                .setContentText("消息内容....")
                .setSmallIcon(R.drawable.ic_star_border_black_24dp)
                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_ALL)
                // 该方法在Android 4.1之前会被忽略
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("大文本风格"))
                .addAction(R.drawable.ic_star_black_24dp, "btn", piDismiss);
        return builder.build();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "前台服务onUnbind了.....");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "前台服务onDestroy了.....");
        super.onDestroy();
        stopForeground(true);
    }
}
