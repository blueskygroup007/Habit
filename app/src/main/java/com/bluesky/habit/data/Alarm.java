package com.bluesky.habit.data;

import androidx.room.Entity;

/**
 * @author BlueSky
 * @date 2019/4/20
 * Description:
 */
@Entity
public class Alarm implements Cloneable {
    private int count_number;
    private int current_number;
    private int interval_time;
    private int current_time;
    private int total_time;
    private int style;
    private int feedback;

    public Alarm(int count_number, int current_number, int interval_time, int current_time, int total_time, int style, int feedback) {
        this.count_number = count_number;
        this.current_number = current_number;
        this.interval_time = interval_time;
        this.current_time = current_time;
        this.total_time = total_time;
        this.style = style;
        this.feedback = feedback;
    }

    public Alarm(Alarm alarm) {
        this.count_number = alarm.getCount_number();
        this.current_number = alarm.getCurrent_number();
        this.interval_time = alarm.getInterval_time();
        this.current_time = alarm.getCurrent_time();
        this.total_time = alarm.getTotal_time();
        this.style = alarm.getStyle();
        this.feedback = alarm.getFeedback();
    }

    public int getCount_number() {
        return count_number;
    }

    public void setCount_number(int count_number) {
        this.count_number = count_number;
    }

    public int getCurrent_number() {
        return current_number;
    }

    public void setCurrent_number(int current_number) {
        this.current_number = current_number;
    }

    public int getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(int interval_time) {
        this.interval_time = interval_time;
    }

    public int getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(int current_time) {
        this.current_time = current_time;
    }

    public int getTotal_time() {
        return total_time;
    }

    public void setTotal_time(int total_time) {
        this.total_time = total_time;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "count_number=" + count_number +
                ", current_number=" + current_number +
                ", interval_time=" + interval_time +
                ", current_time=" + current_time +
                ", total_time=" + total_time +
                ", style=" + style +
                ", feedback=" + feedback +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
