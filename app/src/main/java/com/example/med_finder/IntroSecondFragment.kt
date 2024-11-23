package com.example.med_finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.med_finder.databinding.FragmentIntroSecondBinding

class IntroSecondFragment : Fragment() {

    private lateinit var binding: FragmentIntroSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroSecondBinding.inflate(inflater, container, false)

        // Set up padding untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tombol Learn More untuk pindah ke halaman ketiga
        binding.GetOurServicesButton.setOnClickListener {
            (activity as IntroActivity).binding.viewPager.currentItem = 2 // Pindah ke halaman ketiga
        }

        return binding.root
    }
}
