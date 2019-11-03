package com.bluesky.habit.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @author BlueSky
 * @date 2019/4/20
 * Description:
 */
@Entity
public class Alarm implements Parcelable, Cloneable {
    /**
     * 预设总提示次数(统计用)
     */

    private int numberCount = 100;
    /**
     * 当前提示计数
     */

    private int numberCurrent = 0;
    /**
     * 闹钟间隔(秒)
     */

    private int alarmInterval = 300;
    /**
     * 当前闹钟进行时间(秒)//todo 已经被停用
     */

    private int alarmCurrent = 0;
    /**
     * 闹钟总时长(统计用)(秒)
     */

    private int alarmTotal = 0;
    /**
     * 闹钟报警方式(声,光,震)
     */
    private int wakeStyle = 1;
//    private EnumSet<Types.WakeStyle> wakeStyle = EnumSet.of(Types.WakeStyle.RING);
    /**
     * accept反馈方式(摇一摇,翻转[重力+距离],遮挡光线)
     */
    private int acceptStyle = 1;
//    private EnumSet<Types.FeedbackStyle> feedbackStyle = EnumSet.of(Types.FeedbackStyle.SHAKE);

    /**
     * delay反馈方式(摇一摇,翻转[重力+距离],遮挡光线)
     */
    private int delayStyle = 1;

    /**
     * todo 由Url转换而来
     * 铃声uri
     */
    private String ringTone = "";

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

    @Override
    public Alarm clone() {
        Alarm alarm;
        try {
            alarm = (Alarm) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        return alarm;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Alarm)) {
            return false;
        }

        Alarm other = (Alarm) obj;
        if (!(numberCount == other.getNumberCount())) {
            return false;
        }
        if (!(numberCurrent == other.getNumberCurrent())) {
            return false;
        }
        if (!(alarmInterval == other.getAlarmInterval())) {
            return false;
        }
        if (!(alarmCurrent == other.getAlarmCurrent())) {
            return false;
        }
        if (!(alarmTotal == other.getAlarmTotal())) {
            return false;
        }
        if (!(wakeStyle == other.getWakeStyle())) {
            return false;
        }
        if (!(acceptStyle == other.getAcceptStyle())) {
            return false;
        }
        if (!(delayStyle == other.getDelayStyle())) {
            return false;
        }
        if (!(ringTone.equals(other.getRingTone()))) {
            return false;
        }
        if (!(increasingVolume == other.isIncreasingVolume())) {
            return false;
        }
        if (!(isActive == other.isActive())) {
            return false;
        }
        if (!(repeatDay == other.getRepeatDay())) {
            return false;
        }
        if (!(vibrateStyle == other.getVibrateStyle())) {
            return false;
        }
        return true;
    }

    @Ignore
    public Alarm(int numberCount, int numberCurrent, int alarmInterval, int alarmCurrent, int alarmTotal, int wakeStyle, int acceptStyle, int delayStyle, String ringTone, boolean increasingVolume, boolean isActive, int repeatDay, int vibrateStyle) {
        this.numberCount = numberCount;
        this.numberCurrent = numberCurrent;
        this.alarmInterval = alarmInterval;
        this.alarmCurrent = alarmCurrent;
        this.alarmTotal = alarmTotal;
        this.wakeStyle = wakeStyle;
        this.acceptStyle = acceptStyle;
        this.delayStyle = delayStyle;
        this.ringTone = ringTone;
        this.increasingVolume = increasingVolume;
        this.isActive = isActive;
        this.repeatDay = repeatDay;
        this.vibrateStyle = vibrateStyle;
    }

    @Ignore
    protected Alarm(Parcel in) {
        numberCount = in.readInt();
        numberCurrent = in.readInt();
        alarmInterval = in.readInt();
        alarmCurrent = in.readInt();
        alarmTotal = in.readInt();
        wakeStyle = in.readInt();
        acceptStyle = in.readInt();
        delayStyle = in.readInt();
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

    public int getAcceptStyle() {
        return acceptStyle;
    }

    public void setAcceptStyle(int acceptStyle) {
        this.acceptStyle = acceptStyle;
    }

    public int getDelayStyle() {
        return delayStyle;
    }

    public void setDelayStyle(int delayStyle) {
        this.delayStyle = delayStyle;
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
        dest.writeInt(acceptStyle);
        dest.writeInt(delayStyle);
        dest.writeString(ringTone);
        dest.writeByte((byte) (increasingVolume ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeInt(repeatDay);
        dest.writeInt(vibrateStyle);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "numberCount=" + numberCount +
                ", numberCurrent=" + numberCurrent +
                ", alarmInterval=" + alarmInterval +
                ", alarmCurrent=" + alarmCurrent +
                ", alarmTotal=" + alarmTotal +
                ", wakeStyle=" + wakeStyle +
                ", acceptStyle=" + acceptStyle +
                ", delayStyle=" + delayStyle +
                ", ringTone='" + ringTone + '\'' +
                ", increasingVolume=" + increasingVolume +
                ", isActive=" + isActive +
                ", repeatDay=" + repeatDay +
                ", vibrateStyle=" + vibrateStyle +
                '}';
    }
}
