package com.bluesky.habit.habit_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.progresviews.ProgressWheel;
import com.bluesky.habit.R;
import com.bluesky.habit.activity.Main2Activity;
import com.bluesky.habit.activity.MainActivity;
import com.bluesky.habit.constant.AppConstant;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.habit_detail.HabitDetailActivity;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.LogUtils;
import com.bluesky.habit.util.TimeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import ch.ielse.view.SwitchView;

import static com.bluesky.habit.data.Habit.HABIT_ID;
import static com.bluesky.habit.service.ForegroundService.ACTION_ACCEPT;
import static com.bluesky.habit.service.ForegroundService.ACTION_SKIP;
import static com.google.common.base.Preconditions.checkNotNull;


public class HabitFragment extends Fragment implements HabitListContract.View {

    // TODO: Customize parameter argument names
    private static final String TAG = HabitFragment.class.getSimpleName();
    public static final String ARG_BINDER = "foregroundservice_binder";
    private static final int TIME_UP = 1;
    private static final int TIME_UP_FINISHED = 2;
    // TODO: Customize parameters
    private HabitListContract.Presenter mPresenter;
    private HabitAdapter mAdapter;


    // 没有任务的提示框---------

    private View mNoHabitsView;

    private ImageView mNoHabitIcon;

    private TextView mNoHabitMainView;

    private TextView mNoHabitAddView;

    // ------------------
    private LinearLayout mHabitsView;
    private TextView mFilteringLabelView;
    private ListView mListView;
    //    private Toolbar mToolbar;
    private Menu mMenu;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HabitFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HabitFragment newInstance() {
        HabitFragment fragment = new HabitFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.i(TAG, "Fragment onCreate()...");

        super.onCreate(savedInstanceState);

        //Todo 这里必须有一个初始化的0.否则adapter中就会报空指针(必须让getCount是0)
        //todo 经查,是adapter忘记写setTag()方法了
        mAdapter = new HabitAdapter(new ArrayList<>(0), mItemListener);

    }


    @Override
    public void onResume() {
        LogUtils.i(TAG, "Fragment onResume()...");

        super.onResume();

        //更新列表即时状态
        if (mPresenter != null) {
            mPresenter.updateActiveHabitState();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.i(TAG, "Fragment onCreateView()...");

        View root = inflater.inflate(R.layout.fragment_habit_list, container, false);
        //初始化toolbar
//        mToolbar = root.findViewById(R.id.toolbar);
        //初始化任务列表视图
        mListView = root.findViewById(R.id.lv_task_list);
        mListView.setAdapter(mAdapter);
        mFilteringLabelView = root.findViewById(R.id.tv_filtering_lable);
        mHabitsView = root.findViewById(R.id.ll_tasklist);
        //初始化no tasks窗体视图
        mNoHabitsView = root.findViewById(R.id.ll_no_task);
        mNoHabitIcon = root.findViewById(R.id.iv_no_task_icon);
        mNoHabitMainView = root.findViewById(R.id.tv_no_tasks_main);
        mNoHabitAddView = root.findViewById(R.id.tv_no_tasks_add);
        //这个控件是NoTask视图中的add控件,visible状态在别处设置
        mNoHabitAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHabit();
            }
        });

        //
        ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setScrollUpChild(mListView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter != null) {
                    mPresenter.loadHabits(false);
                }
            }
        });


        return root;
    }



    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mHabitsView.setVisibility(View.GONE);
        mNoHabitsView.setVisibility(View.VISIBLE);
        mNoHabitMainView.setText(mainText);
        //todo 使用了contextcompat,源码使用getResources().getDrawable
        mNoHabitIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconRes));
        mNoHabitAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);

    }

    @Override
    public void setLoadingIndicator(boolean active) {
        //getView获取的是fragment的onCreateView返回的rootview
        if (getView() == null) {
            return;
        }
        //todo -----------------------当前进度
        //todo 主线---生成V和P的实现类
        //todo 支线---替换RecyclerView为SwipeRefreshLayout以支持下拉刷新
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
        //确保setRefreshing()在布局完成后再被调用
        //todo View.post()
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showHabits(List<Habit> habits) {
        mAdapter.replaceData(habits);
        mHabitsView.setVisibility(View.VISIBLE);
        mNoHabitsView.setVisibility(View.GONE);
    }

    @Override
    public void showAddHabit() {

    }

    @Override
    public void showHabitDetailsUi(String habitId) {
        Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        intent.putExtra(HabitDetailActivity.EXTRA_HABIT_ID, habitId);
        startActivity(intent);
    }

    @Override
    public void showLoadingHabitsError() {

    }

    @Override
    public void showNoHabits() {
        showNoTasksViews(getString(R.string.no_habits_all), R.drawable.ic_check_circle_24dp, false);

    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(R.string.label_active);
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(R.string.label_completed);

    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(R.string.label_all);

    }

    @Override
    public void showNoActiveHabits() {
        showNoTasksViews(getResources().getString(R.string.no_habits_active), R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showNoCompletedHabits() {
        showNoTasksViews(getResources().getString(R.string.no_habits_completed), R.drawable.ic_check_circle_24dp, false);

    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_habit_message));
    }

    /**
     * 在Snackbar上显示一条吐司信息
     *
     * @param message
     */
    private void showMessage(String message) {

        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        //todo 判断fragment是否被添加到activity上,即是否为当前活动的fragment
        return isAdded();
    }

    @Override
    public void refreshHabitItem(String id, int currentSec) {
        updateItem(mListView, id, currentSec);

    }

    @Override
    public void updateHabits(List<Habit> habits) {
        mAdapter.replaceData(habits);
    }

    @Override
    public void showTimeUpButtons(String id, String title) {
/*        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Main2Activity) getActivity()).showTimeUpButtons(id, title);
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMenu != null) {
                    MenuItem itemAccept = mMenu.findItem(R.id.menu_timeup_accept);
                    itemAccept.setVisible(true);
                    Intent intentAccept = new Intent(getActivity(), ForegroundService.class);
                    intentAccept.setAction(ACTION_ACCEPT);
                    intentAccept.putExtra(HABIT_ID, id);
                    itemAccept.setIntent(intentAccept);
                    itemAccept.setTitle(title);

                    mMenu.findItem(R.id.menu_timeup_skip).setVisible(true);
                    Intent intentSkip = new Intent(getActivity(), ForegroundService.class);
                    intentSkip.setAction(ACTION_SKIP);
                    intentSkip.putExtra(HABIT_ID, id);
                    mMenu.findItem(R.id.menu_timeup_skip).setIntent(intentSkip);
                }
            }
        });*/
    }

    @Override
    public void hideTimeUpButtons(String id) {
/*        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMenu != null) {
                    mMenu.findItem(R.id.menu_timeup_accept).setVisible(false);
                    mMenu.findItem(R.id.menu_timeup_skip).setVisible(false);
                }
            }
        });*/
    }


    /**
     * 单独更新某个listview的item显示,并更新数据源
     *
     * @param listView
     */
    private void updateItem(ListView listView, String id, int currentSec) {
        if (listView != null) {
            int first = listView.getFirstVisiblePosition();
            int end = listView.getLastVisiblePosition();
            int position = 0;
            for (int i = first; i <= end; i++) {
                if (id.equals(((Habit) listView.getItemAtPosition(i)).getId())) {
                    position = i;
                    break;
                }
            }
            if (position >= first && position <= end) {
                View view = listView.getChildAt(position - first);
                HabitAdapter.ViewHolder holder = (HabitAdapter.ViewHolder) view.getTag();
                String currentTime = TimeUtils.secToTime(currentSec);
                int interval = ((Habit) listView.getItemAtPosition(position)).getAlarm().getAlarmInterval();
                final int pos = position;
                holder.pb_time.post(new Runnable() {
                    @Override
                    public void run() {
                        //todo 防止currentSec为0
                        holder.pb_time.setPercentage(currentSec * 360 / interval);
                        holder.pb_time.setStepCountText(currentTime);
                        //csdn上提到的google推荐的刷新item方法
//                        mAdapter.getView(pos, view, listView);
                    }
                });
//                mAdapter.updateItem(habit);

            } else {
                //todo 如果目标位置不在显示区域
//                mAdapter.updateItem(habit);

            }

        }
    }

    @Override
    public void onHabitStarted(String id) {
        onOpenOrCloseHabit(mListView, id, true);
    }

    @Override
    public void onHabitStoped(String id) {
        onOpenOrCloseHabit(mListView, id, false);
    }

    private void onOpenOrCloseHabit(ListView listView, String id, boolean onOff) {
        if (listView != null) {
            int first = listView.getFirstVisiblePosition();
            int end = listView.getLastVisiblePosition();
            int position = 0;
            for (int i = first; i <= end; i++) {
                if (id.equals(((Habit) listView.getItemAtPosition(i)).getId())) {
                    position = i;
                    break;
                }
            }
            if (position >= first && position <= end) {
                View view = listView.getChildAt(position - first);
                HabitAdapter.ViewHolder holder = (HabitAdapter.ViewHolder) view.getTag();
                holder.switch_completed.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.switch_completed.setOpened(onOff);
                    }
                });
                holder.pb_time.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb_time.setPercentage(0);
                    }
                });
            } else {

            }
        }
    }

    @Override
    public void setPresenter(HabitListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
        mPresenter.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unRegister();
    }

    private class HabitAdapter extends BaseAdapter {

        private List<Habit> mHabits;
        private ItemListener mItemListener;
        public SwitchButton.OnCheckedChangeListener mSwitchButtonOnChangeListener;

        public HabitAdapter(List<Habit> habits, ItemListener itemListener) {
            mHabits = habits;
            mItemListener = itemListener;
        }

        /**
         * 重设所有数据,并刷新
         *
         * @param habits
         */
        public void replaceData(List<Habit> habits) {
            setList(habits);
            LogUtils.i(TAG, "habit列表个数是:......" + habits.size());
            notifyDataSetChanged();
        }

        /**
         * 重设单个数据(因为item对应的条目不在当前显示区域)
         *
         * @param modifyHabit
         */
        public void updateItem(Habit modifyHabit) {
            for (Habit habit :
                    mHabits) {
                if (habit.getId().equals(modifyHabit.getId())) {
                    mHabits.set(mHabits.indexOf(habit), modifyHabit);
                    notifyDataSetChanged();
                }
            }
        }

        private void setList(List<Habit> habits) {
            mHabits = checkNotNull(habits);
        }


        @Override
        public int getCount() {
            return mHabits.size();
        }

        @Override
        public Habit getItem(int position) {
            return mHabits.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_habit_final, parent, false);
                holder.root = convertView.findViewById(R.id.constraintlayout);
                holder.pb_time = convertView.findViewById(R.id.pb_time);
                holder.switch_completed = convertView.findViewById(R.id.switch_completed);
                holder.iv_icon = convertView.findViewById(R.id.iv_icon);
                holder.tv_title = convertView.findViewById(R.id.tv_title);
                holder.tv_description = convertView.findViewById(R.id.tv_description);
                holder.pb_number = convertView.findViewById(R.id.pb_number);
                convertView.setTag(holder);//todo 很重要且很容易忘记的,会造成空指针
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Habit habit = getItem(position);

            holder.pb_time.setDefText("当前:");
            long ms = habit.getAlarm().getAlarmCurrent();
            String currentTime = TimeUtils.secToTime(Long.valueOf(ms).intValue());
            holder.pb_time.setStepCountText(currentTime);
            holder.pb_time.setPercentage(0);

            LogUtils.i("TEST", "开始三次开关....");
            holder.switch_completed.setOpened(habit.isActive());

            holder.switch_completed.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    LogUtils.i("TEST", "OnStateChangedListener--ON--isPressed=" + view.isPressed());
                    view.toggleSwitch(true);
                    mItemListener.onActivateTaskClick(habit);
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    LogUtils.i("TEST", "OnStateChangedListener--OFF--isPressed=" + view.isPressed());
                    view.toggleSwitch(false);
                    mItemListener.onCompleteTaskClick(habit);
                }
            });

            holder.iv_icon.setImageResource(AppConstant.HABIT_ICONS[habit.getIcon()]);
            holder.tv_title.setText(habit.getTitle());
            holder.tv_description.setText(habit.getDescription());
            holder.pb_number.setMax(habit.getAlarm().getNumberCount());
            holder.pb_number.setProgress(habit.getAlarm().getNumberCurrent());

            //修改item的背景变化
            convertView.setSelected(habit.isActive());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开编辑界面
                    mItemListener.onTaskClick(habit);
                }
            });

            return convertView;
        }


        class ViewHolder {
            ConstraintLayout root;
            ProgressWheel pb_time;
            SwitchView switch_completed;
            ImageView iv_icon;
            TextView tv_title;
            TextView tv_description;
            ProgressBar pb_number;
        }
    }

    public interface ItemListener {
        void onTaskClick(Habit clickedHabit);

        void onCompleteTaskClick(Habit completedHabit);

        void onActivateTaskClick(Habit activatedHabit);
    }

    ItemListener mItemListener = new ItemListener() {
        @Override
        public void onTaskClick(Habit clickedHabit) {
            mPresenter.openHabitDetails(clickedHabit);
        }

        @Override
        public void onCompleteTaskClick(Habit completedHabit) {
            //todo 这里只是启动和取消alarm了.还没有更新Repository,也没有更新列表(列表有分类显示:活动的,暂停的)
            //todo 也没有刷新列表
//            mPresenter.completeHabit(completedHabit);
            mPresenter.cancelHabitAlarm(completedHabit);
        }

        @Override
        public void onActivateTaskClick(Habit activatedHabit) {
//            mPresenter.activateHabit(activatedHabit);
            mPresenter.startHabitAlarm(activatedHabit);
        }
    };

}
