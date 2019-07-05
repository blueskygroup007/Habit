package com.bluesky.habit.constant;

import com.bluesky.habit.R;

/**
 * @author BlueSky
 * @date 2019/4/25
 * Description:
 */
public class AppConstant {
    //一些调试开关放在了这里
    public static final boolean FIRST_LOAD_ON_NETWORK = false;

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
