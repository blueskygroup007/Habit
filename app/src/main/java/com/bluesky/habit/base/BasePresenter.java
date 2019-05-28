package com.bluesky.habit.base;

/**
 * @author BlueSky
 * @date 2019/4/27
 * Description:
 */
public interface BasePresenter<T> {
    /**
     * P的启动函数
     */
    void start();

    /**
     * 设置V
     */
    void setView(T view);
}
