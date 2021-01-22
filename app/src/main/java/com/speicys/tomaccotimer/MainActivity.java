package com.speicys.tomaccotimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.speicys.tomaccotimer.constant.ConstVal;
import com.speicys.tomaccotimer.enums.ClockStateEnum;
import com.speicys.tomaccotimer.enums.TomaccoStateEnum;
import com.speicys.tomaccotimer.util.StringUtils;

public class MainActivity extends AppCompatActivity {

    /**
     * Always holds the latest CountDownTimer reference
     */
    private CountDownTimer timer;

    /**
     * ClockStateEnum is used to keep a track of the current clock state
     */
    private ClockStateEnum clockState = ClockStateEnum.NOT_STARTED;

    /**
     * TomaccoStateEnum keeps a global track of Tomacco States, only stored on memory for now
     */
    private TomaccoStateEnum tomaccoState = TomaccoStateEnum.VANILLA;

    private Integer minutes;
    private Integer seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateTextClock(ConstVal.Clock.ORIGINAL_CLOCK_MINUTES, ConstVal.Clock.ORIGINAL_CLOCK_SECONDS);
    }

    public void onStartClick(View view) {
        Button button = (Button) view;
        String buttonText = null;
        ClockStateEnum newState = null;

        switch (clockState) {

            case NOT_STARTED:
                createTimer(ConstVal.Clock.OG_CLOCK_DURATION_MILIS, ConstVal.Clock.TICK_INTERVAL_SECOND);
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
                createTimer(newMillis, ConstVal.Clock.TICK_INTERVAL_SECOND);
                buttonText = getString(R.string.clock_stop);
                newState = ClockStateEnum.STARTED;
                break;
        }
        button.setText(buttonText);
        clockState = newState;
    }

    public void onResetClick(View view) {
        this.resetGlobalClock();
    }

    private void createTimer(long targetMillis, long interval) {
        timer = new CountDownTimer(targetMillis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds = (int) (millisUntilFinished / 1000);
                minutes = (int) ((millisUntilFinished / 1000) / 60);
                updateTextClock(minutes, seconds);
            }

            @Override
            public void onFinish() {
                ImageView tomato = findViewById(R.id.tomatoView);
                resetGlobalClock();
                loadScalingAnimation(tomato);
                tomaccoState.nextStage();
                tomato.setImageResource(tomaccoState.getDrawableId());
            }
        };
        timer.start();
    }

    private void loadScalingAnimation(ImageView view) {
        Animation scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.shrink_and_grow);
        view.startAnimation(scaleDownAnimation);
    }

    private void updateTextClock(Integer minutes, Integer seconds) {
        TextView clockTextView = findViewById(R.id.clockTextView);
        String showMinutes = StringUtils.padLeftZeros(minutes.toString(), 2);
        String showSeconds = StringUtils.padLeftZeros(seconds.toString(), 2);
        clockTextView.setText(String.format("%s:%s", showMinutes, showSeconds));
    }

    private void resetGlobalClock() {
        if (clockState.equals(ClockStateEnum.NOT_STARTED))
            return;

        clockState = ClockStateEnum.NOT_STARTED;
        timer.cancel();
        updateTextClock(ConstVal.Clock.ORIGINAL_CLOCK_MINUTES, ConstVal.Clock.ORIGINAL_CLOCK_SECONDS);
        Button startButton = findViewById(R.id.startClockButton);
        startButton.setText(R.string.clock_start);
    }

}