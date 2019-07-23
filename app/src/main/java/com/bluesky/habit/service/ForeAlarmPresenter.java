package com.bluesky.habit.service;

import android.content.Context;

import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
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
    /**
     * 增长步长(秒)
     */
    private static final int TIME_UNIT = 1;
    private final HabitsRepository mRepository;
    private final Context mContext;
    private ScheduledExecutorService mMonitor = new ScheduledThreadPoolExecutor(5);
    //全部Habit列表,所有的列表从这取
    private Map<String, Integer> mActiveList = new HashMap<>();
    //活动Habit链表
//    private List<Habit> mList = new ArrayList<>();
    private Map<String, ScheduledFuture> mMonitorMap = new HashMap<>();
    private List<OnControlListener> mOnControlListeners = new ArrayList<>();

    public ForeAlarmPresenter(Context context, HabitsRepository repository) {
        mRepository = repository;
        mContext = context;
    }

    /**
     * 添加或修改一个Habit.根据ID
     */
    private void addActiveHabit(String id, int currentSec) {
        mActiveList.put(id, currentSec);
    }

    private void deleteActiveHabit(String id) {
        mActiveList.remove(id);
    }


    @Override
    public Map<String, Integer> getActiveHabitList() {
        return mActiveList;
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
    public void activeHabit(String id, int currentSec, int interval) {
        mActiveList.put(id, currentSec);
        startAlarm(id, interval - currentSec);
        mMonitorMap.put(id, startMonitor(id, currentSec));
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitStarted();
        }
    }

    @Override
    public void disableHabit(String id) {
        if (mMonitorMap.containsKey(id)) {
            LogUtils.e("没有找到对应ID.....");
        } else {
            LogUtils.e("对应ID=" + id);
        }
        if (mMonitorMap.containsKey(id)) {
            if (stopMonitor(id)) {
                mMonitorMap.remove(id);
                stopAlarm(id);
                mActiveList.remove(id);
                for (OnControlListener listener : mOnControlListeners
                ) {
                    listener.onHabitStopped();
                }
            }
        }
    }

    /**
     * 不知道如何记录暂停了的habit.以及它的进度,所以该功能暂不使用
     *
     * @param id
     * @param currentSec
     */
    @Override
    public void pauseabit(String id, int currentSec) {
        if (mMonitorMap.containsKey(id)) {
            //todo 这里应该是暂停alarm,记录进度,暂停monitor,不remove,等startHabit时,再恢复
            //todo 方法1:再创建一个pauseHabit的Map集合.

            //todo 方法0:记录当前进度
            for (OnControlListener listener : mOnControlListeners
            ) {
                listener.onHabitPaused();
            }
        }
    }

    @Override
    public void addOrUpdateHabit(Habit habit) {
        mRepository.saveHabit(habit);
        for (OnControlListener listener :
                mOnControlListeners) {
            listener.onHabitsChanged();
        }
    }

    @Override
    public void deleteHabit(String id) {
        mRepository.deleteHabit(id);
        for (OnControlListener listener :
                mOnControlListeners) {
            listener.onHabitsChanged();
        }
    }



    /**
     * 启动一个监听线程
     *
     * @return 返回一个可结束该监听线程的句柄
     */
    private ScheduledFuture startMonitor(String id, int currentSec) {
        return mMonitor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mActiveList != null) {
                    int process = mActiveList.get(id);
                    mActiveList.put(id, process + TIME_UNIT);
                    LogUtils.i("Thread-run", "Monitor Thread ...." + (process + TIME_UNIT));
                    for (OnControlListener listener : mOnControlListeners
                    ) {
                        listener.onHabitProcessed(id, process + TIME_UNIT);
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);


    }

    /**
     * 关闭监听线程
     *
     * @return
     */
    private boolean stopMonitor(String id) {
        ScheduledFuture future = mMonitorMap.get(id);
        if (null != future) {
            future.cancel(true);
            LogUtils.i("Thread-stop", mRepository.getTaskWithId(id).getTitle() + "---------------");
            return true;
        }
        return false;
    }

    public void startAlarm(String id, int alertSec) {
        AlarmUtils.setAlarm(mContext, AlarmClockReceiver.class, id, alertSec);
    }

    public void stopAlarm(String id) {
        AlarmUtils.cancelAlarm(mContext, AlarmClockReceiver.class, id);
    }

    public void pauseAlarm(Alarm alarm) {

    }


    @Override
    public void onAlarmTimeIsUp(String id) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitTimeUp();
        }
    }

    @Override
    public void onAlarmAccept(String id) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitAccepted();
        }
    }

    @Override
    public void onAlarmSkip(String id) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitSkipped();
        }
    }


    @Override
    public void stopAccService() {

    }

    @Override
    public void loadHabits(boolean forceUpdate, HabitsDataSource.LoadHabitsCallback loadHabitsCallback) {
        if (forceUpdate) {
            mRepository.refreshHabits();
        }
        mRepository.getHabits(loadHabitsCallback);
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
        void onHabitProcessed(String id, int currentSec);

        void onHabitStarted();

        void onHabitPaused();

        void onHabitStopped();

        void onHabitAccepted();

        void onHabitSkipped();

        void onHabitTimeUp();

        /**
         * 当repository的数据被"增删改"
         */
        void onHabitsChanged();

        /**
         * todo 这里写需要被动通知的方法
         */
    }
}
