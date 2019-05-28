package com.bluesky.habit.data.source.local;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/20
 * Description:
 */
public class HabitsLocalDataSource implements HabitsDataSource {

    private static volatile HabitsLocalDataSource INSTANCE;
    private HabitDao mHabitDao;

    //todo 这里用到了Executors类,需要了解
    private AppExecutors mAppExecutors;

    private HabitsLocalDataSource(HabitDao habitsDao, AppExecutors appExecutors) {
        mHabitDao = habitsDao;
        mAppExecutors = appExecutors;
    }

    public static HabitsLocalDataSource getInstance(HabitDao habitsDao, AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (HabitsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HabitsLocalDataSource(habitsDao, appExecutors);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void getHabits(HabitsDataSource.LoadHabitsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Habit> habits = mHabitDao.getHabits();
                //todo 如何保证getHabits()返回结果之后,才开始在mainThread检查结果(habits)为空?
                //todo debug一下,看看是否都在主线程.

                //todo 理解:这里的getHabits()是个阻塞操作.只有结果返回了,才会执行下一行代码.
                //todo 接下来只是为了在主线程中压入一个事件,即回调
                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (habits.isEmpty()) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onHabitsLoaded(habits);
                        }
                    }
                });
            }
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void getHabit(String habitId, GetHabitCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Habit habit = mHabitDao.getHabitById(habitId);
                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (habit != null) {
                            callback.onHabitLoaded(habit);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void saveHabit(Habit habit) {
        checkNotNull(habit);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mHabitDao.insertHabit(habit);
            }
        };
        mAppExecutors.getDiskIO().execute(saveRunnable);
    }

    @Override
    public void completeHabit(Habit habit) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                mHabitDao.updateCompleted(habit.getId(), true);
            }
        };
        mAppExecutors.getDiskIO().execute(completeRunnable);
    }

    /**
     * 这个方法没有实现,源码上说本地数据源不需要,因为HabitsRepository负责转换一个habitId到habit,使用它缓存的数据
     * 可能是说,由于HabitsRepository可以用habitId转换成habit,然后调用上面那个使用habit的方法即可
     * @param habitId
     */
    @Override
    public void completeHabit(String habitId) {

    }

    @Override
    public void activateHabit(Habit habit) {

    }

    @Override
    public void activateHabit(String habitId) {

    }

    @Override
    public void clearCompletedHabits() {

    }

    @Override
    public void refreshHabits() {

    }

    @Override
    public void deleteAllHabits() {

    }

    @Override
    public void deleteHabit(String habitId) {

    }
}
