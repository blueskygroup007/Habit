package com.bluesky.habit.habit_list;

import com.bluesky.habit.base.BasePresenter;
import com.bluesky.habit.base.BaseView;
import com.bluesky.habit.data.Habit;

import java.util.List;

/**
 * @author BlueSky
 * @date 2019/4/27
 * Description:
 */
public interface HabitListContract {
    interface Presenter extends BasePresenter {

        /**
         * 从网络加载所有habit数据
         * todo 重点备忘:这里没有返回值,因为方法的实现中,由repository来真正获取数据,并完成成功/失败的回调
         * todo         repository回调通知Presenter,再由presenter来继续处理数据(让view去显示数据)
         *
         * @param forceUpdate 是否强制更新
         */
        void loadHabits(boolean forceUpdate);


        /**
         * 添加新Habit
         */
        void addNewHabit();

        /**
         * 打开详情页
         *
         * @param requestedHabit
         */
        void openHabitDetails(Habit requestedHabit);

        /**
         * 停用该Habit
         *
         * @param completedHabit
         */
        void completeHabit(Habit completedHabit);

        /**
         * 激活该habit
         *
         * @param activeHabit
         */
        void activateHabit(Habit activeHabit);


    }

    interface View extends BaseView<Presenter> {
        /**
         * 设置loading指示器.
         *
         * @param active 是否显示刷新进度
         */
        void setLoadingIndicator(boolean active);

        /**
         * 显示所有tasks列表
         *
         * @param habits
         */
        void showHabits(List<Habit> habits);


        /**
         * 显示添加任务界面
         */
        void showAddHabit();

        /**
         * 显示任务详情界面
         *
         * @param habitId 任务的id
         */
        void showHabitDetailsUi(String habitId);

        /**
         * 显示"获取任务失败"
         */
        void showLoadingHabitsError();

        /**
         * 显示"没有任务"
         */
        void showNoHabits();



        /**
         * 是否激活
         *
         * @return
         */
        //Todo 具体用途不明
        //Todo 判断当前view是否可见,避免Presenter中的子线程任务完成后,
        //Todo 回调view来刷新UI时,view已经销毁,发生空指针
        boolean isActive();
    }
}
