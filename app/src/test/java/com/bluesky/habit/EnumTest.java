package com.bluesky.habit;

import org.junit.Test;

import java.util.EnumSet;

/**
 * @author BlueSky
 * @date 2019/6/19
 * Description:
 */
public class EnumTest {
    public enum Light {
        RED, GREEN, YELLOW
    }

    @Test
    public void enumTest1() {
        EnumSet<Light> currEnumSet = EnumSet.noneOf(Light.class);
        currEnumSet.add(Light.RED);
        currEnumSet.add(Light.RED);
        currEnumSet.add(Light.GREEN);
        currEnumSet.add(Light.GREEN);
        currEnumSet.add(Light.YELLOW);
        currEnumSet.add(Light.YELLOW);
        currEnumSet.add(Light.YELLOW);
        System.out.println(currEnumSet);

    }

}
