package com.bluesky.habit.habit_list;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.service.ForeAlarmPresenter;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.EspressoIdlingResource;
import com.bluesky.habit.util.LogUtils;
import com.bluesky.habit.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bluesky.habit.constant.AppConstant.CONFIG_FIRST_LOAD_ON_NETWORK;
import static com.bluesky.habit.data.Habit.HABIT_ID;
import static com.bluesky.habit.data.Habit.HABIT_INTERVAL;
import static com.bluesky.habit.service.ForegroundService.ACTION_ACCEPT;
import static com.bluesky.habit.service.ForegroundService.ACTION_PLAY;
import static com.bluesky.habit.service.ForegroundService.ACTION_SKIP;
import static com.bluesky.habit.service.ForegroundService.ACTION_STOP;

/**
 * @author BlueSky
 * @date 2019/4/27
 * Description:
 */
public class HabitListPresenter implements HabitListContract.Presenter {

    private static final String TAG = "HabitListPresenter";
    private final HabitsRepository mRepository;
    private HabitListContract.View mView;
    private Context mContext;
    private ForegroundService.ForeControlBinder mBinder;

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(TAG, "onServiceConnected()...");
            mBinder = (ForegroundService.ForeControlBinder) service;
            mBinder.registerOnControlListener(mListener);
        }

        //绑定失败
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e(TAG, "onServiceDisconnected()...");
        }
    };

    private ForeAlarmPresenter.OnControlListener mListener = new ForeAlarmPresenter.OnControlListener() {
        @Override
        public void onHabitProcessed(String id, int currentSec) {
            if (mView.isActive()) {
                mView.refreshHabitItem(id, currentSec);
            }
        }

        @Override
        public void onHabitStarted(String id) {
            LogUtils.d(TAG, "HabitStarted回调了...");
            mView.onHabitStarted(id);
        }

        @Override
        public void onHabitPaused(String id) {

        }

        @Override
        public void onHabitStopped(String id) {
            mView.onHabitStoped(id);
        }

        @Override
        public void onHabitAccepted(String id) {
            mView.hideTimeUpButtons(id);
        }

        @Override
        public void onHabitSkipped(String id) {
            mView.hideTimeUpButtons(id);
        }

        @Override
        public void onHabitTimeUp(String id) {
            mView.showTimeUpButtons(id, mRepository.getTaskWithId(id).getTitle());
        }

        @Override
        public void onHabitsChanged(String id) {
            loadHabits(false, true);
        }
    };


    public HabitListPresenter(Context context, HabitsRepository repository, HabitListContract.View view) {
        mRepository = repository;
        mView = view;
        mContext = context;
        mView.setPresenter(this);
        context.bindService(new Intent(context, ForegroundService.class), mConn, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void register() {
        if (mBinder != null) {
            mBinder.registerOnControlListener(mListener);
        }
    }

    @Override
    public void unRegister() {
        if (mBinder != null) {
            mBinder.unregisterOnControlListener(mListener);
            mContext.unbindService(mConn);

        }
    }

    @Override
    public void unBind() {
        mContext.unbindService(mConn);
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
        boolean isFirstRun = PreferenceUtils.getBoolean(CONFIG_FIRST_LOAD_ON_NETWORK, true);
        if (isFirstRun) {
            PreferenceUtils.putBoolean(CONFIG_FIRST_LOAD_ON_NETWORK, false);
        }
        loadHabits(forceUpdate || isFirstRun, true);
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

                    LogUtils.i(TAG, "Habit=  " + habit.toString() );
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
        if (mBinder != null) {
            Map<String, Integer> activeHabitList = mBinder.getActiveHabitList();
            for (Map.Entry<String, Integer> entry : activeHabitList.entrySet()) {
                mView.refreshHabitItem(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void accept(String id) {
        mView.hideTimeUpButtons(id);
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(ACTION_ACCEPT);
        intent.putExtra(HABIT_ID, id);
        mContext.startService(intent);
    }

    @Override
    public void skip(String id) {
        mView.hideTimeUpButtons(id);
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(ACTION_SKIP);
        intent.putExtra(HABIT_ID, id);
        mContext.startService(intent);
    }

    @Override
    public void start() {
        loadHabits(false);
    }

}
