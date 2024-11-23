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
            // Pindah ke halaman utama atau dashboard setelah berhasil sign in
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Google Sign-In Button
        val googleSignInButton = findViewById<ImageView>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            // Tambahkan logika untuk Google Sign-In di sini
            // Misalnya, gunakan Firebase Auth atau API lainnya
        }

        val signUpText = findViewById<TextView>(R.id.signUpNow)
        signUpText.setOnClickListener {
            // Pindah ke halaman sign up
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
