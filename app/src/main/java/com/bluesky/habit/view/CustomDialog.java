package com.bluesky.habit.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.bluesky.habit.R;

/**
 * @author BlueSky
 * @date 2019/10/17
 * Description:
 */
public class CustomDialog extends Dialog {

    //第一个最简单的构造方法
    public CustomDialog(@NonNull Context context, int layout, int style) {
        this(context, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, layout, style, Gravity.CENTER, R.style.pop_anim_style);
    }

    //第二个全参数构造方法(相比第三个,只是默认了动画参数)
    public CustomDialog(Context context, int width, int height, int layout, int style, int gravity) {
        this(context, width, height, layout, style, gravity, R.style.pop_anim_style);
    }


    public CustomDialog(Context context, int width, int height, int layout, int style, int gravity, int anim) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        window.setAttributes(params);
        window.setWindowAnimations(anim);
    }


}
