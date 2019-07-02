package com.bluesky.habit.habit_detail;

import android.util.Log;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;

/**
 * @author BlueSky
 * @date 2019/6/26
 * Description:
 */
public class HabitDetailPresenter implements HabitDetailContract.Presenter, HabitsDataSource.GetHabitCallback {
    private final static String TAG = HabitDetailActivity.class.getSimpleName();
    private final String mHabitId;
    private Habit mHabit;
    private final HabitsRepository mHabitRespository;
    private final HabitDetailContract.View mView;

    public HabitDetailPresenter(String habitId, HabitsRepository habitsRepository, HabitDetailContract.View taskDetailView) {
        mHabitId = habitId;
        mHabitRespository = habitsRepository;
        mView = taskDetailView;
    }

    @Override
    public void start() {
        populateHabit(mHabitId);
    }

    @Override
    public void saveHabit() {
        mHabitRespository.saveHabit(mView.updateHabit(mHabit));
    }

    @Override
    public void populateHabit(String id) {
        mHabitRespository.getHabit(mHabitId, this);
    }

    /**
     * 获取habit的回调方法之一
     *
     * @param habit
     */
    @Override
    public void onHabitLoaded(Habit habit) {
        if (mView.isActive()) {
            mView.showHabit(habit);
            mHabit = habit;
        }

    }

    /**
     * 获取habit的回调方法之一
     */
    @Override
    public void onDataNotAvailable() {
        if (mView.isActive()) {
            Log.e(TAG, "Habit获取失败");
        }
    }
}
