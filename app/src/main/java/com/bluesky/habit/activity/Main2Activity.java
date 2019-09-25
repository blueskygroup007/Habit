package com.bluesky.habit.activity;

import android.content.Intent;
import android.os.Bundle;

import com.bluesky.habit.habit_list.HabitFragment;
import com.bluesky.habit.habit_list.HabitListPresenter;
import com.bluesky.habit.util.Injection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.bluesky.habit.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initFragment();
    }

    private void initFragment() {
        //原意图是:当Activity被回收重建时,用id来找回Fragment.弃用
/*        HabitFragment habitFragment = (HabitFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (habitFragment == null) {
            habitFragment = HabitFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, habitFragment);
            transaction.commit();
        }*/

        HabitFragment habitFragment = HabitFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFrame, habitFragment);
        transaction.commit();

        HabitListPresenter mPresenter = new HabitListPresenter(this, Injection.provideTasksRepository(getApplicationContext()), habitFragment);

    }

    /**
     * 返回键执行HOME键的效果
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

}
