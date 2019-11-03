package com.bluesky.habit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluesky.habit.R;
import com.bluesky.habit.util.PreferenceUtils;
import com.google.android.material.snackbar.Snackbar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class PasswordModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtOldPwd;
    private EditText mEtNewPwd;
    private EditText mEtAgainPwd;
    private Button mBtnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_modify);
        initView();
        initEven();
    }

    private void initView() {
        mEtOldPwd = findViewById(R.id.et_old_pwd);
        mEtNewPwd = findViewById(R.id.et_new_pwd);
        mEtAgainPwd = findViewById(R.id.et_again_pwd);
        mBtnModify = findViewById(R.id.btn_modify_pwd);
    }

    private void initEven() {
        mBtnModify.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify_pwd:
                modifyPwd(v);
                break;
            default:
                break;
        }
    }

    private void modifyPwd(View view) {
        String pwd_old = mEtOldPwd.getText().toString().trim();
        String pwd_new = mEtNewPwd.getText().toString().trim();
        String pwd_again = mEtAgainPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd_new) || TextUtils.isEmpty(pwd_new) || TextUtils.isEmpty(pwd_again)) {
            Toast.makeText(this, "输入不能为空!", Toast.LENGTH_SHORT).show();
        } else {
            if (!pwd_new.equals(pwd_again)) {
                Toast.makeText(this, "两次输入新密码必须相同!", Toast.LENGTH_SHORT).show();
            } else {
                if (pwd_old.equals(pwd_new)) {
                    Toast.makeText(this, "新旧密码不能相同!", Toast.LENGTH_SHORT).show();
                } else {
                    BmobUser.updateCurrentUserPassword(pwd_old, pwd_new, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                PreferenceUtils.put("username", "");
                                PreferenceUtils.put("password", "");
                                Snackbar.make(view.getRootView(), "修改密码成功!下次请用新密码登录.", LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PasswordModifyActivity.this, "修改密码失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }
}
