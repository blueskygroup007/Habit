package com.bluesky.habit.habit_list;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.progresviews.ProgressWheel;
import com.bluesky.habit.R;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.HabitsDataSource;
import com.bluesky.habit.data.source.HabitsRepository;
import com.bluesky.habit.habit_list.dummy.DummyContent.DummyItem;
import com.bluesky.habit.util.Injection;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    // TODO: Customize parameters
    private int mColumnCount = 1;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HabitFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HabitFragment newInstance(int columnCount) {
        HabitFragment fragment = new HabitFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        //Todo 这里必须有一个初始化的0.否则adapter中就会报空指针(必须让getCount是0)
        //todo 经查,是adapter忘记写setTag()方法了
        mAdapter = new HabitAdapter(new ArrayList<>(0), mItemListener);
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
        ListView listView = root.findViewById(R.id.lv_task_list);
        listView.setAdapter(mAdapter);
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

        return root;
    }

    /**
     * 测试代码:当fragment的视图都创建完毕,传递真正的列表数据进去
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HabitsRepository repository = Injection.provideTasksRepository(getContext());
        repository.getHabits(new HabitsDataSource.LoadHabitsCallback() {
            @Override
            public void onHabitsLoaded(List<Habit> habits) {
                mAdapter.replaceData(habits);
                Log.e(TAG, habits.toString());
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "取数据失败................");
            }
        });

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

    }

    @Override
    public void showLoadingHabitsError() {

    }

    @Override
    public void showNoHabits() {
        showNoTasksViews(getString(R.string.no_habits_all), R.drawable.ic_check_circle_24dp, false);

    }


    @Override
    public boolean isActive() {
        //todo 判断fragment是否被添加到activity上,即是否为当前活动的fragment
        return isAdded();
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
        private ItemListener mItemListener;

        public HabitAdapter(List<Habit> habits, ItemListener itemListener) {
            mHabits = habits;
            mItemListener = itemListener;
        }

        public void replaceData(List<Habit> habits) {
            setList(habits);
            notifyDataSetChanged();
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
            //TODO ----------当前进度-----------------
            final Habit habit = getItem(position);

            //holder.pb_time.setMax(habit.getAlarm().getInterval_time());
            //holder.pb_time.setProgress(habit.getAlarm().getCurrent_time());
            holder.pb_time.setDefText("当前:");
            holder.pb_time.setStepCountText(habit.getAlarm().getCurrent_time() + "");
            holder.pb_time.setPercentage(habit.getAlarm().getCurrent_time() * 100 / habit.getAlarm().getInterval_time());

            holder.switch_completed.setChecked(habit.isCompleted());
            holder.iv_icon.setImageResource(habit.getIcon());
            holder.tv_title.setText(habit.getTitle());
            holder.tv_description.setText(habit.getDescription());
            holder.pb_number.setMax(habit.getAlarm().getCount_number());
            holder.pb_number.setProgress(habit.getAlarm().getCurrent_number());
            //修改item的背景变化
            convertView.setSelected(habit.isCompleted());

            holder.switch_completed.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if (isChecked) {
                        mItemListener.onCompleteTaskClick(habit);
                    } else {
                        mItemListener.onActivateTaskClick(habit);
                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onTaskClick(habit);
                }
            });
            return convertView;
        }

        class ViewHolder {
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
            mPresenter.completeHabit(completedHabit);
        }

        @Override
        public void onActivateTaskClick(Habit activatedHabit) {
            mPresenter.activateHabit(activatedHabit);
        }
    };
}
