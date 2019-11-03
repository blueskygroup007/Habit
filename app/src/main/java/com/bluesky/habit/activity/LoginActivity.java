package com.bluesky.habit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluesky.habit.R;
import com.bluesky.habit.entity.HabitUser;
import com.bluesky.habit.util.LogUtils;
import com.bluesky.habit.util.PreferenceUtils;
import com.bluesky.habit.view.CustomDialog;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private TextView mEtForget;
    private Button mBtnLogin;
    private Button mBtnRegister;

    private CheckBox mCheckBoxRemember;

    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEven();
    }

    private void initEven() {
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mEtForget.setOnClickListener(this);
    }

    private void initView() {
        mEtUsername = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mCheckBoxRemember = findViewById(R.id.cb_remember);
        mEtForget = findViewById(R.id.tv_forget);
        String sp_username = PreferenceUtils.getString("username", "");
        String sp_password = PreferenceUtils.getString("password", "");
        if (!TextUtils.isEmpty(sp_username) & !TextUtils.isEmpty(sp_password)) {
            mEtUsername.setText(sp_username);
            mEtPassword.setText(sp_password);
            mCheckBoxRemember.setChecked(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget:
                Intent intent_pw = new Intent(this, PasswordForgetActivity.class);
                startActivity(intent_pw);
                break;
            default:

        }
    }

    private void login() {
        /*BasePopupView popupView = new XPopup.Builder(this)
                .asLoading("正在加载中")
                .show();*/

        CustomDialog dialog = new CustomDialog(this, 100, 100,
                R.layout.dialog_loading,
                R.style.dialog_custom,
                Gravity.CENTER);
        dialog.setCancelable(false);
        String username = mEtUsername.getText().toString().trim();
        String pwd = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username) | TextUtils.isEmpty(pwd)) {
            return;
        }
        dialog.show();

        HabitUser user = new HabitUser();
        user.setUsername(username);
        user.setPassword(pwd);
        user.login(new SaveListener<HabitUser>() {
            @Override
            public void done(HabitUser habitUser, BmobException e) {

                dialog.dismiss();
//                popupView.delayDismiss(300);
                if (e == null) {
                    //登陆成功后,再做记住密码操作
                    if (mCheckBoxRemember.isChecked()) {
                        PreferenceUtils.put("username", username);
                        PreferenceUtils.put("password", pwd);
                    }
                    Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                    //因为后台没有开启邮箱验证,所以,如果启用,会报错9015
                    /*if (user.getEmailVerified()) {
                        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "请前往验证邮箱!", Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    PreferenceUtils.put("username", "");
                    PreferenceUtils.put("password", "");
                    Toast.makeText(LoginActivity.this, "登陆失败!" + "  错误代码是:" + e.toString(), Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG, "USER=" + user.getUsername() + "\n" + " ERROR=" + e.getMessage());
                }
            }
        });
    }
}
