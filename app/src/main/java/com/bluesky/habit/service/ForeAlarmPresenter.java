package com.bluesky.habit.service;

import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.source.HabitsRepository;

public class ForeAlarmPresenter implements ForeContract.ForePresenter {
    private final HabitsRepository mRepository;

    public ForeAlarmPresenter(HabitsRepository repository){
        mRepository = repository;

    }
    @Override
    public void startAlarm(Alarm alarm) {

    }

    @Override
    public void stopAlarm(Alarm alarm) {

    }

    @Override
    public void pauseAlarm(Alarm alarm) {

    }

    @Override
    public void onAlarmTimeIsUp(Alarm alarm) {

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
