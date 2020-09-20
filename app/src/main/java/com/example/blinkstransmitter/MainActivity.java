package com.example.blinkstransmitter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int INITIAL_DELAY_millies = 5000; //5 sec
    private static final int DELAY_BETWEEN_BITS_DURATION_millies = 500; //0.5 sec
    private static final int BIT_TRANSFER_DURATION_millies = 500; //0.5 sec
    private static final int BIT_TRANSFER_FULL_DUTY_CYCLE_DURATION_millies = BIT_TRANSFER_DURATION_millies + DELAY_BETWEEN_BITS_DURATION_millies;
    private static final String SEQUENCE_TO_TRANSMIT = "1111";

    private LinearLayout mRightBlinkLinearLayout;
    private Button mStartTransmitButton;
    private Timer mTimer;
    String mSequenceToTransmitQueueMSB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRightBlinkLinearLayout = findViewById(R.id.right_blink_ll);
        mStartTransmitButton = findViewById(R.id.start_transmit_btn);
    }

    public void onClick_transmit(View view) {
        String sequenceToTransmit = SEQUENCE_TO_TRANSMIT;
        Log.i(TAG, "onClick_transmit(): sequenceToTransmit = " + sequenceToTransmit);
        if (!isInputToTransmitValid(sequenceToTransmit)) {
            return;
        }

        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mStartTransmitButton.setEnabled(false);
                    }
                }
        );

        mSequenceToTransmitQueueMSB = sequenceToTransmit;
        mTimer =  new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                transmitNextBit();
            }
        }, INITIAL_DELAY_millies , BIT_TRANSFER_FULL_DUTY_CYCLE_DURATION_millies);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setOffAndWaitBetweenBits();
            }
        }, INITIAL_DELAY_millies + BIT_TRANSFER_DURATION_millies, BIT_TRANSFER_FULL_DUTY_CYCLE_DURATION_millies);
    }

    private boolean isInputToTransmitValid(String input) {
        if (input.isEmpty()) {
            return false;
        }

        if (!input.matches("^[01]+$")) {
            Log.e(TAG, "input not valid");
            return false;
        }

        return true;
    }

    private void transmitNextBit()
    {
        Log.i(TAG, "transmitNextBit: called");
        if (mSequenceToTransmitQueueMSB.length() == 0) {
            endTransmission();
            return;
        }

        Log.d(TAG, "transmitNextBit: mSequenceToTransmitQueueMSB = " + mSequenceToTransmitQueueMSB);
        final boolean isDataBitOn = Integer.parseInt(mSequenceToTransmitQueueMSB.substring(0,1)) == 1;
        mSequenceToTransmitQueueMSB = mSequenceToTransmitQueueMSB.substring(1);
        Log.d(TAG, "transmitNextBit: dataOn = " + isDataBitOn + " subString = " + mSequenceToTransmitQueueMSB);

        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (isDataBitOn) {
                            mRightBlinkLinearLayout.setBackgroundColor(Color.WHITE);
                        }
                    }
                }
        );

    }

    private void setOffAndWaitBetweenBits()
    {
        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mRightBlinkLinearLayout.setBackgroundColor(Color.BLACK);
                    }
                }
        );
    }

    private void endTransmission() {
        mTimer.cancel();

        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mStartTransmitButton.setEnabled(true);
                    }
                }
        );
    }



} //END CLASS