package com.bluesky.habit.service;

import com.bluesky.habit.base.BasePresenter;
import com.bluesky.habit.base.BaseView;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;

import java.util.List;
import java.util.Map;

public interface ForeContract {

    interface ForePresenter extends BasePresenter {
        /**
         * 给后台service一个激活了的habit列表,暂时不需要,可以通过activeHabit逐个激活
         *
         */
//        void setActiveHabitList(List<String,Integer> list);

        /**
         * 得到激活列表
         *
         * @return
         */
        Map<String, Integer> getActiveHabitList();

        void registerOnControlListener(ForeAlarmPresenter.OnControlListener listener);

        void unregisterOnControlListener(ForeAlarmPresenter.OnControlListener listener);

        /**
         * 激活一个habit
         */
        void activeHabit(String id, int currentSec, int interval);

        /**
         * 重启一个habit
         *
         * @param id
         */
        void reactiveHabit(String id);

        /**
         * 取消一个habit
         */
        void disableHabit(String id);

        void pauseabit(String id, int currentSec);

        /**
         * 增加一个habit
         */
        void addOrUpdateHabit(Habit habit);

        /**
         * 删除一个habit
         */
        void deleteHabit(String id);

        /**
         * 习惯时间到
         */
        void onAlarmTimeIsUp(String id);

        /**
         * 确认习惯
         *
         * @param id
         */
        void onAlarmAccept(String id);

        /**
         * 跳过习惯
         *
         * @param id
         */
        void onAlarmSkip(String id);

        /**
         * 停止加速度监听服务
         */
        void stopAccService();


        void loadHabits(boolean forceUpdate, HabitsDataSource.LoadHabitsCallback loadHabitsCallback);
    }

//    interface ForeService extends BaseView<ForePresenter>{}



    /*    interface ForeAlertView extends BaseView<ForePresenter> {
     *//**
     * 显示闹钟到期提醒对话框
     *//*
        void showAlarmDialog();

        *//**
     * 关闭闹钟到期提醒对话框
     *//*
        void closeAlarmDialog();

        *//*
        判断activity是否还存活
         *//*
        boolean isActive();
    }*/
}
