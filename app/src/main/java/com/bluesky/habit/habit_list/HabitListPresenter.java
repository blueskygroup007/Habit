package com.bluesky.habit.habit_list;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;

import com.bluesky.habit.activity.MainActivity;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.service.ForeAlarmPresenter;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.EspressoIdlingResource;
import com.bluesky.habit.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bluesky.habit.constant.AppConstant.FIRST_LOAD_ON_NETWORK;
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
    private ForeAlarmPresenter.OnControlListener mListener=new ForeAlarmPresenter.OnControlListener() {
        @Override
        public void onHabitProcessed(Habit habit) {
            if (mView.isActive()){
                mView.refreshHabitItem(habit);
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
        //Binder方式启动Alarm
//        LogUtils.d(TAG, "启动一个Habit...");
//        if (mBinder != null) {
//            mBinder.doStartHabit(habit);
//        }

        //Action方式启动Alarm
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_HABIT, habit);
        mContext.startService(intent);

    }

    @Override
    public void cancelHabitAlarm(Habit habit) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(ACTION_STOP);
        intent.putExtra(EXTRA_HABIT, habit);

        mContext.startService(intent);
    }




    @Override
    public void loadHabits(boolean forceUpdate) {
        //首次加载时,强制网络加载
        if (!FIRST_LOAD_ON_NETWORK){
            mFirstLoad=false;
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

                processHabits(tasksToShow);
                //todo 源码这里单独使用了processTasks方法,
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
    public void start() {
        loadHabits(false);
    }

    public void onDestory(){
        mBinder.unregisterOnControlListener(mListener);
    }
}
