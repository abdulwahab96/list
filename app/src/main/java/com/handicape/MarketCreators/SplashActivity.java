package com.handicape.MarketCreators;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.handicape.MarketCreators.R;

public class SplashActivity extends AppCompatActivity {
    RelativeLayout partO , partT;
    Animation uptdown, downt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        partO = (RelativeLayout)findViewById(R.id.partO);
        partT = (RelativeLayout)findViewById(R.id.partT);

        uptdown = AnimationUtils.loadAnimation(this, R.anim.uptdown);
        downt = AnimationUtils.loadAnimation(this, R.anim.downt);

        partO.setAnimation(uptdown);
        partT.setAnimation(downt);

        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this,
                            MainProductActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        timer.start();

    }
}
