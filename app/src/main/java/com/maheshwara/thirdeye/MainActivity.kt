package com.maheshwara.thirdeye

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton // Import ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.FOREGROUND_SERVICE
    )
    private val PERMISSION_CODE = 100

    private var isAudioRecording = false
    private var isVideoRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Audio Button Logic
        val btnAudio: Button = findViewById(R.id.btnToggleRecord)
        val tvStatus: TextView = findViewById(R.id.tvStatus)

        btnAudio.setOnClickListener {
            if (checkPermissions()) {
                if (!isAudioRecording) {
                    startSafetyService()
                    btnAudio.text = "STOP AUDIO"
                    btnAudio.setBackgroundColor(0xFF555555.toInt())
                    tvStatus.text = "Status: Audio Recording..."
                    isAudioRecording = true
                } else {
                    stopSafetyService()
                    btnAudio.text = "Start Audio Mode"
                    btnAudio.setBackgroundColor(0xFFD32F2F.toInt())
                    tvStatus.text = "Status: Stopped"
                    isAudioRecording = false
                }
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_CODE)
            }
        }

        // 2. Video Button Logic
        val btnVideo: Button = findViewById(R.id.btnVideoRecord)
        btnVideo.setOnClickListener {
            if (checkPermissions()) {
                if (!isVideoRecording) {
                    startVideoService()
                    btnVideo.text = "STOP VIDEO"
                    isVideoRecording = true
                } else {
                    stopVideoService()
                    btnVideo.text = "Start Video Mode"
                    isVideoRecording = false
                }
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_CODE)
            }
        }

        // 3. Gallery Button Logic (MOVED OUTSIDE)
        val btnGallery: Button = findViewById(R.id.btnGallery)
        btnGallery.setOnClickListener {
            val intent = Intent(this, RecordingsActivity::class.java)
            startActivity(intent)
        }

        // 4. Settings Button Logic (MOVED OUTSIDE)
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    // --- SERVICE FUNCTIONS ---
    private fun startSafetyService() {
        val intent = Intent(this, AudioRecorderService::class.java)
        intent.action = "START"
        ContextCompat.startForegroundService(this, intent)
        Toast.makeText(this, "Audio Started", Toast.LENGTH_SHORT).show()
    }

    private fun stopSafetyService() {
        val intent = Intent(this, AudioRecorderService::class.java)
        intent.action = "STOP"
        ContextCompat.startForegroundService(this, intent)
        Toast.makeText(this, "Audio Saved", Toast.LENGTH_SHORT).show()
    }

    private fun startVideoService() {
        if (!android.provider.Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please allow 'Display over other apps'", Toast.LENGTH_LONG).show()
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
            return
        }
        val intent = Intent(this, VideoRecorderService::class.java)
        intent.action = "START"
        ContextCompat.startForegroundService(this, intent)
        Toast.makeText(this, "Video Background Started", Toast.LENGTH_SHORT).show()
    }

    private fun stopVideoService() {
        val intent = Intent(this, VideoRecorderService::class.java)
        intent.action = "STOP"
        ContextCompat.startForegroundService(this, intent)
        Toast.makeText(this, "Video Saved", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}