package com.bangkit.ripad.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.ripad.MainActivity
import com.bangkit.ripad.R
import com.bangkit.ripad.ui.onboarding.OnBoardingActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        // Menunggu beberapa detik sebelum pindah ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigasi ke MainActivity
            if (user != null) {
                startActivity(Intent(this, MainActivity ::class.java))
                finish()
            } else {
                startActivity(Intent(this, OnBoardingActivity ::class.java))
                finish()
            }
        }, 2000) // Delay 2000 ms = 2 detik

    }
}