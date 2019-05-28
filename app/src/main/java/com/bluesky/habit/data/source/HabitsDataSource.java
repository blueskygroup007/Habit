package com.bluesky.habit.data.source;

import com.bluesky.habit.data.Habit;

import java.util.List;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description: Task数据的接口
 */
public interface HabitsDataSource {

    /**
     * 获取所有task的回调.注意"Tasks"
     */
    interface LoadHabitsCallback {
        /**
         * 当tasks成功获取的回调
         *
         * @param habits
         */
        void onHabitsLoaded(List<Habit> habits);

        /**
         * 数据无效的回调
         */
        void onDataNotAvailable();
    }

    /**
     * 获取某个task
     */
    interface GetHabitCallback {

        /**
         * 当task成功获取的回调
         *
         * @param habit
         */
        void onHabitLoaded(Habit habit);

        /**
         * 数据无效的回调
         */
        void onDataNotAvailable();
    }

    /**
     * 得到所有tasks
     *
     * @param callback
     */
    void getHabits(LoadHabitsCallback callback);

    /**
     * 得到某个task
     *
     * @param habitId
     * @param callback
     */
    void getHabit(String habitId, GetHabitCallback callback);

    /**
     * 保存一个task
     *
     * @param habit
     */
    void saveHabit(Habit habit);

    /**
     * 结束某个task,用task
     *
     * @param habit
     */
    void completeHabit(Habit habit);

    /**
     * 结束某个task,用id
     *
     * @param habitId
     */
    void completeHabit(String habitId);

    /**
     * 激活某个task,用task
     *
     * @param habit
     */
    void activateHabit(Habit habit);

    /**
     * 激活某个task,用id
     *
     * @param habitId
     */
    void activateHabit(String habitId);

    /**
     * 清除所有已经完成的task
     */
    void clearCompletedHabits();

    /**
     * 刷新
     */
    void refreshHabits();

    /**
     * 删除所有
     */
    void deleteAllHabits();

    /**
     * 删除某个task
     *
     * @param habitId
     */
    void deleteHabit(String habitId);
}
