package com.bluesky.habit.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bluesky.habit.R;
import com.bluesky.habit.entity.HabitUser;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author BlueSky
 * @date 2019/9/30
 * Description:
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEtUsername;
    EditText mEtNickname;
    EditText mEtAge;
    EditText mEtDesc;
    RadioGroup mRgGender;
    EditText mEtPwd;
    EditText mEtPwd2;
    EditText mEtEmail;

    Button mBtnRegister;
    Button mBtnTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtUsername = findViewById(R.id.et_reg_username);
        mEtNickname = findViewById(R.id.et_reg_nickname);
        mEtAge = findViewById(R.id.et_reg_age);
        mEtDesc = findViewById(R.id.et_reg_desc);
        mRgGender = findViewById(R.id.rg_reg_gender);
        mEtPwd = findViewById(R.id.et_reg_password);
        mEtPwd2 = findViewById(R.id.et_reg_again_password);
        mEtEmail = findViewById(R.id.et_reg_email);

        mBtnRegister = findViewById(R.id.btn_reg_register);
        mBtnTest = findViewById(R.id.btn_reg_test);

        mBtnRegister.setOnClickListener(this);
        mBtnTest.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reg_test:
                test();
                break;
            case R.id.btn_reg_register:
                register();
                break;
        }


    }

    private void register() {
        String username = mEtUsername.getText().toString().trim();
        String nickname = mEtNickname.getText().toString().trim();

        String strAge = mEtAge.getText().toString().trim();

        //已经将Edittext的inputStyle设置为number

        String desc = mEtDesc.getText().toString().trim();
        int rbId = mRgGender.getCheckedRadioButtonId();
        Boolean gender = (rbId == R.id.rb_reg_gender_male ? true : false);
        String pwd = mEtPwd.getText().toString().trim();
        String pwd2 = mEtPwd2.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(username)
                | TextUtils.isEmpty(nickname)
                | TextUtils.isEmpty(strAge)
                | TextUtils.isEmpty(desc)
                | TextUtils.isEmpty(pwd)
                | TextUtils.isEmpty(pwd2)
                | TextUtils.isEmpty(email)) {
            Toast.makeText(this, "每一项不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.equals(pwd2)) {
            Toast.makeText(this, "两次输入密码必须一致!", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer age;
        age = 0;
        try {
            age = Integer.parseInt(strAge);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "年龄必须是数字!", Toast.LENGTH_SHORT).show();
        }
        HabitUser user = new HabitUser();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setAge(age);
        user.setDesc(desc);
        user.setGender(gender);
        user.setPassword(pwd);
        user.setEmail(email);

        user.signUp(new SaveListener<HabitUser>() {
            @Override
            public void done(HabitUser habitUser, BmobException e) {
                Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void test() {
        CrashReport.testJavaCrash();

    }
}
