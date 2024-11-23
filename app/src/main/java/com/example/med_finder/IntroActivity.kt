package com.example.med_finder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.med_finder.databinding.ActivityIntroBinding
import me.relex.circleindicator.CircleIndicator3
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IntroActivity : AppCompatActivity() {

    lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager2
        val adapter = IntroPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Setup CircleIndicator untuk indikator titik
        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(binding.viewPager)

        // Penyesuaian padding untuk WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
