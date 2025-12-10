package com.maheshwara.thirdeye

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if this is the first run
            val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val isFirstRun = prefs.getBoolean("isFirstRun", true)

            if (isFirstRun) {
                // If first time, go to Terms
                startActivity(Intent(this, TermsActivity::class.java))
            } else {
                // If returned user, go to Main Dashboard
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 3000)
    }
}