package com.bluesky.habit.base;

/**
 * @author BlueSky
 * @date 2019/4/27
 * Description:
 */
public interface BaseView<T extends BasePresenter> {
    /**
     * 给V指定P
     *
     * @param presenter
     */
    void setPresenter(T presenter);
}
