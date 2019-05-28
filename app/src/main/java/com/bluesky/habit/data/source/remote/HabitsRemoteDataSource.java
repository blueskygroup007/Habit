package com.bluesky.habit.data.source.remote;

import android.os.Handler;

import com.bluesky.habit.constant.AppConstant;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description: 实现一个模拟网络的数据源
 */
public class HabitsRemoteDataSource implements HabitsDataSource {

    private static HabitsRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Habit> HABITS_SERVICE_DATA;

    static {
        HABITS_SERVICE_DATA = new LinkedHashMap<>(3);

        try {
            addHabit(0, "habit1", "第一个好习惯,默认图标...");
            addHabit(1, "habit2", "第二个好习惯,半小时喝一次水...");
            addHabit(2, "habit3", "第三个好习惯,眼保健操...");
            addHabit(3, "habit4", "最重要的好习惯,学习,学习,学习...");
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public static HabitsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HabitsRemoteDataSource();
        }
        return INSTANCE;
    }

    private HabitsRemoteDataSource() {
    }

    private static void addHabit(int icon, String title, String description) throws CloneNotSupportedException {
        Alarm alarm = new Alarm(500, 50, 30, 10, 1000, 0, 1);
        Habit newHabit = new Habit(AppConstant.HABIT_ICONS[icon], title, description, false, alarm);
        HABITS_SERVICE_DATA.put(newHabit.getId(), newHabit);
    }

    /**
     * 模拟网络取数据，先延时5秒，再返回自行创建的两个habit
     *
     * @param callback
     */
    @Override
    public void getHabits(LoadHabitsCallback callback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onHabitsLoaded(Lists.newArrayList(HABITS_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getHabit(String habitId, GetHabitCallback callback) {
        final Habit habit = HABITS_SERVICE_DATA.get(habitId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onHabitLoaded(habit);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveHabit(Habit habit) {
        HABITS_SERVICE_DATA.put(habit.getId(), habit);
    }

    @Override
    public void completeHabit(Habit habit) {
        Habit completedHabit = new Habit(habit.getId(), habit.getIcon(), habit.getTitle(), habit.getDescription(), true, habit.getAlarm());
        HABITS_SERVICE_DATA.put(habit.getId(), habit);
    }

    @Override
    public void completeHabit(String habitId) {
        //源码注明不需要实现
    }

    @Override
    public void activateHabit(Habit habit) {
        Habit activeHabit = new Habit(habit.getId(), habit.getIcon(), habit.getTitle(), habit.getDescription(), habit.isCompleted(), habit.getAlarm());
        HABITS_SERVICE_DATA.put(habit.getId(), habit);
    }

    @Override
    public void activateHabit(String habitId) {
        //同上，不需要实现
    }

    @Override
    public void clearCompletedHabits() {
        Iterator<Map.Entry<String, Habit>> it = HABITS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Habit> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshHabits() {
        //同上
    }

    @Override
    public void deleteAllHabits() {
        HABITS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteHabit(String habitId) {
        HABITS_SERVICE_DATA.remove(habitId);
    }
}
