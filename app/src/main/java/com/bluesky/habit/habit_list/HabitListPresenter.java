package com.bluesky.habit.habit_list;

import android.os.Binder;
import android.util.Log;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BlueSky
 * @date 2019/4/27
 * Description:
 */
public class HabitListPresenter implements HabitListContract.Presenter {

    private static final String TAG = "HabitListPresenter";
    private final HabitsRepository mRepository;

    private HabitListContract.View mView;


    private boolean mFirstLoad = true;
    private ForegroundService.ForeControlBinder mBinder;

    public HabitListPresenter(HabitsRepository repository, HabitListContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void setService(Binder service) {
        //todo 这里强转,是否要判断一下
        mBinder = (ForegroundService.ForeControlBinder) service;
    }

    @Override
    public void startHabitAlarm(Habit habit) {
        if (mBinder != null) {
            mBinder.doStartHabit(habit);
        }
    }

    @Override
    public void loadHabits(boolean forceUpdate) {
        //首次加载时,强制网络加载
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mRepository.refreshHabits();
        }
        //App进入阻塞
        EspressoIdlingResource.increment();//App is busy
        //接下来执行耗时操作
        mRepository.getHabits(new HabitsDataSource.LoadHabitsCallback() {
            @Override
            public void onHabitsLoaded(List<Habit> habits) {
                List<Habit> tasksToShow = new ArrayList<>();

                //这个回调可能被调用两次,一次是cache,一次是从服务器中取出数据.
                //所以,在decrement执行前检查,否则会抛出异常
                //如果计数器不为0
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();//Set app as idle
                }

                //用requestType过滤tasks
                for (Habit habit :
                        habits) {
                    tasksToShow.add(habit);
                }
                //当前view可能无法处理UI更新
                if (!mView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mView.setLoadingIndicator(false);
                }
                mView.showHabits(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                //当前view可能无法处理UI更新
                if (!mView.isActive()) {
                    return;
                }
                mView.showLoadingHabitsError();
            }
        });
    }


    @Override
    public void addNewHabit() {

    }

    @Override
    public void openHabitDetails(Habit requestedHabit) {

    }

    @Override
    public void completeHabit(Habit completedHabit) {
        Log.d(TAG, "habit paused!!!");

    }

    @Override
    public void activateHabit(Habit activeHabit) {
        Log.d(TAG, "habit actived!!!");
    }

    @Override
    public void start() {
        loadHabits(false);
    }

    @Override
    public void setView(HabitListContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }


}
