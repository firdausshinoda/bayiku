package com.example.bayiku.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.example.bayiku.databinding.ActivitySplashBinding;
import androidx.appcompat.app.AppCompatActivity;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private Context context;
    public static final int second = 2;
    public static final int milisecond = second * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setLoading();
    }

    private void setLoading() {
        new CountDownTimer(milisecond,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);;
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }.start();
    }
}