package com.bluesky.habit.service;

import android.content.Context;

import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.receiver.AlarmClockReceiver;
import com.bluesky.habit.util.AlarmUtils;
import com.bluesky.habit.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ForeAlarmPresenter implements ForeContract.ForePresenter {
    private final HabitsRepository mRepository;
    private final Context mContext;
    private ScheduledExecutorService mMonitor = new ScheduledThreadPoolExecutor(5);
    //这里维护一个活动Habit链表
    private List<Habit> mList = new ArrayList<>();
    private Map<String, ScheduledFuture> mMonitorMap = new HashMap();
    private List<OnControlListener> mOnControlListeners = new ArrayList<>();

    public ForeAlarmPresenter(Context context, HabitsRepository repository) {
        mRepository = repository;
        mContext = context;
    }

    /**
     * 添加或修改一个Habit.根据ID
     *
     * @param habit
     */
    private void addActiveHabit(Habit habit) {
        mList.add(habit);

    }

    private void deleteActiveHabit(Habit habit) {
        mList.remove(habit);

    }

    @Override
    public void setActiveHabitList(List<Habit> list) {
        mList.clear();
        mList.addAll(list);
    }

    @Override
    public List<Habit> getActiveHabitList() {
        return mList;

    }

    @Override
    public void registerOnControlListener(OnControlListener listener) {
        mOnControlListeners.add(listener);

    }

    @Override
    public void unregisterOnControlListener(OnControlListener listener) {
        mOnControlListeners.remove(listener);

    }

    @Override
    public void activeHabit(Habit habit) {
        mList.add(habit);
        startAlarm(habit.getAlarm());
        mMonitorMap.put(habit.getId(), startMonitor(habit));
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitStarted();
        }
    }

    @Override
    public void disableHabit(Habit habit) {
        if (mList.contains(habit)) {
            mMonitorMap.remove(stopMonitor(habit));
            stopAlarm(habit.getAlarm());
            mList.remove(habit);
            for (OnControlListener listener : mOnControlListeners
            ) {
                listener.onHabitStopped();
            }
        }
    }

    @Override
    public void pauseabit(Habit habit) {
        if (mList.contains(habit)) {
            //todo 这里应该是暂停alarm,记录进度,暂停monitor,不remove,等startHabit时,再恢复
            //todo 方法1:再创建一个pauseHabit的Map集合.
            for (OnControlListener listener : mOnControlListeners
            ) {
                listener.onHabitPaused();
            }
        }
    }

    /**
     * 启动一个监听线程
     *
     * @param habit
     * @return 返回一个可结束该监听线程的句柄
     */
    private ScheduledFuture startMonitor(Habit habit) {
        return mMonitor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                habit.getAlarm().setAlarmCurrent(habit.getAlarm().getAlarmCurrent() + 1 * 1000);
                LogUtils.i("Thread-run", "Monitor Thread ...." + habit.getTitle() + "current=" + habit.getAlarm().getAlarmCurrent());

                for (OnControlListener listener : mOnControlListeners
                ) {

                    listener.onHabitProcessed(habit);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);


    }

    /**
     * 关闭监听线程
     * @param habit
     * @return
     */
    private String stopMonitor(Habit habit) {
        ScheduledFuture future = mMonitorMap.get(habit.getId());
        if (null != future) {
            future.cancel(true);
            LogUtils.i("Thread-stop", habit.getTitle() + "---------------");
            return habit.getId();
        }
        return "";
    }

    public void startAlarm(Alarm alarm) {
        AlarmUtils.setAlarm(mContext, AlarmClockReceiver.class, alarm);
    }

    public void stopAlarm(Alarm alarm) {
        AlarmUtils.cancelAlarm(mContext, AlarmClockReceiver.class, alarm);
    }

    public void pauseAlarm(Alarm alarm) {

    }

    public void onAlarmTimeIsUp(Alarm alarm) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitTimeUp();
        }
    }

    public void onAlarmAccept(Alarm alarm) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitAccepted();
        }
    }

    public void onAlarmSkip(Alarm alarm) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitSkipped();
        }
    }

    @Override
    public void stopAccService() {

    }

    @Override
    public void start() {

    }

    public void onDestory() {
        mOnControlListeners.clear();
        mOnControlListeners = null;
    }

    /**
     * 所有需要通知监听者的动作接口
     */
    public interface OnControlListener {
        void onHabitProcessed(Habit habit);

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
