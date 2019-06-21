package com.bluesky.habit.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;

/**
 * @author BlueSky
 * @date 2019/4/20
 * Description:
 */
@Entity
public class Alarm implements Parcelable {
    /**
     * 预设总提示次数(统计用)
     */

    private int numberCount = 100;
    /**
     * 当前提示计数
     */

    private int numberCurrent = 0;
    /**
     * 闹钟间隔
     */

    private int alarmInterval = 10 * 60 * 1000;
    /**
     * 当前闹钟进行时间
     */

    private int alarmCurrent = 0;
    /**
     * 闹钟总时长(统计用)
     */

    private int alarmTotal = 0;
    /**
     * 闹钟报警方式(声,光,震)
     */
    private int wakeStyle = 1;
//    private EnumSet<Types.WakeStyle> wakeStyle = EnumSet.of(Types.WakeStyle.RING);
    /**
     * 反馈方式(摇一摇,翻转[重力+距离],遮挡光线)
     */
    private int feedbackStyle = 1;
//    private EnumSet<Types.FeedbackStyle> feedbackStyle = EnumSet.of(Types.FeedbackStyle.SHAKE);

    /**
     * todo 由Url转换而来
     * 铃声uri
     */
    private String ringTone = null;

    /**
     * 渐强音量
     */
    private boolean increasingVolume = true;

    /**
     * 闹钟状态(active,inactive)
     */
    private boolean isActive = false;

    /**
     * 闹钟重复(星期1-7)
     */
    private int repeatDay = 128;
//    private EnumSet<Types.RepeatDay> repeatDay = EnumSet.of(Types.RepeatDay.NONE);
    /**
     * 震动方式
     */
    private int vibrateStyle = 0;
//    private Types.VibrateType vibrateStyle = Types.VibrateType.MEDIUM;

    public Alarm() {
    }

    protected Alarm(Parcel in) {
        numberCount = in.readInt();
        numberCurrent = in.readInt();
        alarmInterval = in.readInt();
        alarmCurrent = in.readInt();
        alarmTotal = in.readInt();
        wakeStyle = in.readInt();
        feedbackStyle = in.readInt();
        ringTone = in.readString();
        increasingVolume = in.readByte() != 0;
        isActive = in.readByte() != 0;
        repeatDay = in.readInt();
        vibrateStyle = in.readInt();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public String toString() {
        return "Alarm{" +
                "numberCount=" + numberCount +
                ", numberCurrent=" + numberCurrent +
                ", alarmInterval=" + alarmInterval +
                ", alarmCurrent=" + alarmCurrent +
                ", alarmTotal=" + alarmTotal +
                ", wakeStyle=" + wakeStyle +
                ", feedbackStyle=" + feedbackStyle +
                ", ringTone='" + ringTone + '\'' +
                ", increasingVolume=" + increasingVolume +
                ", isActive=" + isActive +
                ", repeatDay=" + repeatDay +
                ", vibrateStyle=" + vibrateStyle +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return numberCount == alarm.numberCount &&
                numberCurrent == alarm.numberCurrent &&
                alarmInterval == alarm.alarmInterval &&
                alarmCurrent == alarm.alarmCurrent &&
                alarmTotal == alarm.alarmTotal &&
                wakeStyle == alarm.wakeStyle &&
                feedbackStyle == alarm.feedbackStyle &&
                increasingVolume == alarm.increasingVolume &&
                isActive == alarm.isActive &&
                repeatDay == alarm.repeatDay &&
                vibrateStyle == alarm.vibrateStyle &&
                Objects.equals(ringTone, alarm.ringTone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberCount, numberCurrent, alarmInterval, alarmCurrent, alarmTotal, wakeStyle, feedbackStyle, ringTone, increasingVolume, isActive, repeatDay, vibrateStyle);
    }

    public int getNumberCount() {
        return numberCount;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
    }

    public int getNumberCurrent() {
        return numberCurrent;
    }

    public void setNumberCurrent(int numberCurrent) {
        this.numberCurrent = numberCurrent;
    }

    public int getAlarmInterval() {
        return alarmInterval;
    }

    public void setAlarmInterval(int alarmInterval) {
        this.alarmInterval = alarmInterval;
    }

    public int getAlarmCurrent() {
        return alarmCurrent;
    }

    public void setAlarmCurrent(int alarmCurrent) {
        this.alarmCurrent = alarmCurrent;
    }

    public int getAlarmTotal() {
        return alarmTotal;
    }

    public void setAlarmTotal(int alarmTotal) {
        this.alarmTotal = alarmTotal;
    }

    public int getWakeStyle() {
        return wakeStyle;
    }

    public void setWakeStyle(int wakeStyle) {
        this.wakeStyle = wakeStyle;
    }

    public int getFeedbackStyle() {
        return feedbackStyle;
    }

    public void setFeedbackStyle(int feedbackStyle) {
        this.feedbackStyle = feedbackStyle;
    }

    public String getRingTone() {
        return ringTone;
    }

    public void setRingTone(String ringTone) {
        this.ringTone = ringTone;
    }

    public boolean isIncreasingVolume() {
        return increasingVolume;
    }

    public void setIncreasingVolume(boolean increasingVolume) {
        this.increasingVolume = increasingVolume;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRepeatDay() {
        return repeatDay;
    }

    public void setRepeatDay(int repeatDay) {
        this.repeatDay = repeatDay;
    }

    public int getVibrateStyle() {
        return vibrateStyle;
    }

    public void setVibrateStyle(int vibrateStyle) {
        this.vibrateStyle = vibrateStyle;
    }

    @Ignore
    public Alarm(int numberCount, int numberCurrent, int alarmInterval, int alarmCurrent, int alarmTotal, int wakeStyle, int feedbackStyle, String ringTone, boolean increasingVolume, boolean isActive, int repeatDay, int vibrateStyle) {
        this.numberCount = numberCount;
        this.numberCurrent = numberCurrent;
        this.alarmInterval = alarmInterval;
        this.alarmCurrent = alarmCurrent;
        this.alarmTotal = alarmTotal;
        this.wakeStyle = wakeStyle;
        this.feedbackStyle = feedbackStyle;
        this.ringTone = ringTone;
        this.increasingVolume = increasingVolume;
        this.isActive = isActive;
        this.repeatDay = repeatDay;
        this.vibrateStyle = vibrateStyle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numberCount);
        dest.writeInt(numberCurrent);
        dest.writeInt(alarmInterval);
        dest.writeInt(alarmCurrent);
        dest.writeInt(alarmTotal);
        dest.writeInt(wakeStyle);
        dest.writeInt(feedbackStyle);
        dest.writeString(ringTone);
        dest.writeByte((byte) (increasingVolume ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeInt(repeatDay);
        dest.writeInt(vibrateStyle);
    }
}
