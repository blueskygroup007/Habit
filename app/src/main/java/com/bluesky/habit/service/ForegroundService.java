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
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.habit_list.HabitListContract;
import com.bluesky.habit.habit_list.HabitListPresenter;
import com.bluesky.habit.util.Injection;
import com.bluesky.habit.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

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
    private static final String EXTRA_HABIT = "extra_habit";
    private ForeAlarmPresenter mPresenter;

    private List<OnControlListener> mOnControlListeners = new ArrayList<>();

    /**
     * todo 这里主要是响应客户端的复杂指令
     */
    public class ForeControlBinder extends Binder {

        /**
         * 维护一个Listener列表,用来让service通知客户端去更新状态和显示.
         *
         * @param listener
         */
        public void registerOnControlListener(OnControlListener listener) {
            mOnControlListeners.add(listener);
        }

        public void unregisterOnControlListener(OnControlListener listener) {
            mOnControlListeners.remove(listener);
        }

        /**todo 这里应该放置 "允许bind到service的客户端" 的主动方法.
         * todo 例如:设置当前habit列表;
         */

        /**
         * todo 该方法执行时机是Bind(绑定)ForeService,
         * todo 分为第一次创建,或者Activity Resume.
         * todo 即--Activity和Service建立联系.同时也是V和P建立联系.
         * todo 此时的工作应该是:将V给P,将P也给V.使双方能互相操作
         */
        public void start() {
            if (mPresenter == null) {
                mPresenter = new ForeAlarmPresenter(ForegroundService.this, Injection.provideTasksRepository(ForegroundService.this));
            }
            mPresenter.start();

        }

        /**
         * 启动Habit
         *
         * @param habit
         */
        public void doStartHabit(Habit habit) {
            LogUtils.d(TAG, "启动一个Habit...");
            mPresenter.startAlarm(habit.getAlarm());
        }
    }

    private ForeControlBinder mBinder = new ForeControlBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static final String ACTION_PLAY = "com.bluesky.habit.action.PLAY";
    public static final String ACTION_PAUSE = "com.bluesky.habit.action.PAUSE";
    public static final String ACTION_STOP = "com.bluesky.habit.action.STOP";
    public static final String ACTION_TIMEUP = "com.bluesky.habit.action.TIMEUP";
    public static final String ACTION_ACCEPT = "com.bluesky.habit.action.ACCEPT";
    public static final String ACTION_SKIP = "com.bluesky.habit.action.SKIP";
    public static final String ACTION_DISMISS = "com.bluesky.habit.action.DISMISS";

    /**
     * 这里是被客户端通过发送intent,设置action来启动的方法.
     * todo 这里主要是接收简单指令
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Habit habit = intent.getParcelableExtra(EXTRA_HABIT);
        Alarm alarm = habit.getAlarm();
        switch (action) {
            case ACTION_PLAY:
                mPresenter.startAlarm(alarm);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitStarted();
                }
                break;
            case ACTION_PAUSE:
                mPresenter.pauseAlarm(alarm);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitPaused();
                }
                break;
            case ACTION_STOP:
                mPresenter.stopAlarm(alarm);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitStopped();
                }
                break;
            case ACTION_ACCEPT:
                mPresenter.onAlarmAccept(alarm);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitAccepted();
                }
            case ACTION_SKIP:
                mPresenter.onAlarmSkip(alarm);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitSkipped();
                }
                break;
            case ACTION_TIMEUP:
                mPresenter.onAlarmSkip(alarm);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitTimeUp();
                }
                break;
            case ACTION_DISMISS:
                doDissmiss();
                break;
            default:
                LogUtils.e(TAG, "前台服务首次启动了.onStartCommand 和startForeground.....");
                startForeground(ID, createNoti());
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, "创建ForegroundService...");
    }

    private void doDissmiss() {
        stopForeground(true);
        LogUtils.e(TAG, "service被停止前台了...................");
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
        LogUtils.e(TAG, "前台服务onUnbind了.....");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtils.e(TAG, "前台服务onDestroy了.....");
        super.onDestroy();
        stopForeground(true);

        mOnControlListeners.clear();
        mOnControlListeners = null;
    }

    /**
     * 所有需要通知监听者的动作接口
     */
    private interface OnControlListener {
        void onHabitStarted();

        void onHabitPaused();

        void onHabitStopped();

        void onHabitAccepted();

        void onHabitSkipped();

        void onHabitTimeUp();

        /**
         * todo 这里写需要被动通知的方法
         */
    }
}
