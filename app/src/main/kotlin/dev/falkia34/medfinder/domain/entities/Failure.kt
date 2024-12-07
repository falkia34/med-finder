package dev.falkia34.medfinder.domain.entities

sealed class Failure {
    abstract val message: String

    data class Unknown(override val message: String = "Unknown error occurred!") : Failure()
    data class NoContent(override val message: String = "No content response from OpenAI!") :
        Failure()
}
