package com.kieran.winnipegbus

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class ShakeDetector(private val shakeListener: OnShakeListener) : SensorEventListener {

    // Arrays to store gravity and linear acceleration values
    private val gravity = floatArrayOf(0.0f, 0.0f, 0.0f)
    private val linearAcceleration = floatArrayOf(0.0f, 0.0f, 0.0f)

    private var startTime: Long = 0

    private var moveCount = 0

    private// Start by setting the value to the x value
    // Check if the y value is greater
    // Check if the z value is greater
    val maxCurrentLinearAcceleration: Float
        get() {
            var maxLinearAcceleration = linearAcceleration[X]
            if (linearAcceleration[Y] > maxLinearAcceleration) {
                maxLinearAcceleration = linearAcceleration[Y]
            }
            if (linearAcceleration[Z] > maxLinearAcceleration) {
                maxLinearAcceleration = linearAcceleration[Z]
            }

            return maxLinearAcceleration
        }

    override fun onSensorChanged(event: SensorEvent) {
        // Call a helper method that wraps code from the Android developer site
        setCurrentAcceleration(event)

        // Get the max linear acceleration in any direction
        val maxLinearAcceleration = maxCurrentLinearAcceleration

        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            val now = System.currentTimeMillis()

            // Set the startTime if it was reset to zero
            if (startTime == 0L) {
                startTime = now
            }

            val elapsedTime = now - startTime

            // Check if we're still in the shake window we defined
            if (elapsedTime > MAX_SHAKE_DURATION) {
                // Too much time has passed. Start over!
                resetShakeDetection()
            } else {
                // Keep track of all the movements
                moveCount++

                // Check if enough movements have been made to qualify as a shake
                if (moveCount > MIN_MOVEMENTS) {
                    shakeListener.onShake()

                    resetShakeDetection()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Intentionally blank
    }

    private fun setCurrentAcceleration(event: SensorEvent) {
        /*
         *  BEGIN SECTION from Android developer site. This code accounts for
         *  gravity using a high-pass filter
         */

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        val alpha = 0.8f

        // Gravity components of x, y, and z acceleration
        gravity[X] = alpha * gravity[X] + (1 - alpha) * event.values[X]
        gravity[Y] = alpha * gravity[Y] + (1 - alpha) * event.values[Y]
        gravity[Z] = alpha * gravity[Z] + (1 - alpha) * event.values[Z]

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        linearAcceleration[X] = event.values[X] - gravity[X]
        linearAcceleration[Y] = event.values[Y] - gravity[Y]
        linearAcceleration[Z] = event.values[Z] - gravity[Z]

        /*
         *  END SECTION from Android developer site
         */
    }

    private fun resetShakeDetection() {
        startTime = 0
        moveCount = 0
    }

    interface OnShakeListener {
        fun onShake()
    }

    companion object {
        private val MIN_SHAKE_ACCELERATION = 5

        private val MIN_MOVEMENTS = 2

        private val MAX_SHAKE_DURATION = 500

        // Indexes for x, y, and z values
        private val X = 0
        private val Y = 1
        private val Z = 2
    }
}