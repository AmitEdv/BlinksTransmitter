package com.example.blinkstransmitter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int INITIAL_DELAY_millies = 10000; //10 sec
    private static final int SCREEN_OFF_DURATION_millies = 500; //0.5 sec
    private static final int SCREEN_ON_DURATION_millies = 500; //0.5 sec
    private static final int SCREEN_BLINK_DURATION_millies = SCREEN_ON_DURATION_millies + SCREEN_OFF_DURATION_millies;
    private static final int STAGE_1_REQUIRED_BLINKS_AMOUNT = 4;

    private LinearLayout mRightBlinkLinearLayout;
    private Timer mTimer = new Timer();
    private int mStageOneBlinksCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRightBlinkLinearLayout = findViewById(R.id.right_blink_ll);
    }

    public void onClick_stage1(View view) {

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setScreenOn();
            }
        }, INITIAL_DELAY_millies , SCREEN_BLINK_DURATION_millies);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setScreenOff();
            }
        }, INITIAL_DELAY_millies + SCREEN_ON_DURATION_millies, SCREEN_BLINK_DURATION_millies);
    }

    private void setScreenOn()
    {
        mStageOneBlinksCounter++;

        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mRightBlinkLinearLayout.setBackgroundColor(Color.WHITE);
                    }
                }
        );
    }

    private void setScreenOff()
    {
        if (mStageOneBlinksCounter >= STAGE_1_REQUIRED_BLINKS_AMOUNT) {
            resetStage1();
        }

        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mRightBlinkLinearLayout.setBackgroundColor(Color.BLACK);
                    }
                }
        );
    }

    private void resetStage1() {
        mStageOneBlinksCounter = 0;
        mTimer.cancel();
    }

}