package com.bluesky.habit.habit_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.progresviews.ProgressWheel;
import com.bluesky.habit.R;
import com.bluesky.habit.habit_detail.HabitDetailActivity;
import com.bluesky.habit.constant.AppConstant;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.habit_list.dummy.DummyContent.DummyItem;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.LogUtils;
import com.bluesky.habit.util.TimeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HabitFragment extends Fragment implements HabitListContract.View {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = HabitFragment.class.getSimpleName().toString();
    public static final String ARG_BINDER = "foregroundservice_binder";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ForegroundService.ForeControlBinder mBinder;
    private HabitListContract.Presenter mPresenter;
    private OnListFragmentInteractionListener mListener;
    private ScrollChildSwipeRefreshLayout mRecyclerView;
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


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HabitFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HabitFragment newInstance(ForegroundService.ForeControlBinder binder) {
        HabitFragment fragment = new HabitFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BINDER, binder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Todo 这里必须有一个初始化的0.否则adapter中就会报空指针(必须让getCount是0)
        //todo 经查,是adapter忘记写setTag()方法了
        mAdapter = new HabitAdapter(new ArrayList<>(0), mItemListener);
        if (savedInstanceState != null) {
            mBinder = (ForegroundService.ForeControlBinder) savedInstanceState.getSerializable(ARG_BINDER);
        } else {

            if (getArguments() != null) {
                mBinder = (ForegroundService.ForeControlBinder) getArguments().getSerializable(ARG_BINDER);
            }
        }


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(ARG_BINDER, mBinder);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //P的启动入口

        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_habit_list, container, false);
        //初始化任务列表视图
        mListView = root.findViewById(R.id.lv_task_list);
        mListView.setAdapter(mAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position >= 0) {
//                    mItemListener.onTaskClick(mAdapter.mHabits.get(position));
//                }
//            }
//        });
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
                mPresenter.loadHabits(false);
            }
        });
        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void refreshHabitItem(Habit habit) {
        //todo 两种实现方法:
//        mAdapter.updateView(int,ListView);
        updateItem(mListView, habit);
    }


    /**
     * 单独更新某个listview的item显示,并更新数据源
     *
     * @param listView
     * @param habit
     */
    private void updateItem(ListView listView, Habit habit) {
        if (listView != null) {
            int first = listView.getFirstVisiblePosition();
            int end = listView.getLastVisiblePosition();
            int position = 0;
            for (int i = first; i <= end; i++) {
                if (habit.getId().equals(((Habit) listView.getItemAtPosition(i)).getId())) {
                    position = i;
                    break;
                }
            }
            if (position >= first && position <= end) {
                View view = listView.getChildAt(position - first);
                HabitAdapter.ViewHolder holder = (HabitAdapter.ViewHolder) view.getTag();
                long ms = habit.getAlarm().getAlarmCurrent();
                String currentTime = TimeUtils.secToTime(new Long(ms / 1000).intValue());
                holder.pb_time.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb_time.setPercentage(habit.getAlarm().getAlarmCurrent() * 360 / habit.getAlarm().getAlarmInterval());
                        holder.pb_time.setStepCountText(currentTime);
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
    public void setPresenter(HabitListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }


    private class HabitAdapter extends BaseAdapter {

        private List<Habit> mHabits;
        private Map<String, Habit> mHabitMap = new ArrayMap<>();
        private ItemListener mItemListener;
        private SwitchButton.OnCheckedChangeListener mSwitchButtonOnChangeListener;

        public HabitAdapter(List<Habit> habits, ItemListener itemListener) {
            mHabits = habits;
            for (Habit habit : habits
            ) {
                mHabitMap.put(habit.getId(), habit);
            }
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
            mSwitchButtonOnChangeListener = new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
//                    //判断是不是点击触发的，当setChecked()时则不会触发此listener
//                    //但是,因为SwitchButton是第三方控件,可能是因为继承于view,而没有实现ClickAble,所以isPressed不能返回true.
//                    if (!view.isPressed()) {
//                        return;
//                    }
                    if (isChecked) {
                        mItemListener.onActivateTaskClick(habit);
                    } else {
                        mItemListener.onCompleteTaskClick(habit);
                    }
                }
            };


            holder.pb_time.setDefText("当前:");
            long ms = habit.getAlarm().getAlarmCurrent();
            String currentTime = TimeUtils.secToTime(new Long(ms / 1000).intValue());
            holder.pb_time.setStepCountText(currentTime);
            holder.pb_time.setPercentage(habit.getAlarm().getAlarmCurrent() * 360 / habit.getAlarm().getAlarmInterval());
            //先取消监听
            holder.switch_completed.setOnCheckedChangeListener(null);
            holder.switch_completed.setChecked(habit.isActive());
            //设置状态后再开启监听
            holder.switch_completed.setOnCheckedChangeListener(mSwitchButtonOnChangeListener);
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
//            holder.root.setSelected(habit.isActive());
//            holder.root.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mItemListener.onTaskClick(habit);
//                }
//            });

            return convertView;
        }


        class ViewHolder {
            ConstraintLayout root;
            ProgressWheel pb_time;
            SwitchButton switch_completed;
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
            mPresenter.completeHabit(completedHabit);
//            mPresenter.cancelHabitAlarm(completedHabit);
        }

        @Override
        public void onActivateTaskClick(Habit activatedHabit) {
            mPresenter.activateHabit(activatedHabit);
//            mPresenter.startHabitAlarm(activatedHabit);
        }
    };
}
