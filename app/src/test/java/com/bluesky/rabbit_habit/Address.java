package com.bluesky.rabbit_habit;

/**
 * @author BlueSky
 * @date 2019/4/25
 * Description:
 */
public class Address implements Cloneable {
    public int number;
    public String street;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
