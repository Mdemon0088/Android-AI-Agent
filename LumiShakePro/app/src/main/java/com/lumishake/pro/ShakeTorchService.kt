package com.lumishake.pro

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.*
import androidx.core.app.NotificationCompat
import kotlin.math.sqrt

class ShakeTorchService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var cameraManager: CameraManager
    private var backCameraId: String? = null
    private var isTorchOn = false
    private var isPocketCovered = false
    private val gravity = FloatArray(3)
    private val linearAcceleration = FloatArray(3)
    private var currentGyroMagnitude = 0f
    private val alpha = 0.82f
    private val shakeThreshold = 13.8f
    private val maxGyroChaos = 12.5f
    private val minChopGapMs = 130L
    private val maxChopWindowMs = 650L
    private val cooldownMs = 900L
    private var chopCount = 0
    private var lastChopTime = 0L
    private var lastToggleTime = 0L

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        backCameraId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                if (cameraId == backCameraId) isTorchOn = enabled
            }
        }, Handler(Looper.getMainLooper()))
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        if (accel != null) sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)
        if (gyro != null) sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME)
        if (proximity != null) sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_PROXIMITY -> isPocketCovered = event.values[0] < event.sensor.maximumRange && event.values[0] < 3.0f
            Sensor.TYPE_GYROSCOPE -> {
                val wx = event.values[0]; val wy = event.values[1]; val wz = event.values[2]
                currentGyroMagnitude = sqrt(wx * wx + wy * wy + wz * wz)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                for (i in 0..2) {
                    gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i]
                    linearAcceleration[i] = event.values[i] - gravity[i]
                }
                val netForce = sqrt(linearAcceleration[0] * linearAcceleration[0] * 1.15f +
                    linearAcceleration[1] * linearAcceleration[1] * 1.15f +
                    linearAcceleration[2] * linearAcceleration[2] * 0.70f)
                evaluateDoubleChopMatrix(netForce)
            }
        }
    }

    private fun evaluateDoubleChopMatrix(netForce: Float) {
        if (isPocketCovered || currentGyroMagnitude > maxGyroChaos) return
        val now = System.currentTimeMillis()
        if (now - lastToggleTime < cooldownMs) return
        if (netForce >= shakeThreshold) {
            if (now - lastChopTime > maxChopWindowMs) {
                chopCount = 1; lastChopTime = now
            } else if (now - lastChopTime >= minChopGapMs) {
                chopCount++; lastChopTime = now
                if (chopCount >= 2) {
                    toggleHardwareFlashlight(); chopCount = 0; lastToggleTime = now
                }
            }
        }
    }

    private fun toggleHardwareFlashlight() {
        val camId = backCameraId ?: return
        try {
            val nextState = !isTorchOn
            cameraManager.setTorchMode(camId, nextState)
            isTorchOn = nextState
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
            else { @Suppress("DEPRECATION") vibrator.vibrate(70) }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun startForegroundServiceNotification() {
        val channelId = "lumishake_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "LumiShake Background Guard", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("LumiShake Pro সক্রিয় আছে")
            .setContentText("ফোন দুইবার ঝাঁকালেই ফ্ল্যাশলাইট অন/অফ হবে")
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging).setOngoing(true).build()
        startForeground(101, notification)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onDestroy() { sensorManager.unregisterListener(this); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
}