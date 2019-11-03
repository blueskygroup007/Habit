package com.bluesky.habit.constant;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.Bmob;

import static com.bluesky.habit.constant.AppConstant.BMOB_APP_ID;
import static com.bluesky.habit.constant.AppConstant.BUGLY_APP_ID;


/**
 * @author BlueSky
 * @date 2019/9/25
 * Description:
 */
public class HabitApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //分包配置
        MultiDex.install(this);
        //初始化Bugly,最后一个参数是日志输出,发布时应设置为false
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APP_ID, true);
        //初始化Bmob
        Bmob.initialize(this, BMOB_APP_ID);
    }


    public static Context getContext() {
        return mContext;
    }
}
