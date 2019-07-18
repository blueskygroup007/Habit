package com.bluesky.habit.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bluesky.habit.R;
import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.LogUtils;

import static com.bluesky.habit.service.ForegroundService.ACTION_ACCEPT;
import static com.bluesky.habit.service.ForegroundService.ACTION_STOP;
import static com.bluesky.habit.service.ForegroundService.EXTRA_HABIT;

public class AlertDialogActivity extends Activity {
    public static final String TAG = AlertDialogActivity.class.getSimpleName();
    private static final int RADIO_ACC = 17;//传感器幅度值常量
    private AccelerometerLisenter mLisenter = new AccelerometerLisenter();

    Button btnOk;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setGravity(Gravity.CENTER);
        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setVibrator(true);

    }

    public void startAccSensor() {
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        sensorManager.registerListener(mLisenter, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
                intent.setAction(ACTION_STOP);
                intent.putExtra(EXTRA_HABIT, habit);
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
        //移除所有消息,防止自动延时关闭本界面的消息再被执行.
        mHandler.removeCallbacksAndMessages(null);
    }

    class AccelerometerLisenter implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor1 = event.sensor;
            if (sensor1.getType() == Sensor.TYPE_ACCELEROMETER) {
                float valueX = Math.abs(event.values[0]);
                float valueY = Math.abs(event.values[1]);
                float valueZ = Math.abs(event.values[2]);
                if (valueX > RADIO_ACC || valueY > RADIO_ACC || valueZ > RADIO_ACC) {
                    LogUtils.e(TAG, "传感器数据:" + "X=" + valueX + " Y=" + valueY + " Z=" + valueZ);
                    //监测到摇一摇反馈
                    //1.通知Presenter,本次闹钟标记为已实现.关闭提示页面
                    //2.P操作M,标记数据库
                    //3.P操作V(假如V为活动状态),刷新列表item数据

                    //给前台服务发消息,告诉P,有反馈
                    Intent intent = new Intent(AlertDialogActivity.this, ForegroundService.class);
                    intent.setAction(ACTION_ACCEPT);
                    startService(intent);
                    finish();
                    //取消监听,直接finish(),destroy中有unregister.
                    //sensorManager.unregisterListener(mLisenter, mSensor);

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
}
