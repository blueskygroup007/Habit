package com.bluesky.habit;

import android.content.Context;
import android.util.Log;

import com.bluesky.habit.constant.AppConstant;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.local.HabitDao;
import com.bluesky.habit.data.source.local.RHDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.bluesky.rabbit_habit", appContext.getPackageName());
    }

    @Test
    public void insertFakeDatabase() throws CloneNotSupportedException {
        Context appContext = InstrumentationRegistry.getTargetContext();

        HabitDao dao = RHDatabase.getInstance(appContext).habitDao();
        dao.deleteAllHabits();
        Alarm alarm = new Alarm(500, 50, 30, 10, 1000, 0, 1);

        Alarm alarm2 = (Alarm) alarm.clone();
        Alarm alarm3 = (Alarm) alarm.clone();
        Alarm alarm4 = (Alarm) alarm.clone();
        Alarm alarm5 = (Alarm) alarm.clone();

        Habit habit1 = new Habit(AppConstant.HABIT_ICONS[0], "habit1", "第一个好习惯,默认图标...", false, alarm2);
        Habit habit2 = new Habit(AppConstant.HABIT_ICONS[1], "habit2", "第二个好习惯,半小时喝一次水...", false, alarm3);
        Habit habit3 = new Habit(AppConstant.HABIT_ICONS[2], "habit3", "第三个好习惯,眼保健操...", false, alarm4);
        Habit habit4 = new Habit(AppConstant.HABIT_ICONS[3], "habit4", "最重要的好习惯,学习,学习,学习...", false, alarm5);
        dao.insertHabit(habit1);
        dao.insertHabit(habit2);
        dao.insertHabit(habit3);
        dao.insertHabit(habit4);
        List<Habit> habits = dao.getHabits();
        Log.d(TAG, habits.toString());
    }
}
