package dev.falkia34.medfinder.presentation.ui.intro

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentIntroBinding
import dev.falkia34.medfinder.presentation.viewmodels.intro.IntroViewModel
import dev.falkia34.medfinder.presentation.viewmodels.intro.LoginStatusState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class IntroFragment : Fragment() {
    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IntroViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.checkLoginStatus()

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        lifecycleScope.launch {
            viewModel.introState.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    0 -> {
                        binding.viewPager.currentItem = state
                        binding.buttonPrevious.visibility = View.GONE
                        binding.buttonNext.visibility = View.VISIBLE
                        binding.buttonGetStarted.visibility = View.GONE
                    }

                    1 -> {
                        binding.viewPager.currentItem = state
                        binding.buttonPrevious.visibility = View.VISIBLE
                        binding.buttonNext.visibility = View.VISIBLE
                        binding.buttonGetStarted.visibility = View.GONE
                    }

                    2 -> {
                        binding.viewPager.currentItem = state
                        binding.buttonPrevious.visibility = View.VISIBLE
                        binding.buttonNext.visibility = View.GONE
                        binding.buttonGetStarted.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars =
                insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())

            binding.viewPager.updatePadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.indicator.updatePadding(systemBars.left, 0, 0, systemBars.bottom)
            binding.linearButton.updatePadding(0, 0, systemBars.right, systemBars.bottom)
            insets
        }

        windowInsetsController =
            WindowCompat.getInsetsController(requireActivity().window, binding.root)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        binding.viewPager.adapter = IntroPagerAdapter(this)
        binding.indicator.setViewPager(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentItem(position)
            }
        })

        binding.buttonPrevious.setOnClickListener {
            viewModel.setCurrentItem(viewModel.introState.value - 1)
        }

        binding.buttonNext.setOnClickListener {
            viewModel.setCurrentItem(viewModel.introState.value + 1)
        }

        binding.buttonGetStarted.setOnClickListener {
            viewModel.finishOnboarding()

            when (viewModel.loginStatusState.value) {
                is LoginStatusState.LoggedIn -> navController.navigate(R.id.action_intro_to_home)
                is LoginStatusState.LoggedOut -> navController.navigate(R.id.action_intro_to_login)
                else -> {}
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
