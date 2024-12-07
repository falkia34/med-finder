package com.example.med_finder

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.med_finder.databinding.FragmentIntroFirstBinding

class IntroFirstFragment : Fragment() {

    private lateinit var binding: FragmentIntroFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroFirstBinding.inflate(inflater, container, false)

        // Pastikan layout menyesuaikan sistem bar (navigation/gesture bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.updatePadding(
                bottom = systemBarsInsets.bottom // Tambahkan padding bawah untuk menghindari white space
            )
            insets
        }

        // Muat gambar menggunakan Glide
        Glide.with(this)
            .load(R.drawable.intro1) // URL gambar
            .into(binding.imageView) // Ganti `imageView` dengan ID ImageView Anda di layout

        // Tombol Learn More untuk pindah ke halaman kedua
        binding.learnMoreButton.setOnClickListener {
            (activity as IntroActivity).binding.viewPager.currentItem = 1 // Pindah ke halaman kedua
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sembunyikan navigation bar dan status bar untuk fullscreen
        requireActivity().window.insetsController?.apply {
            hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
        }
    }
}
