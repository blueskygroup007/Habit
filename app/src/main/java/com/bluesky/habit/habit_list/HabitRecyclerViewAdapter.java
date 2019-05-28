package com.bluesky.habit.habit_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bluesky.habit.R;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/4/24
 * Description:
 */
public class HabitRecyclerViewAdapter extends RecyclerView.Adapter<HabitRecyclerViewAdapter.VH> {

    public static class VH extends RecyclerView.ViewHolder {

        public final View mRoot;
        public final ProgressBar pb_time;
        public final Switch switch_completed;
        public final ImageView iv_icon;
        public final TextView tv_title;
        public final TextView tv_description;
        public final ProgressBar pb_number;

        public VH(@NonNull View root) {
            super(root);
            mRoot = root;
            pb_time = root.findViewById(R.id.pb_time);
            switch_completed = root.findViewById(R.id.switch_completed);
            iv_icon = root.findViewById(R.id.iv_icon);
            tv_title = root.findViewById(R.id.tv_title);
            tv_description = root.findViewById(R.id.tv_description);
            pb_number = root.findViewById(R.id.pb_number);
        }
    }

    private List<Habit> mHabits;

    /**
     * 构造时,只传了一个空data
     * 如果需要交互,可以传Listener进来
     * Listener可以是adapter与fragment交互,也可以是和Activity交互
     *
     * @param habits
     */
    public HabitRecyclerViewAdapter(List<Habit> habits) {
        mHabits = habits;
    }

    @NonNull
    @Override
    public HabitRecyclerViewAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new VH(root);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitRecyclerViewAdapter.VH holder, int position) {
        final Habit habit = mHabits.get(position);
        holder.switch_completed.setChecked(habit.isCompleted());
        holder.iv_icon.setImageResource(habit.getIcon());
        holder.tv_title.setText(habit.getTitle());
        holder.tv_description.setText(habit.getDescription());
        holder.mRoot.setSelected(habit.isCompleted());
        if (habit.getAlarm() == null) {
            Alarm alarm = new Alarm(100, 50, 30, 10, 20, 0, 0);
            habit.setAlarm(alarm);
        }

        holder.pb_time.setMax(habit.getAlarm().getInterval_time());
        holder.pb_time.setProgress(habit.getAlarm().getCurrent_time());
        holder.pb_number.setMax(habit.getAlarm().getCount_number());
        holder.pb_number.setProgress(habit.getAlarm().getCurrent_number());
    }

    @Override
    public int getItemCount() {
        return mHabits.size();
    }

    /**
     * 给adapter传真正的data,并刷新
     *
     * @param habits
     */
    public void replaceData(List<Habit> habits) {
        mHabits = checkNotNull(habits);
        notifyDataSetChanged();
    }
}
