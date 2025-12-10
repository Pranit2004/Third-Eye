package com.maheshwara.thirdeye

import android.content.Context
import android.media.CamcorderProfile
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val radioGroup: RadioGroup = findViewById(R.id.radioGroupQuality)
        val btnSave: Button = findViewById(R.id.btnSaveSettings)

        // 1. Load the saved setting (Default to 1080p if nothing saved)
        val prefs = getSharedPreferences("ThirdEyePrefs", Context.MODE_PRIVATE)
        val currentQuality = prefs.getInt("video_quality", CamcorderProfile.QUALITY_1080P)

        // 2. Check the correct box based on saved value
        when (currentQuality) {
            CamcorderProfile.QUALITY_HIGH -> findViewById<RadioButton>(R.id.rbHigh).isChecked = true
            CamcorderProfile.QUALITY_1080P -> findViewById<RadioButton>(R.id.rb1080).isChecked = true
            CamcorderProfile.QUALITY_720P -> findViewById<RadioButton>(R.id.rb720).isChecked = true
            CamcorderProfile.QUALITY_480P -> findViewById<RadioButton>(R.id.rb480).isChecked = true
            CamcorderProfile.QUALITY_LOW -> findViewById<RadioButton>(R.id.rbLow).isChecked = true
            else -> findViewById<RadioButton>(R.id.rb1080).isChecked = true // Fallback
        }

        // 3. Save Logic
        btnSave.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val editor = prefs.edit()

            val qualityToSave = when (selectedId) {
                R.id.rbHigh -> CamcorderProfile.QUALITY_HIGH
                R.id.rb1080 -> CamcorderProfile.QUALITY_1080P
                R.id.rb720 -> CamcorderProfile.QUALITY_720P
                R.id.rb480 -> CamcorderProfile.QUALITY_480P
                R.id.rbLow -> CamcorderProfile.QUALITY_LOW
                else -> CamcorderProfile.QUALITY_1080P
            }

            editor.putInt("video_quality", qualityToSave)
            editor.apply() // Commit changes

            Toast.makeText(this, "Quality Updated!", Toast.LENGTH_SHORT).show()
            finish() // Close settings screen
        }
    }
}