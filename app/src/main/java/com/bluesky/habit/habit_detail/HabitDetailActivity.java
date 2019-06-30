package com.bluesky.habit.habit_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.bluesky.habit.R;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.databinding.ActivityDetailBinding;
import com.bluesky.habit.util.Injection;

/**
 * @author BlueSky
 * @date 2019/6/24
 * Description:
 */
public class HabitDetailActivity extends AppCompatActivity implements HabitDetailContract.View {

    public static final String EXTRA_HABIT_ID = "TASK_ID";
    private Toolbar mToolbar;
    private String mHabitId;
    private HabitDetailPresenter mPresenter;
    private boolean mIsModify = false;
    private ActivityDetailBinding mBinding;

    private boolean isForeground = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);


        initData();
        initView();
        initEvent();
        mPresenter.start();
    }

    private void initData() {
        Intent intent = getIntent();
        mHabitId = intent.getStringExtra(EXTRA_HABIT_ID);
        mPresenter = new HabitDetailPresenter(mHabitId, Injection.provideTasksRepository(this), this);

    }

    private void initView() {
        //toolbar和左上角返回按钮
//        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


    }

    private void initEvent() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://左上角的返回按钮
                finish();
        }
        return true;
    }

    /**
     * 每次显示菜单之前的加载预处理
     *
     * @param menu
     * @return 返回true, 表示显示菜单
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsModify) {
            menu.findItem(R.id.menu_detail_edit).setVisible(true);
            menu.findItem(R.id.menu_detail_finish).setVisible(false);
            mIsModify = false;
        } else {
            menu.findItem(R.id.menu_detail_edit).setVisible(false);
            menu.findItem(R.id.menu_detail_finish).setVisible(true);
            mIsModify = true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public void setPresenter(HabitDetailContract.Presenter presenter) {

    }

    @Override
    public void showHabit(Habit habit) {
        mBinding.etTitle.setText(habit.getTitle());
        mBinding.etContent.setText(habit.getDescription());
        Alarm alarm = habit.getAlarm();
        mBinding.seekBar.setProgress(alarm.getAlarmInterval() / 1000 / 60);//求几分钟
    }

    @Override
    public boolean isActive() {

        return isForeground;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }
}
