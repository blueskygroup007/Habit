package com.bluesky.habit.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.bluesky.habit.R;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.LogUtils;

import static com.bluesky.habit.data.Habit.HABIT_ID;
import static com.bluesky.habit.data.Habit.HABIT_TITLE;
import static com.bluesky.habit.service.ForegroundService.ACTION_ACCEPT;
import static com.bluesky.habit.service.ForegroundService.ACTION_SKIP;

public class AlertDialogActivity extends Activity {
    public static final String TAG = AlertDialogActivity.class.getSimpleName();
    private static final int RADIO_ACC = 17;//传感器幅度值常量
    private AccelerometerLisenter mLisenter = new AccelerometerLisenter();

    Button btnOk;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private Handler mHandler;
    private String mId;
    private PowerManager.WakeLock mWakelock = null;
    private MediaPlayer mp = new MediaPlayer();
    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);
        //背景透明
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        //点击透明区域,窗口不消失的api
        setFinishOnTouchOutside(false);
        //居中
        getWindow().setGravity(Gravity.CENTER);
        //全屏,锁屏显示等等
        WindowManager.LayoutParams winParams = getWindow().getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlertDialogActivity.this, ForegroundService.class);
                intent.putExtra(HABIT_ID, mId);
                intent.setAction(ACTION_ACCEPT);
                startService(intent);
                finish();
            }
        });

        setVibrator(true);
        Intent intent = getIntent();
        //这里能取到正确的值
        mId = intent.getStringExtra(HABIT_ID);
        LogUtils.i(TAG, "onCreate------------>id=" + mId);

        String title = intent.getStringExtra(HABIT_TITLE);
        mTvContent = findViewById(R.id.tv_content);
        mTvContent.setText("当前习惯为 " + title);

        //显示一个通知
        showNotification();
    }

    private void showNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent alertIntent = new Intent(this, AlertDialogActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setContentTitle(getString(R.string.noti_alert_title))
                .setContentText(getString(R.string.noti_alert_content))
                .setContentIntent(pi);
        Notification noti = builder.build();
        manager.notify(1, noti);

    }

    private void closeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);
    }


/*    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mId = intent.getStringExtra(HABIT_ID);
        LogUtils.i(TAG, "onNewIntent------------>id=" + mId);
        setIntent(intent);
    }*/

    public void startAccSensor() {
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        sensorManager.registerListener(mLisenter, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        LogUtils.e(TAG, "加速度监听启动了");

    }

    @Override
    protected void onStart() {
        super.onStart();
        //启动加速度监听
        startAccSensor();

        //10秒后关闭本界面
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.e(TAG, "自动延时关闭AlertDialogActivity");
                Intent intent = new Intent(AlertDialogActivity.this, ForegroundService.class);
                intent.setAction(ACTION_SKIP);
                if (mId == null || mId.isEmpty()) {
                    try {
                        throw new Exception("没能获取id....");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("捕获异常(没能获取id....)");
                    }
                }
                intent.putExtra(HABIT_ID, mId);
                //todo 需要修复问题:
                //1.该页面需要传进来什么数据,需要传回去什么数据,确定下来.
                //2.传ACTION_STOP回去后.应该有几套方案:1,stop 2,accept 3,ignore等
                AlertDialogActivity.this.startService(intent);
                finish();
            }
        }, 10000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setVibrator(false);
        sensorManager.unregisterListener(mLisenter, mSensor);
        LogUtils.e(TAG, "加速度监听停止了");
        closeNotification();
        //移除所有消息,防止自动延时关闭本界面的消息再被执行.
        mHandler.removeCallbacksAndMessages(null);
    }

    class AccelerometerLisenter implements SensorEventListener {
        boolean ischecked = false;

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor1 = event.sensor;
            if (sensor1.getType() == Sensor.TYPE_ACCELEROMETER) {
                float valueX = Math.abs(event.values[0]);
                float valueY = Math.abs(event.values[1]);
                float valueZ = Math.abs(event.values[2]);
                if (valueX > RADIO_ACC || valueY > RADIO_ACC || valueZ > RADIO_ACC) {
                    if (!ischecked) {
                        //防止下一次摇一摇再进来
                        ischecked = true;
                        LogUtils.e(TAG, "传感器数据:" + "X=" + valueX + " Y=" + valueY + " Z=" + valueZ);
                        //监测到摇一摇反馈
                        //1.通知Presenter,本次闹钟标记为已实现.关闭提示页面
                        //2.P操作M,标记数据库
                        //3.P操作V(假如V为活动状态),刷新列表item数据

                        //给前台服务发消息,告诉P,有反馈
                        Intent intent = new Intent(AlertDialogActivity.this, ForegroundService.class);
                        intent.putExtra(HABIT_ID, mId);
                        intent.setAction(ACTION_ACCEPT);
                        startService(intent);
                        finish();
                        //取消监听,直接finish(),destroy中有unregister.
                        //sensorManager.unregisterListener(mLisenter, mSensor);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private void setVibrator(boolean on) {
        if (on) {
            LogUtils.e(TAG, "开始震动");

        } else {
            LogUtils.e(TAG, "停止震动");

        }

        Vibrator vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            return;
        }

        if (on) {
            vibrator.vibrate(500);
        } else {
            vibrator.cancel();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 唤醒屏幕
        acquireWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    /**
     * 唤醒屏幕
     */
    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    /**
     * 开始播放铃声
     */
    private void startMedia() {
        try {
            mp.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)); //铃声类型为默认闹钟铃声
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    private void createDialog() {
        new AlertDialog.Builder(this)

                .setIcon(R.drawable.clock)
                .setTitle("闹钟提醒时间到了!!")
                .setMessage("你使用闹钟时间到了!!!")
                .setPositiveButton("推迟10分钟", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        tenMRemind();
                        mp.stop();
                        vibrator.cancel();
                        finish();
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mp.stop();
                        vibrator.cancel();
                        finish();
                    }
                }).show();
    }*/

}
