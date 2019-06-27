package com.bluesky.habit.habit_detail;

import com.bluesky.habit.data.source.HabitsRepository;

/**
 * @author BlueSky
 * @date 2019/6/26
 * Description:
 */
public class HabitDetailPresenter implements HabitDetailContract.Presenter {

    private final String mHabitId;
    private final HabitsRepository mHabitRespository;
    private final HabitDetailContract.View mView;

    public HabitDetailPresenter(String habitId, HabitsRepository habitsRepository, HabitDetailContract.View taskDetailView) {
        mHabitId = habitId;
        mHabitRespository = habitsRepository;
        mView = taskDetailView;
    }

    @Override
    public void start() {

    }
}
