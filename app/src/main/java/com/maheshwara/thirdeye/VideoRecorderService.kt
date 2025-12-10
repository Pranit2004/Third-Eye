package com.maheshwara.thirdeye

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import java.io.File
import java.util.Date

class VideoRecorderService : Service(), SurfaceHolder.Callback {

    private var windowManager: WindowManager? = null
    private var surfaceView: SurfaceView? = null
    private var camera: Camera? = null
    private var mediaRecorder: MediaRecorder? = null

    override fun onBind(intent: Intent?): IBinder? { return null }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START") {
            startForegroundService()
            setupSurface()
        } else if (intent?.action == "STOP") {
            stopRecording()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    // 1. Create the invisible window (1x1 pixel)
    private fun setupSurface() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        surfaceView = SurfaceView(this)

        // This makes the window "Always on top" but tiny
        val params = WindowManager.LayoutParams(
            1, 1, // Size: 1 pixel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START

        surfaceView?.holder?.addCallback(this)
        windowManager?.addView(surfaceView, params)
    }

    // 2. When surface is ready, start camera
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            camera = Camera.open()
            camera?.unlock()

            mediaRecorder = MediaRecorder()
            mediaRecorder?.setCamera(camera)
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
            // --- NEW QUALITY LOGIC ---
            // 1. Get the saved preference
            val prefs = getSharedPreferences("ThirdEyePrefs", Context.MODE_PRIVATE)

            // 2. Default is 1080P if user never chose anything
            var qualityIndex = prefs.getInt("video_quality", android.media.CamcorderProfile.QUALITY_1080P)

            // 3. Safety Check: If phone doesn't support 1080p, drop to High
            if (!android.media.CamcorderProfile.hasProfile(qualityIndex)) {
                qualityIndex = android.media.CamcorderProfile.QUALITY_HIGH
            }

            // 4. Set the profile
            mediaRecorder?.setProfile(android.media.CamcorderProfile.get(qualityIndex))
            // -------------------------

            val fileName = File(getExternalFilesDir(null), "Video_${Date().time}.mp4").absolutePath
            mediaRecorder?.setOutputFile(fileName)
            mediaRecorder?.setPreviewDisplay(holder.surface)

            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            camera?.lock()
            camera?.release()
            windowManager?.removeView(surfaceView)
        } catch (e: Exception) {}
    }

    private fun startForegroundService() {
        val channelId = "VideoChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Video Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Third Eye Video")
            .setContentText("Recording video...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
        startForeground(2, notification)
    }
}