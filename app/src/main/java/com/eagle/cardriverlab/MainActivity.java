package com.eagle.cardriverlab;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageButton menuButton, accelerate, brake;
    private TextView CarName, MaxSpeed, CurrentSpeed, accpersec, breakpersec;
    private PreferencesManager preferencesManager;

    private SoundPool soundPool;
    private int accSound, decSound;
    private int activeSoundStream = -1; // Keeps track of the active sound stream
    private long startPressTime;
    private Handler handler = new Handler();

    private float currentSpeed = 0; // Current speed of the car
    private int maxSpeed;
    private int accelerationPerSec; // Acceleration rate per second
    private int decelerationPerSec; // Deceleration rate per second

    // Flag to check if a button is being pressed
    private boolean isAccelerating = false;
    private boolean isBraking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PreferencesManager
        preferencesManager = new PreferencesManager(this);

        // Initialize Views
        initializeViews();

        // Set up SoundPool
        setupSoundPool();

        menuButton.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, menunav.class);
            startActivity(i);
        });

        CarName.setOnClickListener(v -> {
                    Intent i = new Intent(MainActivity.this, CarModels.class);
                    startActivity(i);
                });

        // Handle incoming data from the intent
        handleIncomingData();

        // Display car data
        displayCarData();

        // Set up button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        menuButton = findViewById(R.id.ibtnHamMenu);
        CarName = findViewById(R.id.tvCarName);
        MaxSpeed = findViewById(R.id.tvMaxSpeed);
        CurrentSpeed = findViewById(R.id.tvCurrentSpeed);
        accelerate = findViewById(R.id.ibtnAccelerate);
        brake = findViewById(R.id.ibtnBreak);
        accpersec = findViewById(R.id.accpersec);
        breakpersec = findViewById(R.id.breakpersec);
    }

    private void setupSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        accSound = soundPool.load(this, R.raw.acc, 1);
        decSound = soundPool.load(this, R.raw.dec, 1);
    }

    private void setupCarData() {
        CarName.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CarModels.class);
            startActivity(i);
        });

        handleIncomingData();
        displayCarData();
    }

    private void handleIncomingData() {
        Intent intent = getIntent();
        if (intent != null) {
            String carName = intent.getStringExtra("CARNAME");
            String maxSpeedStr = intent.getStringExtra("MAXSPEED");
            String accelerationStr = intent.getStringExtra("ACCELERATION");
            String decelerationStr = intent.getStringExtra("DECELERATION");

            // If string extras are null, try getting integer extras (for the first way)
            if (maxSpeedStr == null) {
                int maxSpeedInt = intent.getIntExtra("MAXSPEED", 0);
                int accelerationInt = intent.getIntExtra("ACCELERATION", 0);
                int decelerationInt = intent.getIntExtra("DECELERATION", 0);

                maxSpeedStr = String.valueOf(maxSpeedInt);
                accelerationStr = String.valueOf(accelerationInt);
                decelerationStr = String.valueOf(decelerationInt);
            }

            if (carName != null && maxSpeedStr != null && accelerationStr != null && decelerationStr != null) {
                try {
                    maxSpeed = Integer.parseInt(maxSpeedStr);
                    accelerationPerSec = Integer.parseInt(accelerationStr);
                    decelerationPerSec = Integer.parseInt(decelerationStr);

                    // Save the new car data to PreferencesManager
                    preferencesManager.saveCarData(carName, maxSpeed, accelerationPerSec, decelerationPerSec);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid car data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void displayCarData() {
        String carName = preferencesManager.getCarName();
        maxSpeed = preferencesManager.getMaxSpeed();
        accelerationPerSec = preferencesManager.getAcceleration();
        decelerationPerSec = preferencesManager.getDeceleration();
        MaxSpeed.setText(String.format("%d km/h", maxSpeed));
        CarName.setText(carName);
        CurrentSpeed.setText("0.0");
    }

    private void setupButtonListeners() {
        accelerate.setOnTouchListener((v, event) -> {
            accpersec.setVisibility(View.VISIBLE);
            handleButtonEvent(event, accSound, accelerate, accpersec, true);
            return true;
        });

        brake.setOnTouchListener((v, event) -> {
            breakpersec.setVisibility(View.VISIBLE);
            handleButtonEvent(event, decSound, brake, breakpersec, false);
            return true;
        });
    }

    private void handleButtonEvent(MotionEvent event, int soundId, View button, TextView timeTextView, boolean isAccelerating) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (activeSoundStream != -1) {
                    soundPool.stop(activeSoundStream);
                }
                button.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200).start();
                activeSoundStream = soundPool.play(soundId, 1, 1, 0, -1, 1);
                startPressTime = System.currentTimeMillis();

                timeTextView.setVisibility(View.VISIBLE);

                // Set the appropriate flags
                if (isAccelerating) {
                    this.isAccelerating = true;
                    this.isBraking = false;
                } else {
                    this.isAccelerating = false;
                    this.isBraking = true;
                }

                updateSpeed(isAccelerating);
                updateTimeText(timeTextView);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                button.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
                if (activeSoundStream != -1) {
                    soundPool.stop(activeSoundStream);
                    activeSoundStream = -1;
                }
                timeTextView.setVisibility(View.GONE);

                // Reset the appropriate flag
                if (isAccelerating) {
                    this.isAccelerating = false;
                } else {
                    this.isBraking = false;
                }

                // Stop the current speed updates
                handler.removeCallbacksAndMessages(null);

                // Start auto-deceleration if no buttons are pressed
                if (!this.isAccelerating && !this.isBraking) {
                    startAutoDeceleration();
                }
                break;
        }
    }

    private void startAutoDeceleration() {
        final Runnable deceleration = new Runnable() {
            @Override
            public void run() {
                if (!isAccelerating && !isBraking && currentSpeed > 0) {
                    float autoDecelerationRate = decelerationPerSec / 2.0f;
                    currentSpeed -= autoDecelerationRate * 0.1f;

                    currentSpeed = Math.max(currentSpeed, 0);

                    CurrentSpeed.setText(String.format("%.1f", currentSpeed));

                    // Continue the auto-deceleration if there's still speed
                    if (currentSpeed > 0) {
                        handler.postDelayed(this, 100); // Update every 100ms for smoother deceleration
                    } else {
                        // Stop the auto-deceleration when speed reaches 0
                        handler.removeCallbacks(this);
                    }
                }
            }
        };

        // Start the auto-deceleration immediately
        handler.post(deceleration);
    }

    private void updateSpeed(boolean isAccelerating) {
        handler.removeCallbacksAndMessages(null); // Clear previous callbacks

        final Runnable speedUpdate = new Runnable() {
            @Override
            public void run() {
                if (isAccelerating && currentSpeed < maxSpeed) {
                    currentSpeed += accelerationPerSec * 0.1f;
                    currentSpeed = Math.min(currentSpeed, maxSpeed);
                } else if (!isAccelerating && currentSpeed > 0) {
                    currentSpeed -= decelerationPerSec * 0.1f;
                    currentSpeed = Math.max(currentSpeed, 0);
                }

                CurrentSpeed.setText(String.format("%.1f", currentSpeed));

                if ((isAccelerating && currentSpeed < maxSpeed) || (!isAccelerating && currentSpeed > 0)) {
                    handler.postDelayed(this, 100);
                }
            }
        };

        handler.post(speedUpdate);
    }

    private void updateTimeText(final TextView timeTextView) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activeSoundStream != -1) {
                    long elapsedTime = System.currentTimeMillis() - startPressTime;
                    long seconds = elapsedTime / 1000;
                    long milliseconds = elapsedTime % 1000;
                    String formattedTime = String.format("%d.%03ds", seconds, milliseconds);
                    timeTextView.setText(formattedTime);
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activeSoundStream != -1) {
            soundPool.stop(activeSoundStream);
            activeSoundStream = -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}