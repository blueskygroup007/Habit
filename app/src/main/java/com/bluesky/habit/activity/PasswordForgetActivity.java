package com.bluesky.habit.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bluesky.habit.R;
import com.google.android.material.snackbar.Snackbar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

/**
 * @author BlueSky
 * @date 2019/10/11
 * Description:
 */
public class PasswordForgetActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText mEtEmail;


    private Button mBtnSendEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forget);
        initView();
        initEven();
    }

    private void initView() {

        mEtEmail = findViewById(R.id.et_email);
        mBtnSendEmail = findViewById(R.id.btn_forget_pwd);

    }

    private void initEven() {
        mBtnSendEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_forget_pwd:
                sendEmail(v);
                break;

            default:

        }
    }



    private void sendEmail(View view) {
        String email = mEtEmail.getText().toString().trim();
        //email格式检查
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "邮箱不能为空!", Toast.LENGTH_SHORT).show();
        } else {
            if (!checkEmail(email)) {
                Toast.makeText(this, "邮箱格式不正确!", Toast.LENGTH_SHORT).show();
            } else {
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Snackbar.make(view.getRootView(), "邮件已经发送!", LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PasswordForgetActivity.this, "邮件发送失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private boolean checkEmail(String email) {
        // 验证邮箱的正则表达式
        String format = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
        //p{Alpha}:内容是必选的，和字母字符[\p{Lower}\p{Upper}]等价。如：200896@163.com不是合法的。
        //w{2,15}: 2~15个[a-zA-Z_0-9]字符；w{}内容是必选的。 如：dyh@152.com是合法的。
        //[a-z0-9]{3,}：至少三个[a-z0-9]字符,[]内的是必选的；如：dyh200896@16.com是不合法的。
        //[.]:'.'号时必选的； 如：dyh200896@163com是不合法的。
        //p{Lower}{2,}小写字母，两个以上。如：dyh200896@163.c是不合法的。
        if (email.matches(format)) {
            return true;
        } else {
            return false;
        }
    }
}
