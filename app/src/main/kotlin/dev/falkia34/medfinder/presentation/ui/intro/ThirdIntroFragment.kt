package dev.falkia34.medfinder.presentation.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentThirdIntroBinding

class ThirdIntroFragment : Fragment() {
    private var _binding: FragmentThirdIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentThirdIntroBinding.inflate(inflater, container, false)

        Glide.with(this).load(R.drawable.third_intro).into(binding.imageFeatured)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
