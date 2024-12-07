package com.example.med_finder

data class PlantData(
    val name: String,
    val description: String,
    val imageRes: Int = 0, // Untuk drawable resource
    val imageUri: String? = null // Untuk URI, default null
)