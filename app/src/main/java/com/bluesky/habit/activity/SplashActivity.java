package com.bluesky.habit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bluesky.habit.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private int[] pages = {R.mipmap.page1, R.mipmap.page2, R.mipmap.page2, R.mipmap.page2};
    private Button mBtnGotoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBtnGotoMain = findViewById(R.id.btn_goto_main);
        mBtnGotoMain.setOnClickListener(this);
        ViewPager viewPager = findViewById(R.id.vp_welcome);
        viewPager.setAdapter(new WelcomeAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == pages.length - 1) {
                    mBtnGotoMain.setVisibility(View.VISIBLE);
                } else {
                    mBtnGotoMain.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    class WelcomeAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pages.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView iv = new ImageView(SplashActivity.this);
            iv.setImageResource(pages[position]);
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
