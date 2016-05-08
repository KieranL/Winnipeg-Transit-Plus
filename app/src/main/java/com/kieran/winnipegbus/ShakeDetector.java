package com.kieran.winnipegbus;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeDetector implements SensorEventListener {
    private static final int MIN_SHAKE_ACCELERATION = 5;

    private static final int MIN_MOVEMENTS = 2;

    private static final int MAX_SHAKE_DURATION = 500;

    // Arrays to store gravity and linear acceleration values
    private float[] gravity = { 0.0f, 0.0f, 0.0f };
    private float[] linearAcceleration = { 0.0f, 0.0f, 0.0f };

    // Indexes for x, y, and z values
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    private OnShakeListener shakeListener;

    private long startTime = 0;

    private int moveCount = 0;

    public ShakeDetector(OnShakeListener shakeListener) {
        this.shakeListener = shakeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Call a helper method that wraps code from the Android developer site
        setCurrentAcceleration(event);

        // Get the max linear acceleration in any direction
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration();

        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            long now = System.currentTimeMillis();

            // Set the startTime if it was reset to zero
            if (startTime == 0) {
                startTime = now;
            }

            long elapsedTime = now - startTime;

            // Check if we're still in the shake window we defined
            if (elapsedTime > MAX_SHAKE_DURATION) {
                // Too much time has passed. Start over!
                resetShakeDetection();
            }
            else {
                // Keep track of all the movements
                moveCount++;

                // Check if enough movements have been made to qualify as a shake
                if (moveCount > MIN_MOVEMENTS) {
                    shakeListener.onShake();

                    resetShakeDetection();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Intentionally blank
    }

    private void setCurrentAcceleration(SensorEvent event) {
        /*
         *  BEGIN SECTION from Android developer site. This code accounts for
         *  gravity using a high-pass filter
         */

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8f;

        // Gravity components of x, y, and z acceleration
        gravity[X] = alpha * gravity[X] + (1 - alpha) * event.values[X];
        gravity[Y] = alpha * gravity[Y] + (1 - alpha) * event.values[Y];
        gravity[Z] = alpha * gravity[Z] + (1 - alpha) * event.values[Z];

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        linearAcceleration[X] = event.values[X] - gravity[X];
        linearAcceleration[Y] = event.values[Y] - gravity[Y];
        linearAcceleration[Z] = event.values[Z] - gravity[Z];

        /*
         *  END SECTION from Android developer site
         */
    }

    private float getMaxCurrentLinearAcceleration() {
        // Start by setting the value to the x value
        float maxLinearAcceleration = linearAcceleration[X];

        // Check if the y value is greater
        if (linearAcceleration[Y] > maxLinearAcceleration) {
            maxLinearAcceleration = linearAcceleration[Y];
        }

        // Check if the z value is greater
        if (linearAcceleration[Z] > maxLinearAcceleration) {
            maxLinearAcceleration = linearAcceleration[Z];
        }

        return maxLinearAcceleration;
    }

    private void resetShakeDetection() {
        startTime = 0;
        moveCount = 0;
    }

    public interface OnShakeListener {
        void onShake();
    }
}