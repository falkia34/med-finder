package dev.falkia34.medfinder.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentLoginBinding
import dev.falkia34.medfinder.presentation.viewmodels.auth.LoginState
import dev.falkia34.medfinder.presentation.viewmodels.auth.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.loginState.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is LoginState.Loading -> binding.buttonSignInGoogle.isEnabled = false
                    is LoginState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        binding.buttonSignInGoogle.isEnabled = true
                    }

                    is LoginState.Success -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_login_to_home)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.buttonSignInGoogle.setOnClickListener {
            viewModel.loginWithGoogle(requireContext())
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
    }
}
