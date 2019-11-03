package com.bluesky.habit.constant;

import com.bluesky.habit.R;

/**
 * @author BlueSky
 * @date 2019/4/25
 * Description:
 */
public class AppConstant {
    //sharedpreference
    public static final String FIRST_RUN_SPLASH = "first_run_splash";
    public static final String CONFIG_FIRST_LOAD_ON_NETWORK = "config_first_load_on_network";

    //Bugly app id

    public static final String BUGLY_APP_ID="dd9286e7a4";

    //Bmob app id
    public static final String BMOB_APP_ID="28e33d09c51e50e1ce24cf0ee6d272b2";

    public static final int DEFAULT_ICON = 0;
    public static final int[] HABIT_ICONS = {R.drawable.ic_access_alarm_black_24dp,
            R.drawable.ic_cloud_black_24dp,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_insert_chart_black_24dp,
            R.drawable.ic_menu_camera,
            R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_send,
            R.drawable.ic_menu_share,
            R.drawable.ic_menu_slideshow,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_person_black_24dp,
            R.drawable.ic_pool_black_24dp
    };


    public interface WakeStyle {
        int RING = 1;
        int LIGHT = 2;
        int VIBRATE = 4;

    }

    public interface AcceptStyle {
        int SHAKE = 1;
        int TURN = 2;
        int COVER = 4;
    }

    public interface DelayStyle {
        int SHAKE = 1;
        int TURN = 2;
        int COVER = 4;
    }

    public interface VibrateStyle {
        int LIGHT = 1;
        int MEDIUM = 2;
        int HEAVY = 4;
    }
}
