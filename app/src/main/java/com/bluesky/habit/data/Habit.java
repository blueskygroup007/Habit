package com.bluesky.habit.data;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Habit implements Parcelable {
    @Ignore
    private static final int DEFAULT_ICON = AppConstant.DEFAULT_ICON;
    //todo 主键必须有NonNull注解
    //todo 用final修饰成员变量,表示只允许在构造时给变量赋值,所以没有setXXX()方法
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private  String mId;
    @ColumnInfo(name = "icon")
    private  int mIcon;
    @ColumnInfo(name = "title")
    private  String mTitle;
    @ColumnInfo(name = "description")
    private  String mDescription;
    @ColumnInfo(name = "active")
    private  boolean mActive;
    @Embedded
    private  Alarm mAlarm;

    /**
     * todo 只留一个构造方法给Room,其余全部Ignore
     *
     * @param id
     * @param icon
     * @param title
     * @param description
     * @param active
     * @param alarm
     */
    public Habit(@NonNull String id, int icon, String title, String description, boolean active, Alarm alarm) {
        mId = id;
        mIcon = icon;
        mTitle = title;
        mDescription = description;
        mActive = active;
        mAlarm = alarm;
    }

    @Ignore
    public Habit(Habit habit) {
        mId = habit.getId();
        mIcon = habit.getIcon();
        mTitle = habit.getTitle();
        mDescription = habit.getDescription();
        mActive = habit.isActive();
        mAlarm = habit.getAlarm();
    }

    @Ignore
    public Habit(int icon, String title, String description) {
        this(UUID.randomUUID().toString(), icon, title, description, false, new Alarm());
    }

    @Ignore
    public Habit(String title, String description) {
        this(UUID.randomUUID().toString(), DEFAULT_ICON, title, description, false, new Alarm());
    }



    @Ignore
    public Habit(int icon, String title, String description, boolean active, Alarm alarm) {
        this(UUID.randomUUID().toString(), icon, title, description, active, alarm);
    }

    protected Habit(Parcel in) {
        mId = in.readString();
        mIcon = in.readInt();
        mTitle = in.readString();
        mDescription = in.readString();
        mActive = in.readByte() != 0;
    }

    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

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

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
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
                ", mCompleted=" + mActive +
                ", mAlarm=" + mAlarm +
                '}' + '\n';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(mIcon);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeByte((byte) (mActive ? 1 : 0));
    }
}
