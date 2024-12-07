package dev.falkia34.medfinder.infrastructure.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIPlant(
    @SerialName("is_plant") val isPlant: Boolean,
    @SerialName("is_medicinal_plant") val isMedicinalPlant: Boolean,
    val name: String = "",
    @SerialName("latin_name") val latinName: String = "",
    val disease: List<String> = emptyList(),
    val description: String = "",
)
