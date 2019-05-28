package com.bluesky.habit.data.source.local;

import com.bluesky.habit.data.Habit;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author BlueSky
 * @date 2019/4/23
 * Description:
 */
@Dao
public interface HabitDao {

    @Query("SELECT * FROM Habit")
    List<Habit> getHabits();

    @Query("SELECT * FROM Habit WHERE id=:habitId")
    Habit getHabitById(String habitId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHabit(Habit habit);

    @Update
    int updateHabit(Habit... habits);

    /**
     * 更新一个habit的completed状态
     *
     * @param habitId
     * @param completed
     */
    @Query("UPDATE Habit SET completed=:completed WHERE id=:habitId")
    void updateCompleted(String habitId, boolean completed);

    @Delete
    void deleteHabits(Habit habit);

    /**
     * 删除表中所有记录
     */
    @Query("DELETE FROM Habit")
    void deleteAllHabits();

    @Query("DELETE FROM Habit WHERE id=:habitId")
    int deleteHabitById(String habitId);
}
