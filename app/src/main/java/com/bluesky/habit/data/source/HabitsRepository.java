package com.bluesky.habit.data.source;

import com.bluesky.habit.data.Habit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description: 三级缓存的task仓库实现
 */
public class HabitsRepository implements HabitsDataSource {

    private static HabitsRepository INSTANCE = null;

    private final HabitsDataSource mTasksRemoteDataSource;

    private final HabitsDataSource mTasksLocalDataSource;

    /**
     * 这个变量被赋予默认访问权限，即包访问权限，是为了测试时能够访问到。
     */
    Map<String, Habit> mCachedTasks;

    /**
     * 将缓存标记为无效，以便在下次请求数据时强制更新，可见性同上
     */
    boolean mCacheIsDirty = false;

    private HabitsRepository(HabitsDataSource tasksRemoteDataSource, HabitsDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * 返回单例
     *
     * @param tasksRemoteDataSource
     * @param tasksLocalDataSource
     * @return
     */
    public static HabitsRepository getInstance(HabitsDataSource tasksRemoteDataSource, HabitsDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new HabitsRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //TODO 仅仅继承了方法,还未具体实现

    /**
     * 获取所有task
     *
     * @param callback presenter的回调，通知presenter获取成功或者失败
     */
    @Override
    public void getHabits(LoadHabitsCallback callback) {
        checkNotNull(callback);

        //注意，此处使用三级缓存。一级“缓存”是指内存，二级是指本地数据库，三级是指net

        //如果有缓存，或者缓存有效(不需要更新)，直接从缓存取数据给回调
        if (mCachedTasks != null && !mCacheIsDirty) {
            //new一个list副本，应该是为了防止修改缓存
            callback.onHabitsLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        //没缓存，或者缓存过期，都到这里

        //如果缓存需要更新，从网络取一次
        if (mCacheIsDirty) {
            getTasksFromRemoteDataSource(callback);
        } else {//否则，从本地取
            mTasksLocalDataSource.getHabits(new LoadHabitsCallback() {
                @Override
                public void onHabitsLoaded(List<Habit> habits) {
                    refreshCache(habits);
                    callback.onHabitsLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    /**
     * 更新缓存
     *
     * @param habits
     */
    private void refreshCache(List<Habit> habits) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Habit habit : habits) {
            mCachedTasks.put(habit.getId(), habit);
        }
        mCacheIsDirty = false;
    }

    /**
     * 从网络缓存取数据
     *
     * @param callback
     */
    private void getTasksFromRemoteDataSource(LoadHabitsCallback callback) {
        mTasksRemoteDataSource.getHabits(new LoadHabitsCallback() {
            @Override
            public void onHabitsLoaded(List<Habit> habits) {
                refreshCache(habits);
                refreshLocalDataSource(habits);
                callback.onHabitsLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * 更新本地缓存
     *
     * @param habits
     */
    private void refreshLocalDataSource(List<Habit> habits) {
        mTasksLocalDataSource.deleteAllHabits();
        for (Habit habit :
                habits) {
            mTasksLocalDataSource.saveHabit(habit);
        }
    }

    @Override
    public void getHabit(String habitId, GetHabitCallback callback) {
        checkNotNull(habitId);
        checkNotNull(callback);
        Habit cachedHabit = getTaskWithId(habitId);
        if (cachedHabit != null) {
            callback.onHabitLoaded(cachedHabit);
            return;
        }

        //本地缓存取task
        mTasksLocalDataSource.getHabit(habitId, new GetHabitCallback() {
            @Override
            public void onHabitLoaded(Habit habit) {
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(habit.getId(), habit);
                callback.onHabitLoaded(habit);
            }

            @Override
            public void onDataNotAvailable() {
                //网络缓存取task
                mTasksRemoteDataSource.getHabit(habitId, new GetHabitCallback() {
                    @Override
                    public void onHabitLoaded(Habit habit) {
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(habit.getId(), habit);
                        callback.onHabitLoaded(habit);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        //都没有取到
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    private Habit getTaskWithId(String taskId) {
        checkNotNull(taskId);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }

    }

    @Override
    public void saveHabit(Habit habit) {
        checkNotNull(habit);
        mTasksRemoteDataSource.saveHabit(habit);
        mTasksLocalDataSource.saveHabit(habit);
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(habit.getId(), habit);
    }


    @Override
    public void completeHabit(Habit habit) {
        checkNotNull(habit);
        mTasksRemoteDataSource.completeHabit(habit);
        mTasksLocalDataSource.completeHabit(habit);
        //根据原task生成一个新task，并设置completed为true，并用HashMap的put方法覆盖原task
        Habit completedHabit = new Habit(habit.getId(),habit.getIcon(), habit.getTitle(), habit.getDescription(), true, habit.getAlarm());

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(habit.getId(), completedHabit);
    }

    @Override
    public void completeHabit(String habitId) {
        checkNotNull(habitId);
        completeHabit(getTaskWithId(habitId));
    }

    @Override
    public void activateHabit(Habit habit) {
        checkNotNull(habit);
        mTasksRemoteDataSource.activateHabit(habit);
        mTasksLocalDataSource.activateHabit(habit);

//        Habit activiteHabit = new Habit(habit);
        Habit activiteHabit = habit;
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(habit.getId(), activiteHabit);
    }

    @Override
    public void activateHabit(String habitId) {
        checkNotNull(habitId);
        activateHabit(getTaskWithId(habitId));
    }

    @Override
    public void clearCompletedHabits() {
        mTasksRemoteDataSource.clearCompletedHabits();
        mTasksLocalDataSource.clearCompletedHabits();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Iterator<Map.Entry<String, Habit>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Habit> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshHabits() {
        //todo 只是设置了标志位，并没有主动刷新列表
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllHabits() {
        mTasksRemoteDataSource.deleteAllHabits();
        mTasksLocalDataSource.deleteAllHabits();
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteHabit(String habitId) {
        mTasksRemoteDataSource.deleteHabit(habitId);
        mTasksLocalDataSource.deleteHabit(habitId);

        mCachedTasks.remove(habitId);
    }
}
