package com.bluesky.testapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mBtnOne;
    Button mBtnTwo;
    Button mBtnThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        mBtnOne = findViewById(R.id.btn_one);
        mBtnTwo = findViewById(R.id.btn_two);
        mBtnThree = findViewById(R.id.btn_three);
    }

    private void initEvent() {
        mBtnOne.setOnClickListener(this);
        mBtnTwo.setOnClickListener(this);
        mBtnThree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                showTestOne();
                break;
            case R.id.btn_two:
                showTestTwo();
                break;
            case R.id.btn_three:
                showTestThree();
                break;
            default:

        }
    }

    private void showTestThree() {
        new XPopup.Builder(this)
                .asLoading("正在加载中")
                .show();
    }

    private void showTestTwo() {
        new XPopup.Builder(this).asInputConfirm("我是标题", "我是内容", new OnInputConfirmListener() {
            @Override
            public void onConfirm(String text) {
                Toast.makeText(MainActivity.this, "input text: " + text, Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    private void showTestOne() {


        new XPopup.Builder(this).asConfirm("我是标题", "我是内容", new OnConfirmListener() {
            @Override
            public void onConfirm() {
                Toast.makeText(MainActivity.this, "click confirm", Toast.LENGTH_SHORT).show();
            }
        }).show();


    }
}
