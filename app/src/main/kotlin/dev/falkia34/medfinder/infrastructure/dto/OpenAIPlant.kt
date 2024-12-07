package dev.falkia34.medfinder.infrastructure.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIPlant(
    val is_plant: Boolean,
    val is_medicinal_plant: Boolean,
    val name: String = "",
    val latin_name: String = "",
    val disease: List<String>,
    val description: String = "",
)
