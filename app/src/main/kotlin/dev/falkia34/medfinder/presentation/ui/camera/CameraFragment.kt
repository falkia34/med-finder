package dev.falkia34.medfinder.presentation.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Base64
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.databinding.FragmentCameraBinding
import dev.falkia34.medfinder.presentation.viewmodels.camera.CameraViewModel
import dev.falkia34.medfinder.presentation.viewmodels.camera.ImageDetailsState
import dev.falkia34.medfinder.presentation.viewmodels.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

val CAMERA_REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private var resolutionSelector: ResolutionSelector? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraExecutor: ExecutorService? = null
    private lateinit var navController: NavController
    private lateinit var windowInsetsController: WindowInsetsControllerCompat
    private val binding get() = _binding!!
    private val viewModel: CameraViewModel by viewModels()
    private val homeViewModel: HomeViewModel by hiltNavGraphViewModels(R.id.navigation_mobile_home)

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        lifecycleScope.launch {
            viewModel.imageDetailsState.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is ImageDetailsState.Loading -> {
                        cameraProvider?.unbind(preview)

                        binding.progressIndicator.visibility = View.VISIBLE
                        binding.buttonCapture.isEnabled = false
                    }

                    is ImageDetailsState.Success -> {
                        binding.progressIndicator.visibility = View.GONE

                        homeViewModel.add(listOf(state.plant))

                        val action = CameraFragmentDirections.actionCameraToDetails(state.plant.id)

                        navController.navigate(action)
                    }

                    is ImageDetailsState.Error -> {
                        binding.progressIndicator.visibility = View.GONE

                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()

                        cameraProvider?.unbindAll()
                        cameraProvider?.bindToLifecycle(
                            viewLifecycleOwner, cameraSelector!!, preview, imageCapture,
                        )

                        binding.buttonCapture.isEnabled = true
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars =
                insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            val displayCutout =
                insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.displayCutout())

            val appBarLayoutParams = (binding.appBar.layoutParams as ViewGroup.MarginLayoutParams)
            val buttonCaptureLayoutParams =
                (binding.buttonCapture.layoutParams as ViewGroup.MarginLayoutParams)

            appBarLayoutParams.setMargins(
                systemBars.left + displayCutout.left,
                systemBars.top,
                systemBars.right + displayCutout.right,
                0
            )
            buttonCaptureLayoutParams.setMargins(
                systemBars.left + displayCutout.left,
                0,
                systemBars.right + displayCutout.right,
                systemBars.bottom + (16 * Resources.getSystem().displayMetrics.density).roundToInt()
            )

            binding.appBar.layoutParams = appBarLayoutParams
            binding.buttonCapture.layoutParams = buttonCaptureLayoutParams

            insets
        }

        windowInsetsController =
            WindowCompat.getInsetsController(requireActivity().window, binding.root)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        binding.materialToolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.buttonCapture.setOnClickListener {
            takePhoto()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        if (checkSelfPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(CAMERA_REQUIRED_PERMISSION)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor?.shutdown()
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        _binding = null
    }

    private fun checkSelfPermissionsGranted() = CAMERA_REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when (permission.entries.all { it.value }) {
            true -> startCamera()
            false -> Toast.makeText(
                requireContext(), "Permissions not granted.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()
            // Select back camera
            cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
                ResolutionStrategy(
                    Size(480, 640), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            ).build()
            preview = Preview.Builder().setResolutionSelector(resolutionSelector!!).build().also {
                it.surfaceProvider = binding.cameraPreview.surfaceProvider
            }
            imageCapture =
                ImageCapture.Builder().setResolutionSelector(resolutionSelector!!).build()

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, cameraSelector!!, preview, imageCapture)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to start camera.", Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                @OptIn(ExperimentalGetImage::class)
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.image?.planes?.get(0)?.buffer

                    buffer?.rewind()

                    val bytes = buffer?.let { ByteArray(it.capacity()) }

                    if (bytes != null) {
                        buffer.get(bytes)
                    }

                    val encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT)

                    viewModel.scanImage(encodedImage)
                }

                override fun onError(e: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(), "Capture failed!", Toast.LENGTH_SHORT
                    ).show()
                }
            },
        )
    }
}
