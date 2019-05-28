package com.bluesky.habit.data.source.local;

import android.content.Context;

import com.bluesky.habit.data.Habit;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author BlueSky
 * @date 2019/4/23
 * Description:
 */
@Database(entities = {Habit.class}, version = 1, exportSchema = false)
public abstract class RHDatabase extends RoomDatabase {
    //todo exportSchema = false 否则报错
    private static RHDatabase INSTANCE;

    public abstract HabitDao habitDao();

    private static final Object sLock = new Object();

    public static RHDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RHDatabase.class, "Habits.db").build();
            }
        }
        return INSTANCE;
    }

}
