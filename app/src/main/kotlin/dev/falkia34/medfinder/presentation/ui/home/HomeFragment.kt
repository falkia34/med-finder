package dev.falkia34.medfinder.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentHomeBinding
import dev.falkia34.medfinder.presentation.viewmodels.home.HomeState
import dev.falkia34.medfinder.presentation.viewmodels.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels { defaultViewModelProviderFactory }
    private lateinit var adapter: PlantAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.plants.flowWithLifecycle(lifecycle).collectLatest { plants ->
                (binding.recyclerViewPlant.adapter as PlantAdapter).submitList(plants)
            }
        }

        lifecycleScope.launch {
            viewModel.homeState.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is HomeState.Loading -> binding.swipeRefresh.isRefreshing = true
                    is HomeState.Page -> binding.swipeRefresh.isRefreshing = false
                    is HomeState.LastPage -> binding.swipeRefresh.isRefreshing = false
                    is HomeState.Error -> {
                        binding.swipeRefresh.isRefreshing = false

                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PlantAdapter(onItemClick = { id ->
            val action = HomeFragmentDirections.actionHomeToDetails(id)

            navController.navigate(action)
        }, onItemDelete = { index ->
            viewModel.delete(index)
        })

        binding.recyclerViewPlant.layoutManager = layoutManager
        binding.recyclerViewPlant.adapter = adapter

        binding.recyclerViewPlant.addOnScrollListener(object : PlantScrollListener() {
            override fun onLoadMore() {
                viewModel.loadMore()
            }

            override fun isLastPage(): Boolean {
                return viewModel.homeState.value is HomeState.LastPage
            }

            override fun isLoading(): Boolean {
                return viewModel.homeState.value is HomeState.Loading
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    MaterialAlertDialogBuilder(
                        requireContext(),
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
                    ).setTitle(resources.getString(R.string.home_dialog_logout_title))
                        .setMessage(resources.getString(R.string.home_dialog_logout_description))
                        .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                            dialog.cancel()
                        }.setNegativeButton(resources.getString(R.string.yes)) { dialog, _ ->
                            dialog.dismiss()
                        }.setPositiveButton(resources.getString(R.string.no)) { dialog, _ ->
                            viewModel.logout()
                            viewModel.clear()
                            dialog.dismiss()
                            navController.navigate(R.id.action_home_to_login)
                        }.show()

                    true
                }

                else -> false
            }
        }

        binding.buttonScan.setOnClickListener {
            navController.navigate(R.id.action_home_to_camera)
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
