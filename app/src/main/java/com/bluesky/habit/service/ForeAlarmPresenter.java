package com.bluesky.habit.service;

import android.content.Context;

import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.receiver.AlarmClockReceiver;
import com.bluesky.habit.util.AlarmUtils;

public class ForeAlarmPresenter implements ForeContract.ForePresenter {
    private final HabitsRepository mRepository;
    private final Context mContext;

    public ForeAlarmPresenter(Context context, HabitsRepository repository) {
        mRepository = repository;
        mContext = context;
    }

    @Override
    public void startAlarm(Alarm alarm) {
        AlarmUtils.setAlarm(mContext, AlarmClockReceiver.class,alarm);

    }

    @Override
    public void stopAlarm(Alarm alarm) {
        AlarmUtils.cancelAlarm(mContext, AlarmClockReceiver.class,alarm);
    }

    @Override
    public void pauseAlarm(Alarm alarm) {

    }

    @Override
    public void onAlarmTimeIsUp(Alarm alarm) {

    }

    @Override
    public void onAlarmAccept(Alarm alarm) {

    }

    @Override
    public void onAlarmSkip(Alarm alarm) {

    }

    @Override
    public void stopAccService() {

    }

    @Override
    public void start() {

    }

    @Override
    public void setView(Object view) {

    }

}
