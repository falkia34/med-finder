package com.example.med_finder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        // Pastikan layout menyesuaikan dengan sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tombol Sign In
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            // Pindah ke halaman Home tanpa autentikasi
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Menutup SignInActivity
        }

        // Google Sign-In Button
        val googleSignInButton = findViewById<ImageView>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            // Logika untuk Google Sign-In (opsional untuk pengembangan ke depan)
        }

        // Navigasi ke halaman Sign Up
        val signUpText = findViewById<TextView>(R.id.signUpNow)
        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
