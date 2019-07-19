package com.bluesky.habit.habit_list;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.service.ForeAlarmPresenter;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.EspressoIdlingResource;
import com.bluesky.habit.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bluesky.habit.constant.AppConstant.FIRST_LOAD_ON_NETWORK;
import static com.bluesky.habit.data.Habit.HABIT_ID;
import static com.bluesky.habit.data.Habit.HABIT_INTERVAL;
import static com.bluesky.habit.service.ForegroundService.ACTION_PLAY;
import static com.bluesky.habit.service.ForegroundService.ACTION_STOP;
import static com.bluesky.habit.service.ForegroundService.EXTRA_HABIT;

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
    private Context mContext;
    private ForegroundService.ForeControlBinder mBinder;
    private ForeAlarmPresenter.OnControlListener mListener = new ForeAlarmPresenter.OnControlListener() {
        @Override
        public void onHabitProcessed(String id, int currentSec) {
            if (mView.isActive()) {
                mView.refreshHabitItem(id, currentSec);
            }
        }

        @Override
        public void onHabitStarted() {
            LogUtils.d(TAG, "HabitStarted回调了...");

        }

        @Override
        public void onHabitPaused() {

        }

        @Override
        public void onHabitStopped() {

        }

        @Override
        public void onHabitAccepted() {

        }

        @Override
        public void onHabitSkipped() {

        }

        @Override
        public void onHabitTimeUp() {

        }

        @Override
        public void onHabitsChanged() {
            loadHabits(false, true);
        }
    };


    public HabitListPresenter(Context context, HabitsRepository repository, ForegroundService.ForeControlBinder binder, HabitListContract.View view) {
        mRepository = repository;
        mView = view;
        mBinder = binder;
        mContext = context;
        mView.setPresenter(this);
        mBinder.registerOnControlListener(mListener);
    }

    /**
     * 手动设置Binder,暂时没有使用
     *
     * @param service
     */
    @Override
    public void setService(Binder service) {
        //todo 这里强转,是否要判断一下
        mBinder = (ForegroundService.ForeControlBinder) service;
        mBinder.registerOnControlListener(mListener);
    }

    @Override
    public void startHabitAlarm(Habit habit) {
        //Action方式启动Alarm
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(HABIT_ID, habit.getId());
        intent.putExtra(HABIT_INTERVAL, habit.getAlarm().getAlarmInterval());
        mContext.startService(intent);

    }

    @Override
    public void cancelHabitAlarm(Habit habit) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(ACTION_STOP);
        intent.putExtra(HABIT_ID, habit.getId());
        intent.putExtra(HABIT_INTERVAL, habit.getAlarm().getAlarmInterval());
        mContext.startService(intent);
    }


    @Override
    public void loadHabits(boolean forceUpdate) {
        //首次加载时,强制网络加载
        if (!FIRST_LOAD_ON_NETWORK) {
            mFirstLoad = false;
        }
        loadHabits(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }


    private void loadHabits(boolean forceUpdate, final boolean showLoadingUI) {
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
                //先清空很重要,经常忘记,导致列表自增长.
                //List<Habit> tasksToShow = new ArrayList<Habit>();//源码这里不只是初始化,且定义为局部变量.更佳.

                List<Habit> mHabitToShow = new ArrayList<>();
                //这个回调可能被调用两次,一次是cache,一次是从服务器中取出数据.
                //所以,在decrement执行前检查,否则会抛出异常
                //如果计数器不为0
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();//Set app as idle
                }

                //用requestType过滤tasks
                for (Habit habit :
                        habits) {
                    mHabitToShow.add(habit);
                }
                //当前view可能无法处理UI更新
                if (!mView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mView.setLoadingIndicator(false);
                }

                processHabits(mHabitToShow);
                //todo 源码这里单独使用了processTasks方法,
                mView.showHabits(mHabitToShow);
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

    private void processHabits(List<Habit> habits) {
        if (habits.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            mView.showNoHabits();
        } else {
            // Show the list of tasks
            mView.showHabits(habits);
            // Set the filter label's text.
            //暂时没有加入该功能
            showFilterLabel();
        }
    }

    /**
     * 显示列表过滤标签
     */
    private void showFilterLabel() {
        //没有做标签分类,所以暂时显示"所有"
        mView.showAllFilterLabel();
//        switch (mCurrentFiltering) {
//            case ACTIVE_TASKS:
//                mTasksView.showActiveFilterLabel();
//                break;
//            case COMPLETED_TASKS:
//                mTasksView.showCompletedFilterLabel();
//                break;
//            default:
//                mTasksView.showAllFilterLabel();
//                break;
//        }
    }

    /**
     * 显示空列表过滤标签
     */
    private void processEmptyHabits() {
//        switch (mCurrentFiltering) {
//            case ACTIVE_TASKS:
//                mTasksView.showNoActiveTasks();
//                break;
//            case COMPLETED_TASKS:
//                mTasksView.showNoCompletedTasks();
//                break;
//            default:
//                mTasksView.showNoTasks();
//                break;
//        }
    }


    @Override
    public void addNewHabit() {
        mView.showAddHabit();
    }

    @Override
    public void openHabitDetails(Habit requestedHabit) {
        mView.showHabitDetailsUi(requestedHabit.getId());
    }

    @Override
    public void completeHabit(Habit completedHabit) {
        Log.d(TAG, "habit paused!!!");
        cancelHabitAlarm(completedHabit);
        mRepository.completeHabit(completedHabit);
        loadHabits(false, false);
    }

    @Override
    public void activateHabit(Habit activeHabit) {
        Log.d(TAG, "habit actived!!!");
        startHabitAlarm(activeHabit);
        mRepository.activateHabit(activeHabit);
        loadHabits(false, false);

    }

    @Override
    public void updateActiveHabitState() {
        Map<String, Integer> activeHabitList = mBinder.getActiveHabitList();
        for (Map.Entry<String, Integer> entry : activeHabitList.entrySet()) {
            mView.refreshHabitItem(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void start() {
        loadHabits(false);
    }

//    @Override
//    public void updateActiveHabitState() {
//        List<Habit> activeHabits = mBinder.getActiveHabitList();
//        if (activeHabits != null && activeHabits.size() > 0) {
//            for (Habit habitNew :
//                    activeHabits) {
//                LogUtils.i(TAG, habitNew.toString());
//
//                //方法一
//                for (Habit habitOld :
//                        mHabitToShow) {
//                    if (habitOld.getId().equals(habitNew.getId())) {
//                        mHabitToShow.set(mHabitToShow.indexOf(habitOld), habitNew);
//                        LogUtils.i(TAG, habitOld.toString());
//                    }
//                }
//            }
//            mView.updateHabits(mHabitToShow);
//        }
//    }

    /**
     * 注销监听器,因为该方法不在契约类中,且fragment声明的是Presenter.所以无法调用.
     * 可以查看Xpressmusic源码有无必要析构,再决定是否调用
     */
    public void onDestory() {
        if (mBinder != null) {
            mBinder.unregisterOnControlListener(mListener);
        }
    }
}
