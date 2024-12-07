package com.example.med_finder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.med_finder.databinding.ActivityHomeBinding
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val plantList = mutableListOf<PlantData>()
    private lateinit var adapter: PlantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup Camera Button
        setupCameraButton()

        // Setup Logout Button
        setupLogoutButton()

        // Setup Search Section
        setupSearchSection()

        // Handle new plant data from CameraActivity
        handleNewPlantData()
    }

    private fun setupRecyclerView() {
        adapter = PlantAdapter(plantList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

//        // Example data
//        plantList.add(
//            PlantData(
//                name = "Aloe Vera",
//                description = "Used for skin treatment",
//                imageUri = null // Drawable resource fallback
//            )
//        )
//
//        plantList.add(
//            PlantData(
//                name = "Tulsi",
//                description = "Boosts immunity",
//                imageUri = null
//            )
//        )

        adapter.notifyDataSetChanged()
    }

    private fun setupCameraButton() {
        val cameraButton: ImageButton = binding.cameraButton
        cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLogoutButton() {
        val logoutButton: ImageButton = binding.logoutButton
        logoutButton.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            // Navigate back to Login
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupSearchSection() {
        val searchIcon: ImageView = binding.searchIcon
        searchIcon.setOnClickListener {
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                searchPlant(query)
            } else {
                Toast.makeText(this, "Please enter a plant name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchPlant(query: String) {
        val filteredList = plantList.filter { it.name.contains(query, ignoreCase = true) }
        if (filteredList.isNotEmpty()) {
            adapter.updateList(filteredList)
        } else {
            Toast.makeText(this, "No results found for \"$query\"", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNewPlantData() {
        val image = intent.getStringExtra("PHOTO_DATA")

        if (!image.isNullOrEmpty()) {
            android.util.Log.d("HomeActivity", "Received Photo Data") // Debugging URI

            val openAIRepository = OpenAIRepository()

            val data = runBlocking {
                openAIRepository.generateRespons("data:image/png;base64, $image")
            }

            val parsedData = Json.decodeFromString<OpenAIResultData>(data!!)

            if (parsedData.is_plant && parsedData.is_medicinal_plant) {
                val newPlant = PlantData(
                    name = parsedData.name,
                    latinName = parsedData.latin_name,
                    disease = parsedData.disease,
                    description = parsedData.description,
                    image = image
                )
                plantList.add(newPlant)
                adapter.notifyItemInserted(plantList.size - 1)
            }
        }
    }
}