package com.speicys.tomaccotimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.speicys.tomaccotimer.constant.ConstVal;
import com.speicys.tomaccotimer.enums.ClockStateEnum;
import com.speicys.tomaccotimer.enums.TomaccoStateEnum;
import com.speicys.tomaccotimer.service.StringService;

import java.util.Arrays;

/**
 * BUG FIXES:
 *
 * 21- check and fix layouts for several screen types (moto g7 power)
 * 22- either disable landscape or format it differently
 */

/**
 * future features TODO (0.3+):
 *
 * 97- design an extra window/view to explain what the pomodoro technique is
 * 98- implement #97
 * 99- introduce different modes, freestyle, focused, etc..
 * 100- initial popup when app launches (for the first time) explaining what the pomodoro technique is
 * 101- allow users to add tasks and focus on them (individually) with a tomacco
 * 102- after a tomacco finishes, ask the user if they were able to finish task added in #101
 * 103- permanent data storage for overall progress (maintain session if app is closed accidentally, etc)
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Always holds the latest CountDownTimer reference
     */
    private CountDownTimer mTimer;

    /**
     * ClockStateEnum is used to keep a track of the current clock state
     */
    private ClockStateEnum mClockState = ClockStateEnum.NOT_STARTED;

    /**
     * TomaccoStateEnum keeps a global track of Tomacco States, only stored on memory for now
     */
    private TomaccoStateEnum mTomaccoState = TomaccoStateEnum.VANILLA;

    /**
     * tracks the last CountDownTimer's millis left on tick
     */
    private long mCurrentClockMillis;

    /**
     * Tracks whether the rest mode is currently active or not
     */
    private boolean mRestActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        updateTextClock(ConstVal.Clock.ORIGINAL_CLOCK_MINUTES, ConstVal.Clock.ORIGINAL_CLOCK_SECONDS);
    }

    /**
     * Main Button onClick A.K.A. the Start/Stop Button
     */
    public void onMainClick(View view) {
        Button button = (Button) view;
        String buttonText = null;
        ClockStateEnum newState = null;

        switch (mClockState) {

            case NOT_STARTED:
                long newTimerDuration = mRestActive ? ConstVal.Clock.OG_REST_DURATION_MILLIS : ConstVal.Clock.OG_CLOCK_DURATION_MILLIS;
                createTimer(newTimerDuration, ConstVal.Clock.TICK_INTERVAL_SECOND);
                buttonText = getString(R.string.clock_stop);
                newState = ClockStateEnum.STARTED;
                break;

            case STARTED:
                mTimer.cancel();
                buttonText = getString(R.string.clock_start);
                newState = ClockStateEnum.PAUSED;
                break;

            case PAUSED:
                createTimer(mCurrentClockMillis, ConstVal.Clock.TICK_INTERVAL_SECOND);
                buttonText = getString(R.string.clock_stop);
                newState = ClockStateEnum.STARTED;
                break;
        }
        button.setText(buttonText);
        mClockState = newState;
    }

    /**
     * Resets an ongoing clock and plays a small animation afterwards
     */
    public void onResetClick(View view) {
        if (mClockState.equals(ClockStateEnum.NOT_STARTED))
            return;

        resetGlobalClock();
        this.playAnimationOnView(R.anim.rotation, findViewById(R.id.tomaccoView));
        this.playAnimationOnView(R.anim.shrink_and_grow, findViewById(R.id.clockTextView));
    }

    /**
     * Triggers an exit from Pre-Rest mode
     * starts a new clock via onMainClick()
     */
    public void onRestClick(View view) {

        Button mainButton = findViewById(R.id.mainClockButton);
        onMainClick(mainButton);

        ImageView tomacco = findViewById(R.id.tomaccoView);
        tomacco.setImageResource(R.drawable.tomacco_state_rest);

        playAnimationOnView(R.anim.downwards_translation, view);
        changeViewAccessibility(false, view);

        Button resetButton = findViewById(R.id.resetClockButton);

        playAnimationOnView(R.anim.upwards_translation, mainButton, resetButton);
        changeViewColor(getColor(R.color.grey), mainButton, resetButton);
        changeViewAccessibility(true, resetButton, mainButton);

        Toast.makeText(this, getString(R.string.rest_active), Toast.LENGTH_LONG).show();
    }

    private void createTimer(long targetMillis, long interval) {
        mTimer = new CountDownTimer(targetMillis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / 1000) / 60);
                mCurrentClockMillis = millisUntilFinished;
                updateTextClock(minutes, seconds);
            }

            @Override
            public void onFinish() {
                executeFinishActions();
            }
        };
        mTimer.start();
    }

    private void executeFinishActions() {
        if (mRestActive) {
            executeFinishRest();

        } else {
            matureTomacco();
            startPreRest();
        }
        resetGlobalClock();
        playFinishSound(0);
        triggerTomaccoNotification();
    }

    /**
     * Ends the rest period, returns every view to their normal state
     */
    private void executeFinishRest() {
        Button startButton = findViewById(R.id.mainClockButton);
        Button resetButton = findViewById(R.id.resetClockButton);
        ImageView tomacco = findViewById(R.id.tomaccoView);

        tomacco.setImageResource(mTomaccoState.getDrawableId());
        changeViewColor(getColor(R.color.material_red), startButton, resetButton);

        playAnimationOnView(R.anim.shrink_and_grow, tomacco, startButton, resetButton);
        mRestActive = false;
    }

    /**
     *  Starts the Pre-Rest period, where the user can choose to rest 5 minutes after the click of a button
     *  TODO: Long-press tomacco to open aditional options (ver 0.3+)
     */
    private void startPreRest() {
        mRestActive = true;
        Button startButton = findViewById(R.id.mainClockButton);
        Button resetButton = findViewById(R.id.resetClockButton);

        playAnimationOnView(R.anim.downwards_translation, startButton, resetButton);
        changeViewAccessibility(false, startButton, resetButton);

        Button restButton = findViewById(R.id.startRestButton);
        changeViewAccessibility(true, restButton);
        this.playAnimationOnView(R.anim.upwards_translation, restButton);
    }

    /**
     * @param enable defines whether or not the given Views are enabled or disabled
     */
    private void changeViewAccessibility(boolean enable, View... views) {
        Arrays.asList(views).stream().forEach(view -> {
            view.setVisibility(enable ? View.VISIBLE : View.GONE);
            view.setClickable(enable);
        });
    }

    private void changeViewColor(int color, View... views) {
        for(View view : views)
            view.setBackgroundColor(color);
    }

    private void matureTomacco() {
        ImageView tomato = findViewById(R.id.tomaccoView);
        this.playAnimationOnView(R.anim.shrink_and_grow, tomato);
        mTomaccoState.mature();
        tomato.setImageResource(mTomaccoState.getDrawableId());
    }

    /**
     * Plays a given animation for a single object
     */
    private void playAnimationOnView(int animationId, View view) {
        Animation animation = AnimationUtils.loadAnimation(this, animationId);
        view.startAnimation(animation);
    }

    /**
     * Plays a given animation on the input View objects
     */
    private void playAnimationOnView(int animationId, View... views) {
        Animation animation = AnimationUtils.loadAnimation(this, animationId);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setStartOffset(ConstVal.Animation.GROUP_ANIMATION_OFFSET);
        for (View view : views)
            view.startAnimation(animation);

    }

    /**
     * Updates the numerical clock's text view in order to match the real timer
     */
    private void updateTextClock(Integer minutes, Integer seconds) {
        TextView clockTextView = findViewById(R.id.clockTextView);
        String showMinutes = StringService.padLeftZeros(minutes.toString(), 2);
        String showSeconds = StringService.padLeftZeros(seconds.toString(), 2);
        clockTextView.setText(String.format(ConstVal.View.FORMATTED_CLOCK, showMinutes, showSeconds));
    }

    /**
     * if the clock has started, cancels the current timer, updates START Button
     */
    private void resetGlobalClock() {
        mClockState = ClockStateEnum.NOT_STARTED;
        mTimer.cancel();

        if (mRestActive) {
            updateTextClock(ConstVal.Clock.ORIGINAL_REST_MINUTES, ConstVal.Clock.ORIGINAL_REST_SECONDS);
        } else {
            updateTextClock(ConstVal.Clock.ORIGINAL_CLOCK_MINUTES, ConstVal.Clock.ORIGINAL_CLOCK_SECONDS);
        }

        Button startButton = findViewById(R.id.mainClockButton);
        startButton.setText(R.string.clock_start);
    }

    private void playFinishSound(int durationMillis) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.ta_da_sound);
        mediaPlayer.start();
        if(durationMillis != 0)
            new Handler().postDelayed(() -> mediaPlayer.stop(), durationMillis);
    }

    private void triggerTomaccoNotification() {
        Notification notification = getNotification();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(ConstVal.Notification.TOMACCO_END_NOTIFICATION_ID, notification);
    }

    /**
     * Creates a notification channel, should be created when the activity is initializing
     */
    private void createNotificationChannel() {
        CharSequence name = getString(R.string.clock_end_notification_name);
        String description = getString(R.string.clock_end_notification_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(ConstVal.Notification.TOMACCO_END_NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ConstVal.Notification.TOMACCO_END_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.tomacco_state_rest)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent());
        return builder.build();
    }

    /**
     * Returns a new PendingIntent based on the current Intent
     */
    private PendingIntent getPendingIntent() {
        Intent intent = this.getIntent();
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

}