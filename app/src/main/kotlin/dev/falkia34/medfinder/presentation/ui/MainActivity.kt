package dev.falkia34.medfinder.presentation.ui

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.ActivityMainBinding
import dev.falkia34.medfinder.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch

const val NAV_STATE_BUNDLE = "nav_state"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_nav_host) as NavHostFragment

        viewModel.getStartDestination()

        val content: View = findViewById(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (viewModel.ready) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })

        lifecycleScope.launch {
            viewModel.startDestination.flowWithLifecycle(lifecycle).collect { startDestination ->
                initNavigation(startDestination, savedInstanceState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val navState = navHostFragment.navController.saveState()

        outState.putBundle(NAV_STATE_BUNDLE, navState)
    }

    private fun initNavigation(startDestination: Int, savedInstanceState: Bundle?) {
        val navInflater = navHostFragment.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.navigation_mobile)
        val navState = savedInstanceState?.getBundle(NAV_STATE_BUNDLE)

        if (navState != null) {
            navHostFragment.navController.setGraph(navGraph, navState)
        } else {
            navGraph.setStartDestination(startDestination)
            navHostFragment.navController.graph = navGraph
        }
    }
}
