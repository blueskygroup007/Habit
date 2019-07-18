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
         * @return
         */
        Map<String, Integer> getActiveHabitList();

        void registerOnControlListener(ForeAlarmPresenter.OnControlListener listener);

        void unregisterOnControlListener(ForeAlarmPresenter.OnControlListener listener);

        /**
         * 激活一个habit
         *
         */
        void activeHabit(String id,int currentSec);

        /**
         * 取消一个habit
         *
         */
        void disableHabit(String id);

        void pauseabit(String id,int currentSec);

//        /**
//         * 启动闹钟
//         *
//         * @param alarm
//         */
//        void startAlarm(Alarm alarm);
//
//        /**
//         * 停止闹钟
//         *
//         * @param alarm
//         */
//        void stopAlarm(Alarm alarm);
//
//        /**
//         * 暂停闹钟
//         *
//         * @param alarm
//         */
//        void pauseAlarm(Alarm alarm);
//
//        /**
//         * 闹钟计时到了
//         *
//         * @param alarm
//         */
//        void onAlarmTimeIsUp(Alarm alarm);
//
//        void onAlarmAccept(Alarm alarm);
//
//        void onAlarmSkip(Alarm alarm);

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
