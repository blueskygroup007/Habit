package com.bluesky.habit.data;

import androidx.room.Entity;
import androidx.room.TypeConverter;

/**
 * @author BlueSky
 * @date 2019/6/20
 * Description:
 */

public class Types {

    public enum RepeatDay {
        MON(1), TUE(2), WED(4), THU(8), FRI(16), SAT(32), SUN(64), NONE(128);
        int mVal;

        RepeatDay(int val) {
            mVal = val;
        }

    }

    public enum WakeStyle {
        RING(1), LIGHT(2), VIBRATE(4);
        int mVal;

        WakeStyle(int val) {
            mVal = val;
        }
    }

    public enum FeedbackStyle {
        SHAKE(1), TURN(2), COVER(4);
        int mVal;

        FeedbackStyle(int val) {
            mVal = val;
        }
    }

    public enum VibrateType {
        LIGHT(1), MEDIUM(2), HEAVY(4);
        int mVal;

        VibrateType(int val) {
            mVal = val;
        }
    }
}
