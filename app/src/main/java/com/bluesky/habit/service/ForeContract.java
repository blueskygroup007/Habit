package com.bluesky.habit.service;

import com.bluesky.habit.base.BasePresenter;
import com.bluesky.habit.base.BaseView;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;

import java.util.List;

public interface ForeContract {

    interface ForePresenter extends BasePresenter {
        //        public void addActiveHabit(Habit habit);
//        public void deleteActiveHabit(Habit habit);
        void setActiveHabitList(List<Habit> list);

        List<Habit> getActiveHabitList();

        void registerOnControlListener(ForeAlarmPresenter.OnControlListener listener);

        void unregisterOnControlListener(ForeAlarmPresenter.OnControlListener listener);

        /**
         * 激活一个habit
         *
         * @param habit
         */
        void activeHabit(Habit habit);

        /**
         * 取消一个habit
         *
         * @param habit
         */
        void disableHabit(Habit habit);

        void pauseabit(Habit habit);

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
