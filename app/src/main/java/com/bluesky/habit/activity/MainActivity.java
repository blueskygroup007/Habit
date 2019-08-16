package com.bluesky.habit.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bluesky.habit.Listener.OnFragmentInteractionListener;
import com.bluesky.habit.R;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.discover.DiscoverFragment;
import com.bluesky.habit.habit_list.HabitFragment;
import com.bluesky.habit.habit_list.HabitListPresenter;
import com.bluesky.habit.habit_list.dummy.DummyContent;
import com.bluesky.habit.mine.MineFragment;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.statistics.StatisticsFragment;
import com.bluesky.habit.util.Injection;
import com.bluesky.habit.util.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, HabitFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TextView mTextMessage;
    private FrameLayout mContainer;
    private List<Fragment> mFragmentList = new ArrayList<>();
    public ForegroundService.ForeControlBinder mBinder;
    private HabitListPresenter mPresenter;

    @Override
    protected void onDestroy() {
        LogUtils.i(TAG, "Activity onDestroy()...");
        super.onDestroy();
        unbindService(mConn);
    }

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(TAG, "onServiceConnected()...");

            mBinder = (ForegroundService.ForeControlBinder) service;
            //todo activity恢复,从service获取即时状态
            initFragment();

//            mPresenter.setService();
        }

        //绑定失败
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e(TAG, "onServiceDisconnected()...");
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_habit_list:
                    mTextMessage.setText(R.string.title_habit_list);
                    switchFragment(mHabitFragment);
                    return true;
                case R.id.navigation_statistics:
                    mTextMessage.setText(R.string.title_statistics);
                    switchFragment(mStatisticsFragment);
                    return true;
                case R.id.navigation_discover:
                    mTextMessage.setText(R.string.title_discover);
                    switchFragment(mDiscoverFragment);

                    return true;
                case R.id.navigation_mine:
                    mTextMessage.setText(R.string.title_mine);
                    switchFragment(mMineFragment);
                    return true;
                default:
                    return false;
            }
        }
    };

    private void switchFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //要切换的fragment无需切换
        if (!fragment.isHidden()) {
            return;
        }
        for (Fragment toBeHide : mFragmentList) {
            if (!toBeHide.isHidden()) {
                transaction.hide(toBeHide);
            }
        }
        transaction.show(fragment).commit();
        /*Alarm alarm=new Alarm(1, 1, 1, 1, 1, 1, 1);
        Alarm alarm1=alarm.clone();*/
    }

    private HabitFragment mHabitFragment;
    private StatisticsFragment mStatisticsFragment;
    private DiscoverFragment mDiscoverFragment;
    private MineFragment mMineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.i(TAG, "Activity onCreate()...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mContainer = findViewById(R.id.fragment_container);
        mTextMessage = findViewById(R.id.tv_message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bindService(new Intent(this, ForegroundService.class), mConn, BIND_AUTO_CREATE);
    }

    /**
     * 返回键执行HOME键的效果
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    /**
     * 把bindService放在该方法内的原因是:当主界面长时间不可见时,恢复可见马上绑定Service,
     * 绑定成功后,马上就会取当前的各个Habit进度,主动更新列表状态
     */
    @Override
    protected void onStart() {
        LogUtils.i(TAG, "Activity onStart()...");

        super.onStart();
//        bindService(new Intent(this, ForegroundService.class), mConn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        LogUtils.i(TAG, "Activity onResume()...");

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtils.i(TAG, "Activity onPause()...");

        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtils.i(TAG, "Activity onStop()...");
        super.onStop();
        //界面不可见,取消绑定服务,取消监听器
//        mPresenter.onDestory();//放在了fragment自己的onStop中
//        unbindService(mConn);

    }

    private void initFragment() {
        //创建presenter
        //todo 这里的context使用了getApplicationContext
        mHabitFragment = HabitFragment.newInstance(mBinder);
        mPresenter = new HabitListPresenter(this, Injection.provideTasksRepository(getApplicationContext()), mBinder, mHabitFragment);
        mStatisticsFragment = StatisticsFragment.newInstance("param 1", "param 2");
        mDiscoverFragment = DiscoverFragment.newInstance("param 1", "param 2");
        mMineFragment = MineFragment.newInstance("param 1", "param 2");
        mFragmentList.add(mHabitFragment);
        mFragmentList.add(mStatisticsFragment);
        mFragmentList.add(mDiscoverFragment);
        mFragmentList.add(mMineFragment);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, mMineFragment, MineFragment.class.getSimpleName());
        transaction.add(R.id.fragment_container, mDiscoverFragment, DiscoverFragment.class.getSimpleName());
        transaction.add(R.id.fragment_container, mStatisticsFragment, StatisticsFragment.class.getSimpleName());
        transaction.hide(mMineFragment).hide(mDiscoverFragment).hide(mStatisticsFragment);
        transaction.add(R.id.fragment_container, mHabitFragment, HabitFragment.class.getSimpleName());
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
