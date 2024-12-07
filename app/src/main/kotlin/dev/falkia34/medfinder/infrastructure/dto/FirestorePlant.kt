package dev.falkia34.medfinder.infrastructure.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class FirestorePlant(
    @JvmField @DocumentId val id: String = "",
    @JvmField @PropertyName("user_id") val userId: String = "",
    @JvmField val image: String = "",
    @JvmField val name: String = "",
    @JvmField @PropertyName("latin_name") val latinName: String = "",
    @JvmField val description: String = "",
    @JvmField val disease: List<String> = emptyList(),
)
