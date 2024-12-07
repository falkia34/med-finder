package dev.falkia34.medfinder.domain.entities

data class Plant(
    val id: String,
    val image: String,
    val name: String,
    val latinName: String,
    val description: String,
    val disease: List<String>,
)
