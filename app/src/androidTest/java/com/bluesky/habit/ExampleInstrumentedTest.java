package com.bluesky.habit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluesky.habit.constant.AppConstant;
import com.bluesky.habit.data.Alarm;
import com.bluesky.habit.data.Habit;
import com.bluesky.habit.data.source.local.HabitDao;
import com.bluesky.habit.data.source.local.RHDatabase;
import com.bluesky.habit.habit_detail.HabitDetailActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static com.bluesky.habit.habit_detail.HabitDetailActivity.EXTRA_HABIT_ID;
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
        Alarm alarm = new Alarm();

        Alarm alarm2 = alarm;
        Alarm alarm3 = alarm;
        Alarm alarm4 = alarm;
        Alarm alarm5 = alarm;

        Habit habit1 = new Habit(0, "habit1", "第一个好习惯,默认图标...", false, alarm2);
        Habit habit2 = new Habit(1, "habit2", "第二个好习惯,半小时喝一次水...", false, alarm3);
        Habit habit3 = new Habit(2, "habit3", "第三个好习惯,眼保健操...", false, alarm4);
        Habit habit4 = new Habit(3, "habit4", "最重要的好习惯,学习,学习,学习...", false, alarm5);
        dao.insertHabit(habit1);
        dao.insertHabit(habit2);
        dao.insertHabit(habit3);
        dao.insertHabit(habit4);
        List<Habit> habits = dao.getHabits();
        Log.e(TAG, habit1.getAlarm().toString());
        Log.e(TAG, habit2.getAlarm().toString());
        habit1.getAlarm().setNumberCount(555);
        Log.e(TAG, "-------------------");
        Log.e(TAG, habit1.getAlarm().toString());
        Log.e(TAG, habit2.getAlarm().toString());
        Alarm temp = (Alarm) habit1.getAlarm().clone();
        habit2.setAlarm(temp);
        habit1.getAlarm().setNumberCount(666);
        Log.e(TAG, "-------------------");
        Log.e(TAG, habit1.getAlarm().toString());
        Log.e(TAG, habit2.getAlarm().toString());
    }

    @Test
    public void detailActivityTest() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        HabitDao dao = RHDatabase.getInstance(appContext).habitDao();
        List<Habit> habits = dao.getHabits();
        System.out.println(habits.toString());
        Log.e("TEST", habits.get(0).toString());
        Intent intent = new Intent(appContext, HabitDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_HABIT_ID, habits.get(0).getId());
        appContext.startActivity(intent);
    }
}
