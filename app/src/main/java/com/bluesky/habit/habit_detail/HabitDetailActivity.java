package com.bluesky.habit.habit_detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.bluesky.habit.R;
import com.bluesky.habit.constant.AppConstant;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.databinding.ActivityDetailBinding;
import com.bluesky.habit.util.Injection;
import com.bluesky.habit.util.LogUtils;

/**
 * @author BlueSky
 * @date 2019/6/24
 * Description:
 */
public class HabitDetailActivity extends AppCompatActivity implements HabitDetailContract.View, CompoundButton.OnCheckedChangeListener {

    public static final String EXTRA_HABIT_ID = "TASK_ID";
    private static final String TAG = HabitDetailActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private LinearLayout mViewMark;

    private String mHabitId;
    private HabitDetailPresenter mPresenter;
    private ActivityDetailBinding mBinding;

    private boolean isForeground = true;
    private Habit mHabit;
    private MenuItem mMenuItemEdit;
    private MenuItem mMenuItemFinish;

    /**
     * //todo 任务:
     * 1.Service绑定相关:Binder和Connection
     * 2.Presenter的相关方法改写为Binder.xxx()
     *
     * @param savedInstanceState
     */
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
        mViewMark = findViewById(R.id.view_mark);

        //toolbar和左上角返回按钮
//        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


    }

    private void initEvent() {
        mBinding.cbAlertRing.setOnCheckedChangeListener(this);
        mBinding.cbAlertLight.setOnCheckedChangeListener(this);
        mBinding.cbAlertVibrate.setOnCheckedChangeListener(this);
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBinding.tvSeekbar.setText(progress + "分钟");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!(mBinding.cbAlertRing.isChecked() | mBinding.cbAlertLight.isChecked() | mBinding.cbAlertVibrate.isChecked())) {
            buttonView.setChecked(!isChecked);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://左上角的返回按钮
                onBackPressed();
                break;
            case R.id.menu_detail_edit:
                mViewMark.setVisibility(View.GONE);
                mMenuItemEdit.setVisible(false);
                mMenuItemFinish.setVisible(true);
                break;
            case R.id.menu_detail_finish:
                mViewMark.setVisibility(View.VISIBLE);
                mViewMark.requestFocus();
                mMenuItemEdit.setVisible(true);
                mMenuItemFinish.setVisible(false);
                break;
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
        mMenuItemEdit = menu.findItem(R.id.menu_detail_edit).setVisible(true);
        mMenuItemFinish = menu.findItem(R.id.menu_detail_finish).setVisible(false);
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
        mHabit = habit;
        mBinding.etTitle.setText(habit.getTitle());
        mBinding.etContent.setText(habit.getDescription());
        Alarm alarm = habit.getAlarm();
        //求几分钟
        mBinding.seekBar.setProgress(alarm.getAlarmInterval() / 60);
        mBinding.tvSeekbar.setText(alarm.getAlarmInterval() / 60 + "分钟");
        mBinding.cbAlertRing.setChecked(alarm.getWakeStyle() == AppConstant.WakeStyle.RING);
        mBinding.cbAlertLight.setChecked(alarm.getWakeStyle() == AppConstant.WakeStyle.LIGHT);
        mBinding.cbAlertVibrate.setChecked(alarm.getWakeStyle() == AppConstant.WakeStyle.VIBRATE);
        int indexFeedback = alarm.getAcceptStyle();
        int indexDelay = alarm.getDelayStyle();
        switch (indexFeedback) {
            case 1:
                mBinding.rgAccept.check(R.id.rb_accept_shake);

                break;
            case 2:
                mBinding.rgAccept.check(R.id.rb_accept_turnoff);

                break;
            case 3:
                mBinding.rgAccept.check(R.id.rb_accept_cover);

                break;
            default:
                mBinding.rgAccept.check(R.id.rb_accept_shake);
        }

        switch (indexDelay) {
            case 1:
                mBinding.rgDelay.check(R.id.rb_delay_shake);
                break;
            case 2:
                mBinding.rgDelay.check(R.id.rb_delay_turnoff);

                break;
            case 4:
                mBinding.rgDelay.check(R.id.rb_delay_cover);

                break;
            default:
                mBinding.rgDelay.check(R.id.rb_delay_shake);


        }
    }

    @Override
    public Habit updateHabit(Habit habit) {
        Habit result = habit.clone();
        result.setTitle(mBinding.etTitle.getText().toString());
        result.setDescription(mBinding.etContent.getText().toString());
        Alarm alarm = result.getAlarm();
        alarm.setAlarmInterval(mBinding.seekBar.getProgress() * 60);
        int indexWake = mBinding.cbAlertRing.isChecked() ? AppConstant.WakeStyle.RING : 0;
        indexWake += mBinding.cbAlertLight.isChecked() ? AppConstant.WakeStyle.LIGHT : 0;
        indexWake += mBinding.cbAlertVibrate.isChecked() ? AppConstant.WakeStyle.VIBRATE : 0;

        alarm.setWakeStyle(indexWake);
        switch (mBinding.rgAccept.getCheckedRadioButtonId()) {
            case R.id.rb_accept_shake:
                alarm.setAcceptStyle(AppConstant.AcceptStyle.SHAKE);
                break;
            case R.id.rb_accept_turnoff:
                alarm.setAcceptStyle(AppConstant.AcceptStyle.TURN);
                break;
            case R.id.rb_accept_cover:
                alarm.setAcceptStyle(AppConstant.AcceptStyle.COVER);
            default:
                alarm.setAcceptStyle(AppConstant.AcceptStyle.SHAKE);
        }

        switch (mBinding.rgDelay.getCheckedRadioButtonId()) {
            case R.id.rb_delay_shake:
                alarm.setDelayStyle(AppConstant.DelayStyle.SHAKE);
                break;
            case R.id.rb_delay_turnoff:
                alarm.setDelayStyle(AppConstant.DelayStyle.TURN);
                break;
            case R.id.rb_delay_cover:
                alarm.setDelayStyle(AppConstant.DelayStyle.COVER);
                break;
            default:
                alarm.setDelayStyle(AppConstant.DelayStyle.SHAKE);
                break;
        }
        result.setAlarm(alarm);
        return result;
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

    @Override
    public void onBackPressed() {
        Habit habit = updateHabit(mHabit);
        //TODO 该写saveHabit方法了...
        LogUtils.i(TAG, "元数据  " + mHabit.toString());
        LogUtils.i(TAG, "新数据  " + habit.toString());

        if (habit.equals(mHabit)) {
            Toast.makeText(getApplicationContext(), "习惯没有改动...", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setIcon(R.drawable.ic_save_black_24dp).setMessage("退出前是否需要保存?");

            builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "习惯已经被保存!", Toast.LENGTH_SHORT).show();
                    mPresenter.saveHabit();
                    HabitDetailActivity.this.finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    HabitDetailActivity.this.finish();
                }
            }).create().show();
        }
    }
}
