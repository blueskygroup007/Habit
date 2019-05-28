package com.bluesky.habit.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description:
 */
public class ActivityUtils {

    /**
     * 添加fragment到容器中
     * @param fragmentManager
     * @param fragment
     * @param frameId 容器id
     */
    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}
