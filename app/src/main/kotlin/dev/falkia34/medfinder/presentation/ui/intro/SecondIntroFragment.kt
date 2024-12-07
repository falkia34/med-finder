package dev.falkia34.medfinder.presentation.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentSecondIntroBinding

class SecondIntroFragment : Fragment() {
    private var _binding: FragmentSecondIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondIntroBinding.inflate(inflater, container, false)

        binding.buttonNext.setOnClickListener {
            (parentFragment as IntroFragment).setCurrentItem(2)
        }

        binding.buttonPrevious.setOnClickListener {
            (parentFragment as IntroFragment).setCurrentItem(0)
        }

        Glide.with(this).load(R.drawable.second_intro).into(binding.imageFeatured)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}