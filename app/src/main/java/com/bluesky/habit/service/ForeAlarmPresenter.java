package com.bluesky.habit.service;

import android.content.Context;

import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.receiver.AlarmClockReceiver;
import com.bluesky.habit.util.AlarmUtils;
import com.bluesky.habit.util.AppExecutors;
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

    //阻塞锁
    private static final Object mLock = new Object();
    public static boolean condition = false;
    private AppExecutors executors = new AppExecutors();


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
        //如果已经是激活状态,那么不执行
        if (!mActiveList.containsKey(id)) {
            mActiveList.put(id, currentSec);
        } else {
            LogUtils.e("已激活列表中已有该Habit,属于重启Alarm.....ID=" + id);
        }
        startAlarm(id, interval - currentSec);
        mMonitorMap.put(id, startMonitor(id, currentSec));
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitStarted(id);
        }
    }

    @Override
    public void reactiveHabit(String id) {

        activeHabit(id, 0, mRepository.getTaskWithId(id).getAlarm().getAlarmInterval());
    }

    @Override
    public void disableHabit(String id) {
        if (mActiveList.containsKey(id)) {
            if (mMonitorMap.containsKey(id)) {
                if (stopMonitor(id)) {
                    mMonitorMap.remove(id);
                    stopAlarm(id);
                    mActiveList.remove(id);
                    for (OnControlListener listener : mOnControlListeners
                    ) {
                        listener.onHabitStopped(id);
                    }
                }
            } else {
                LogUtils.e("没有找到对应ID....." + id);

            }
        } else {
            LogUtils.e("错误!已激活列表中没有该Habit,无法停止Alarm.....ID=" + id);
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
                listener.onHabitPaused(id);
            }
        }
    }

    @Override
    public void addOrUpdateHabit(Habit habit) {
        mRepository.saveHabit(habit);
        for (OnControlListener listener :
                mOnControlListeners) {
            listener.onHabitsChanged(habit.getId());
        }
    }

    @Override
    public void deleteHabit(String id) {
        mRepository.deleteHabit(id);
        for (OnControlListener listener :
                mOnControlListeners) {
            listener.onHabitsChanged(id);
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
        //一,停止相应的progress进度更新线程

        if (mMonitorMap.containsKey(id)) {
            if (stopMonitor(id)) {
                mMonitorMap.remove(id);
                stopAlarm(id);
                mActiveList.remove(id);
            }
        }

        //二,回调各个观察者
        /**TODO 应该在这里检查:
         *1.是否有多个活动alarm
         *2.应该用一个阻塞线程,即单例线程去执行任务.但问题是,任务只是去发个消息,而不能等任务真正完成.
         *3.维护一个栈Stack,线程检测栈顶的id是否跟自己相等,再决定是否开始执行.
         *4.用锁.锁住正在等待标志,直到10秒后,或者手动操作.即ignore,accept,skip,都能解锁.
         *5.用线程数为1的ScheduledExecutorService来实现,优点是能停止线程
         */
        executors.getBlockThread().execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("onAlarmTimeIsUp", "启动一个时间到线程!!!!!!!!!!!!!!");

                synchronized (mLock) {

                    for (OnControlListener listener : mOnControlListeners
                    ) {
                        listener.onHabitTimeUp(id);
                    }
                    LogUtils.e("onAlarmTimeIsUp", "通知所有观察者.时间到!!!!!!!!!!!!!!");
                    condition = false;
                    while (!condition) {
                        try {
                            LogUtils.e("onAlarmTimeIsUp", "进入阻塞...........mLock=" + mLock);
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        mLock.notify();
                    }
                    LogUtils.e("onAlarmTimeIsUp", "走出阻塞............");
                }
            }

        });
        //三,启动10秒自动skip计时

    }

    @Override
    public void onAlarmAccept(String id) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitAccepted(id);
        }
        onTimeUpFinished();
        reactiveHabit(id);
    }

    @Override
    public void onAlarmSkip(String id) {
        for (OnControlListener listener : mOnControlListeners
        ) {
            listener.onHabitSkipped(id);
        }
        onTimeUpFinished();
        reactiveHabit(id);
    }

    /**
     * TimeUp已经处理完成,通知解锁,可以开始处理下一个
     */
    private void onTimeUpFinished() {


        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("onAlarmTimeIsUp", "解锁.............mLock=" + mLock);
                //TODO 重要:这里如果不加锁,那么notify()解锁就无效
                synchronized (mLock) {
                    condition = true;
                    mLock.notifyAll();
                }
            }
        }, 0, TimeUnit.SECONDS);


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

        void onHabitStarted(String id);

        void onHabitPaused(String id);

        void onHabitStopped(String id);

        void onHabitAccepted(String id);

        void onHabitSkipped(String id);

        void onHabitTimeUp(String id);

        /**
         * 当repository的数据被"增删改"
         */
        void onHabitsChanged(String id);

        /**
         * todo 这里写需要被动通知的方法
         */
    }
}
