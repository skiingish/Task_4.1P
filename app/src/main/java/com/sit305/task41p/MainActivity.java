package com.sit305.task41p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;

// Created by Sean Corcoran for unit SIT305 - Deakin University 19/04/2020)

public class MainActivity extends AppCompatActivity {
    // The sharedPreferences to save and pickup on the required values.
    SharedPreferences sharedPreferences;

    // The different views.
    TextView lastWorkInfo_TV;
    Chronometer timer;
    EditText workoutType_ET;

    // Timer related vars
    long totalTime = 0;
    long startTime = 0;
    long stopTime = 0;
    long timerWidgetStoppedTime = 0;

    // If the timer has been paused by the user.
    boolean timerPaused = false;

    // If the timer is actively running.
    boolean timerActive = false;

    // Workout info related strings (with their starting values)
    String currentWorkoutType = "";
    String pastWorkoutTime = "00:00";
    String pastWorkoutType = "push ups";

    // sharedPreferences Strings
    String TOTAL_TIME = "TOTAL_TIME";
    String START_TIME = "START_TIME";
    String T_WIDGET_STOP_TIME = "T_WIDGET_STOP_TIME";
    String TIMER_PAUSED = "TIMER_PAUSED";
    String TIMER_ACTIVE = "TIMER_ACTIVE";
    String CURRENT_WORKOUT_TYPE = "CURRENT_WORKOUT_TYPE";
    String PAST_WORKOUT_TIME = "PAST_WORKOUT_TIME";
    String PAST_WORKOUT_TYPE = "PAST_WORKOUT_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the different views.
        lastWorkInfo_TV = findViewById(R.id.lastWorkInfo_TV);
        timer = findViewById(R.id.timer);
        workoutType_ET = findViewById(R.id.workoutType_ET);

        // Get the shared preferences.
        sharedPreferences = getSharedPreferences("com.sit305.com.sit305.task41p", MODE_PRIVATE);

        // Set the required values if carrying over.
        totalTime = sharedPreferences.getLong(TOTAL_TIME, 0);
        startTime = sharedPreferences.getLong(START_TIME, 0);
        timerWidgetStoppedTime = sharedPreferences.getLong(T_WIDGET_STOP_TIME, 0);
        timerPaused = sharedPreferences.getBoolean(TIMER_PAUSED, false);
        timerActive = sharedPreferences.getBoolean(TIMER_ACTIVE, false);

        // Work out types and times.
        currentWorkoutType = sharedPreferences.getString(CURRENT_WORKOUT_TYPE, "");
        pastWorkoutTime = sharedPreferences.getString(PAST_WORKOUT_TIME, "00:00:00");
        pastWorkoutType = sharedPreferences.getString(PAST_WORKOUT_TYPE, "nil");

        // Set the EditText if user already set it.
        workoutType_ET.setText(currentWorkoutType);

        // If there was a workout recorded previously display so.
        lastWorkInfo_TV.setText("You spent " + pastWorkoutTime +" on " + pastWorkoutType + " last time.");

        // Check if the timer needs to be restarted (if it was already running or is it's been running and is currently paused).
        checkTimerOnCreate();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Stop the active widget save the values for transition.
        if (timerActive) {

            // Set the time the timer was stopped.
            stopTime = SystemClock.elapsedRealtime();

            // Cal the total running time. (If there's already been time passed add to it)
            totalTime = totalTime + (stopTime - startTime);

            // And stop the timer widget
            timer.stop();

            // Get widget stopped value
            timerWidgetStoppedTime = timer.getBase() - stopTime;
        }

        // Get the current type from the WT_ET as it'll be carried over if not blank.
        currentWorkoutType = workoutType_ET.getText().toString();

        // Store the different Vars
        sharedPreferences.edit().putLong(TOTAL_TIME, totalTime).apply();
        sharedPreferences.edit().putLong(START_TIME, startTime).apply();
        sharedPreferences.edit().putLong(T_WIDGET_STOP_TIME, timerWidgetStoppedTime).apply();
        sharedPreferences.edit().putBoolean(TIMER_PAUSED, timerPaused).apply();
        sharedPreferences.edit().putBoolean(TIMER_ACTIVE, timerActive).apply();
        sharedPreferences.edit().putString(CURRENT_WORKOUT_TYPE, currentWorkoutType).apply();
        sharedPreferences.edit().putString(PAST_WORKOUT_TIME, pastWorkoutTime).apply();
        sharedPreferences.edit().putString(PAST_WORKOUT_TYPE, pastWorkoutType).apply();
    }

    // Check the timer when starting.
    private void checkTimerOnCreate() {

        // If the time is currently active then it will need to be restarted.
        if (timerActive) {
            // Reset the start time.
            startTime = SystemClock.elapsedRealtime();

            // The timer is starting again from the current start time plus it's stopped value.
            timer.setBase(startTime + timerWidgetStoppedTime);

            // Start the timer.
            timer.start();
        }
        else if (timerPaused) {
            // The timer is setup again from the current system time plus it's stopped value.
            timer.setBase(SystemClock.elapsedRealtime() + timerWidgetStoppedTime);
        }
    }

    public void playOnClick(View view) {

        // If the timer is not active and it's not paused then start for the first time.
        if (!timerActive && !timerPaused) {
            // Set the timer's base the current system elapsed time.
            timer.setBase(SystemClock.elapsedRealtime());

            // Start the time;
            timer.start();

            // Set the start time
            startTime = SystemClock.elapsedRealtime();

            // Timer is currently actively counting.
            timerActive = true;
        }
        else if (timerPaused) {
            // If the timer is paused then restart.

            // Reset the start time.
            startTime = SystemClock.elapsedRealtime();

            // The timer is starting again from the current start time plus it's stopped value.
            timer.setBase(startTime + timerWidgetStoppedTime);

            // Start the timer.
            timer.start();

            // Timer is no longer paused
            timerPaused = false;

            //lastWorkInfo_TV.setText("Restart Time:" + startTime + " Pause Time: " + stopTime + " Total Time: " + totalTime);

            // Timer is currently actively counting.
            timerActive = true;
        }

        // Else if the timer is already active clicking on the play button won't do anything.
    }

    public void pauseOnClick(View view) {

        // If the timer is active then it can be paused.
        if (timerActive) {
            pauseTimer();
        }
    }

    private void pauseTimer() {

        // Set the time the timer was stopped.
        stopTime = SystemClock.elapsedRealtime();

        // Cal the total running time. (If there's already been time passed add to it)
        totalTime = totalTime + (stopTime - startTime);

        // Timer is currently paused.
        timerPaused = true;

        // And stop the timer widget
        timer.stop();

        // Get widget stopped value
        timerWidgetStoppedTime = timer.getBase() - stopTime;

        // Timer is stopped.
        timerActive = false;
    }

    public void recordOnClick(View view) {
        // If the timer is active then pause and record the time first
        if (timerActive) {
            pauseTimer();
        }

        // Get the total time and convert the correct ints.
        int h = (int) (totalTime / 3600000);
        int m = (int) (totalTime - h * 3600000) / 60000;
        int s = (int) (totalTime - h * 3600000 - m * 60000) / 1000 ;

        // Convert those ints to strings taking into account on the amount of digits required.
        String hh = h < 10 ? "0"+h: h+"";
        String mm = m < 10 ? "0"+m: m+"";
        String ss = s < 10 ? "0"+s: s+"";

        // Set the past workout time string.
        pastWorkoutTime = hh + ":" + mm + ":" + ss;

        // Get the finished workout type from the edit text, and set it as the past one.
        pastWorkoutType = workoutType_ET.getText().toString();

        // Set the EditText back to blank.
        workoutType_ET.setText("");

        // Reset the timer.
        resetTimer();

        // Display the last completed workout and time.
        lastWorkInfo_TV.setText("You spent " + pastWorkoutTime +" on " + pastWorkoutType + " last time.");
    }

    private void resetTimer() {
        // Reset the timer display.
        timer.setBase(SystemClock.elapsedRealtime());

        // Reset all the timer related fields back to their starting positions.
        totalTime = 0;
        startTime = 0;
        stopTime = 0;
        timerWidgetStoppedTime = 0;
        timerPaused = false;
        timerActive = false;
    }
}