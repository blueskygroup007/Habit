package com.bluesky.habit.habit_list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * @author BlueSky
 * @date 2019/3/6
 * Description:
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

    /**
     * 实际需要滑动的child view
     */
    private View mScrollUpChild;

    public ScrollChildSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写,滑动传进来的child view
     *
     * @return
     */
    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    /**
     * 将需要滑动的child view 赋值
     *
     * @param view
     */
    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }
}
