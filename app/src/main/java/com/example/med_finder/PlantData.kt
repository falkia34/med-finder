package com.example.med_finder

data class PlantData(
    val name: String,
    val latinName: String,
    val disease: Array<String>,
    val description: String,
    val image: String,
)