package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class ShakeDetector(
    private val context: Context,
    private val onShakeCountFired: (count: Int) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val scope = CoroutineScope(Dispatchers.Main)
    private var evaluateJob: Job? = null

    private var currentShakeCount = 0
    private var lastShakeTimestamp: Long = 0
    private var lastDirectionPositive = false

    // Sensitivity threshold (default ~13.5 m/s^2)
    var shakeThreshold = 13.5f

    private val _liveGForce = MutableStateFlow(0f)
    val liveGForce = _liveGForce.asStateFlow()

    private var isListening = false

    fun start() {
        if (isListening || accelerometer == null) return
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        isListening = true
    }

    fun stop() {
        if (!isListening) return
        sensorManager?.unregisterListener(this)
        evaluateJob?.cancel()
        currentShakeCount = 0
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val netForce = kotlin.math.abs(magnitude - SensorManager.GRAVITY_EARTH)
        _liveGForce.value = netForce

        val now = System.currentTimeMillis()
        if (netForce > shakeThreshold) {
            val directionPositive = (x + y) > 0

            // Require minimum delay between shake direction reversals (140ms) to avoid single movement double-counting
            if (now - lastShakeTimestamp > 140 && directionPositive != lastDirectionPositive) {
                lastShakeTimestamp = now
                lastDirectionPositive = directionPositive
                currentShakeCount++

                evaluateJob?.cancel()
                evaluateJob = scope.launch {
                    // Wait for user to stop shaking
                    delay(700)
                    if (currentShakeCount >= 3) {
                        onShakeCountFired(currentShakeCount)
                    }
                    currentShakeCount = 0
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
