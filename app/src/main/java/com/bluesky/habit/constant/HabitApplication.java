package com.bluesky.habit.constant;

import android.app.Application;
import android.content.Context;

import com.bluesky.habit.util.PreferenceUtils;

import static com.bluesky.habit.constant.AppConstant.*;


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

        getConfig();
    }

    private void getConfig() {
        FIRST_LOAD_ON_NETWORK = PreferenceUtils.getBoolean(CONFIG_FIRST_LOAD_ON_NETWORK, true);
    }

    public static Context getContext() {
        return mContext;
    }
}
