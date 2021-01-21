package com.speicys.tomaccotimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.speicys.tomaccotimer.enums.ClockStateEnum;
import com.speicys.tomaccotimer.util.StringUtils;

import java.time.Clock;

public class MainActivity extends AppCompatActivity {

    private static final Long ORIGINAL_DURATION = 10000l;
    private static final Long TICK_INTERVAL = ORIGINAL_DURATION / 10;

    private CountDownTimer timer;

    private ClockStateEnum state = ClockStateEnum.NOT_STARTED;

    private Integer minutes;
    private Integer seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startClock(View view) {
        Button button = (Button) view;
        String buttonText = null;
        ClockStateEnum newState = null;

        switch (state) {

            case NOT_STARTED:
                createTimer(ORIGINAL_DURATION, TICK_INTERVAL);
                buttonText = getString(R.string.clock_stop);
                newState = ClockStateEnum.STARTED;
                break;

            case STARTED:
                timer.cancel();
                buttonText = getString(R.string.clock_start);
                newState = ClockStateEnum.PAUSED;
                break;

            case PAUSED:
                long newMillis = ((minutes * 60) + seconds) * 1000;
                createTimer(newMillis, TICK_INTERVAL);
                buttonText = getString(R.string.clock_stop);
                newState = ClockStateEnum.STARTED;
                break;
        }
        button.setText(buttonText);
        state = newState;
    }

    public void resetClock(View view) {
        if (state.equals(ClockStateEnum.NOT_STARTED))
            return;

        minutes = Math.toIntExact(ORIGINAL_DURATION / (1000 * 60));
        seconds = Math.toIntExact(ORIGINAL_DURATION / 1000);
        state = ClockStateEnum.NOT_STARTED;
        timer.cancel();
        changeTextView(minutes, seconds);
        Button startButton = findViewById(R.id.startClockButton);
        startButton.setText(R.string.clock_start);
    }

    private void createTimer(long targetMillis, long interval) {
        timer = new CountDownTimer(targetMillis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds = (int) (millisUntilFinished / 1000);
                minutes = (int) ((millisUntilFinished / 1000) / 60);
                changeTextView(minutes, seconds);
            }

            @Override
            public void onFinish() {
                findViewById(R.id.tomatoView).setAlpha(0);
            }
        };
        timer.start();
    }

    private void changeTextView(Integer minutes, Integer seconds) {
        TextView clockTextView = findViewById(R.id.clockTextView);
        String showMinutes = StringUtils.padLeftZeros(minutes.toString(), 2);
        String showSeconds = StringUtils.padLeftZeros(seconds.toString(), 2);
        clockTextView.setText(String.format("%s:%s", showMinutes, showSeconds));
    }

}