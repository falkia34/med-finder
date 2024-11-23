package com.example.med_finder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.med_finder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Penyesuaian padding untuk WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delay selama 3 detik sebelum pindah ke halaman berikutnya
        Handler().postDelayed({
            val intent = Intent(this, IntroActivity::class.java) // Pindah ke IntroActivity
            startActivity(intent)
            finish() // Tutup loading screen agar tidak kembali saat tombol 'Back' ditekan
        }, 3000) // 3000 ms = 3 detik
    }
}
