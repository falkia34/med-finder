package com.example.med_finder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.med_finder.databinding.FragmentIntroThirdBinding

class IntroThirdFragment : Fragment() {

    private lateinit var binding: FragmentIntroThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroThirdBinding.inflate(inflater, container, false)

        // Set up padding untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Muat gambar menggunakan Glide
        Glide.with(this)
            .load(R.drawable.intro3) // URL gambar
            .into(binding.imageView) // Ganti dengan ID ImageView di layout Anda

        // Tombol Start Today untuk pindah ke halaman Sign In
        binding.StartTodayButton.setOnClickListener {
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}
