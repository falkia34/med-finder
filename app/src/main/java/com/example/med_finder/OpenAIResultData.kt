package com.example.med_finder

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResultData(
    val is_plant: Boolean,
    val is_medicinal_plant: Boolean,
    val name: String = "",
    val latin_name: String = "",
    val disease: Array<String>,
    val description: String = ""
)
