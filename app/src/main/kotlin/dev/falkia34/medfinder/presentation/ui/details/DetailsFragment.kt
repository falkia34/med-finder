package dev.falkia34.medfinder.presentation.ui.details

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentDetailsBinding
import dev.falkia34.medfinder.presentation.viewmodels.details.DetailsState
import dev.falkia34.medfinder.presentation.viewmodels.details.DetailsViewModel
import dev.falkia34.medfinder.presentation.viewmodels.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var deleteDialog: AlertDialog
    private lateinit var navController: NavController
    private val homeViewModel: HomeViewModel by activityViewModels { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.load(args.id)

        lifecycleScope.launch {
            viewModel.detailsState.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is DetailsState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is DetailsState.Success -> {
                        val imageBytes = Base64.decode(state.plant.image, Base64.DEFAULT)
                        val decodedImage =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        Glide.with(binding.root).load(decodedImage).into(binding.imageFeatured)

                        binding.imageFeatured.contentDescription = state.plant.name
                        binding.textName.text = state.plant.name
                        binding.textName.visibility = View.VISIBLE
                        binding.textLatinName.text = state.plant.latinName
                        binding.textLatinName.visibility = View.VISIBLE
                        binding.textDescription.text = state.plant.description

                        binding.chipGroupDisease.removeAllViews()
                        state.plant.disease.map { disease ->
                            val chip = Chip(requireContext())
                            chip.text = disease.split(" ").joinToString(" ") { word ->
                                word.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }
                            }

                            binding.chipGroupDisease.addView(chip)
                        }
                    }

                    is DetailsState.Deleted -> {
                        deleteDialog.dismiss()
                        homeViewModel.refresh()
                        navController.popBackStack()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.materialToolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    deleteDialog.show()
                    true
                }

                else -> false
            }
        }

        deleteDialog = MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).setTitle(resources.getString(R.string.details_dialog_delete_title))
            .setMessage(resources.getString(R.string.details_dialog_delete_description))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }.setNegativeButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton(resources.getString(R.string.no)) { dialog, _ ->
                viewModel.delete(args.id)
            }.create()

        binding.nameDetails.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.nameDetails.getViewTreeObserver().removeOnGlobalLayoutListener(this)

                    val element: View = binding.nameDetails.getChildAt(1)

                    BottomSheetBehavior.from(binding.bottomSheetDetails).peekHeight = element.bottom

                    binding.imageFeatured.setPaddingRelative(0, 0, 0, element.bottom)
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
    }
}
