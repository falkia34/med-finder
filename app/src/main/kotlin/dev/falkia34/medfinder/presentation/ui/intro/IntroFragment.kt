package dev.falkia34.medfinder.presentation.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentIntroBinding
import dev.falkia34.medfinder.presentation.viewmodels.intro.IntroViewModel
import dev.falkia34.medfinder.presentation.viewmodels.intro.LoginStatusState

@AndroidEntryPoint
class IntroFragment : Fragment() {
    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IntroViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.checkLoginStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.updatePadding(
                systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
            )
            insets
        }

        val pagerAdapter = IntroPagerAdapter(this)

        binding.viewPager.adapter = pagerAdapter
        binding.indicator.setViewPager(binding.viewPager)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setCurrentItem(index: Int) {
        binding.viewPager.currentItem = index
    }

    fun finishOnboarding() {
        viewModel.finishOnboarding()

        when (viewModel.loginStatusState.value) {
            is LoginStatusState.LoggedIn -> navController.navigate(R.id.action_intro_to_home)
            is LoginStatusState.LoggedOut -> navController.navigate(R.id.action_intro_to_login)
            else -> {}
        }
    }

    private inner class IntroPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        private val fragments = listOf(
            FirstIntroFragment(),
            SecondIntroFragment(),
            ThirdIntroFragment(),
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
