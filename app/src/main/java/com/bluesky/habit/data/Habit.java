package com.bluesky.habit.data;

import com.bluesky.habit.constant.AppConstant;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author BlueSky
 * @date 2019/4/20
 * Description:
 */
@Entity
public class Habit {
    @Ignore
    private static final int DEFAULT_ICON = AppConstant.DEFAULT_ICON;
    //todo 主键必须有NonNull注解
    //todo 用final修饰成员变量,表示只允许在构造时给变量赋值,所以没有setXXX()方法
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mId;
    @ColumnInfo(name = "icon")
    private int mIcon;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "description")
    private String mDescription;
    @ColumnInfo(name = "completed")
    private boolean mCompleted;
    @Embedded
    private Alarm mAlarm;

    public Habit(@NonNull String id, int icon, String title, String description, boolean completed, Alarm alarm) {
        mId = id;
        mIcon = icon;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
        mAlarm = alarm;
    }

    @Ignore
    public Habit(Habit habit) {
        mId = habit.getId();
        mIcon = habit.getIcon();
        mTitle = habit.getTitle();
        mDescription = habit.getDescription();
        mCompleted = habit.isCompleted();
        mAlarm = habit.getAlarm();
    }

    @Ignore
    public Habit(int icon, String title, String description) {
        mId = UUID.randomUUID().toString();
        mIcon = icon;
        mTitle = title;
        mDescription = description;
        mCompleted = false;
        mAlarm = null;
    }

    @Ignore
    public Habit(String title, String description) {
        mId = UUID.randomUUID().toString();
        mIcon = DEFAULT_ICON;
        mTitle = title;
        mDescription = description;
        mCompleted = false;
        mAlarm = null;
    }

    @Ignore
    public Habit(int icon, String title, String description, boolean completed, Alarm alarm) {
        this(UUID.randomUUID().toString(), icon, title, description, completed, alarm);
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public void setId(@NonNull String id) {
        mId = id;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }

    public Alarm getAlarm() {
        return mAlarm;
    }

    public void setAlarm(Alarm alarm) {
        mAlarm = alarm;
    }



    @Override
    public String toString() {
        return "Habit{" +
                "mId='" + mId + '\'' +
                ", mIcon=" + mIcon +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mCompleted=" + mCompleted +
                ", mAlarm=" + mAlarm +
                '}' + '\n';
    }
}
