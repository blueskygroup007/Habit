package com.bluesky.habit.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bluesky.habit.R;
import com.bluesky.habit.activity.MainActivity;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.util.Injection;
import com.bluesky.habit.util.LogUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.bluesky.habit.data.Habit.HABIT_ID;
import static com.bluesky.habit.data.Habit.HABIT_INTERVAL;

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
    private ForeAlarmPresenter mPresenter;


    /**
     * todo 这里主要是响应客户端的复杂指令
     */
    public class ForeControlBinder extends Binder implements Serializable {

        /**
         * 维护一个Listener列表,用来让service通知客户端去更新状态和显示.
         *
         * @param listener
         */
        public void registerOnControlListener(ForeAlarmPresenter.OnControlListener listener) {
            mPresenter.registerOnControlListener(listener);
        }

        public void unregisterOnControlListener(ForeAlarmPresenter.OnControlListener listener) {
            mPresenter.unregisterOnControlListener(listener);
        }

        /**
         * todo 放置 "允许bind到service"的客户的主动方法.--------------------------------------
         */

//        public void setActiveHabitList(List<String> list) {
//            mPresenter.setActiveHabitList(list);
//        }
        public Map<String, Integer> getActiveHabitList() {
            return mPresenter.getActiveHabitList();
        }

        /**
         * //todo 获取即时信息-----------------------------------------
         * //todo getActiveHabitList应该就是了
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

        public void loadHabits(boolean forceUpdate, HabitsDataSource.LoadHabitsCallback loadHabitsCallback) {
            mPresenter.loadHabits(forceUpdate, loadHabitsCallback);
        }

        /**
         * 增加一个habit
         */
        public void addOrUpdateHabit(Habit habit) {
            mPresenter.addOrUpdateHabit(habit);
        }

        /**
         * 删除一个habit
         */
        public void deleteHabit(String id) {
            mPresenter.deleteHabit(id);
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
    /**
     * habit的alarm计时到期了
     */
    public static final String ACTION_TIMEUP = "com.bluesky.habit.action.TIMEUP";
    public static final String ACTION_ACCEPT = "com.bluesky.habit.action.ACCEPT";
    public static final String ACTION_SKIP = "com.bluesky.habit.action.SKIP";
    /**
     * 关闭前台service的通知栏
     */
    public static final String ACTION_DISMISS = "com.bluesky.habit.action.DISMISS";
    /**
     * 每分钟更新
     */
    public static final String ACTION_PROCESSED = "com.bluesky.habit.action.PROCESSED";


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

        if (intent == null) {
            LogUtils.e(TAG, "前台服务启动. 但intent为空..");

            return START_STICKY;
        }
        String action = intent.getAction();
        switch (action) {
            case ACTION_PLAY:
                LogUtils.d(TAG, "启动一个Habit...");
                mPresenter.activeHabit(intent.getStringExtra(HABIT_ID), 0, intent.getIntExtra(HABIT_INTERVAL, 15));
                break;
            case ACTION_PAUSE:
//                mPresenter.pauseAlarm(alarm);
                break;
            case ACTION_STOP:
                LogUtils.d(TAG, "Stop一个Habit...");
                mPresenter.disableHabit(intent.getStringExtra(HABIT_ID));
                break;
            case ACTION_ACCEPT:
                mPresenter.onAlarmAccept(intent.getStringExtra(HABIT_ID));
                break;
            case ACTION_SKIP:
                mPresenter.onAlarmSkip(intent.getStringExtra(HABIT_ID));
                break;
            case ACTION_TIMEUP:
                mPresenter.onAlarmTimeIsUp(intent.getStringExtra(HABIT_ID));
                break;
            case ACTION_DISMISS:
                doDissmiss();
                break;
            default:
                LogUtils.e(TAG, "前台服务首次启动了.onStartCommand 和startForeground.....");
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, "创建前台ForegroundService...");
        startForeground(ID, createNoti());
        LogUtils.e(TAG, "创建ForeAlarmPresenter...");

        if (mPresenter == null) {
            mPresenter = new ForeAlarmPresenter(ForegroundService.this, Injection.provideTasksRepository(ForegroundService.this));
        }
        mPresenter.start();
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

    /**
     * todo 系统异常kill掉service时,不会调用destroy
     */
    @Override
    public void onDestroy() {
        LogUtils.e(TAG, "前台服务onDestroy了.....");
        super.onDestroy();
        stopForeground(true);

        mPresenter.onDestory();
    }


}
