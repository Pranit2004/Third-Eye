package com.maheshwara.thirdeye

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        // Button to Accept Terms -> Go to Tutorial
        findViewById<Button>(R.id.btnAccept).setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish() // Close Terms screen so back button doesn't return here
        }

        // Button to Decline Terms -> Close App
        findViewById<Button>(R.id.btnDecline).setOnClickListener {
            finishAffinity() // Closes the entire app
        }
    }
}