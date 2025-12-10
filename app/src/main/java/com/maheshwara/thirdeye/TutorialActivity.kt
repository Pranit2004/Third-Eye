package com.maheshwara.thirdeye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        findViewById<Button>(R.id.btnGotIt).setOnClickListener {
            // Save that user has seen the tutorial
            val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("isFirstRun", false).apply()

            // Go to Main App
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}