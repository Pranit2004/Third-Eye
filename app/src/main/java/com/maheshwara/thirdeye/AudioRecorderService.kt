package com.maheshwara.thirdeye

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecorderService : Service() {

    private var recorder: MediaRecorder? = null
    private var fileName: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null // We don't need binding for this simple app
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == "START") {
            startForegroundService()
            startRecording()
        } else if (action == "STOP") {
            stopRecording()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        // 1. Create Notification Channel (Required for Android 8+)
        val channelId = "SafetyChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Safety Recording Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // 2. Build the Notification
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Third Eye Active")
            .setContentText("Recording audio evidence...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now) // Default Android icon
            .setOngoing(true) // Prevents user from accidentally swiping it away
            .build()

        // 3. Start Foreground
        startForeground(1, notification)
    }

    private fun startRecording() {
        // Generate a unique file name based on time
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        // Save to internal app storage (Private and safe)
        val file = File(getExternalFilesDir(null), "Evidence_$timeStamp.3gp")
        fileName = file.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
                Log.d("ThirdEye", "Recording started: $fileName")
            } catch (e: IOException) {
                Log.e("ThirdEye", "Recorder failed: ${e.message}")
            }
        }
    }

    private fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            Log.d("ThirdEye", "Recording saved to: $fileName")
        } catch (e: Exception) {
            // Handle crash if stopped immediately after start
            recorder = null
        }
    }
}