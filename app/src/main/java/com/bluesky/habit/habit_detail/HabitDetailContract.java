package com.bluesky.habit.habit_detail;

import com.bluesky.habit.base.BasePresenter;
import com.bluesky.habit.base.BaseView;
import com.bluesky.habit.data.Habit;

/**
 * @author BlueSky
 * @date 2019/6/26
 * Description:
 */
public class HabitDetailContract {
    interface View extends BaseView<Presenter> {


    }

    interface Presenter extends BasePresenter {
        void saveHabit(Habit habit);

        /**
         * 使用id取habit
         * TODO 疑问:1.源码没有使用id作为参数.2.没有使用Habit作为返回值.2.1这个方法是个异步方法,源码使用了repository的回调
         */
        void populateHabit(String id);
    }
}
