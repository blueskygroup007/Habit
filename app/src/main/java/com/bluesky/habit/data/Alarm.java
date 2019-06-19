package com.bluesky.habit.data;

import android.net.Uri;

import androidx.room.Entity;

import java.io.Serializable;

/**
 * @author BlueSky
 * @date 2019/4/20
 * Description:
 */
@Entity
public class Alarm implements Serializable {
    /**
     * 预设总提示次数(统计用)
     */

    private int numberCount;
    /**
     * 当前提示计数
     */

    private int numberCurrent;
    /**
     * 闹钟间隔
     */

    private int alarmInterval;
    /**
     * 当前闹钟进行时间
     */

    private int alarmCurrent;
    /**
     * 闹钟总时长(统计用)
     */

    private int alarmTotal;
    /**
     * 闹钟报警方式(声,光,震)
     */
    private int wakeStyle;
    /**
     * 反馈方式(摇一摇,翻转[重力+距离],遮挡光线)
     */
    private int feedbackStyle;

    /**
     * 铃声uri
     */
    private Uri ringTone;

    /**
     * 渐强音量
     */
    private boolean increasingVolume;

    /**
     * 闹钟状态(active,inactive)
     */
    private boolean isActive = true;

    /**
     * 闹钟重复(星期1-7)
     */
    private int repeatDay;
    /**
     * 震动方式
     */
    private int vibrateStyle;


    public enum AlertStyle{}
}
