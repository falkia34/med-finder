package dev.falkia34.medfinder.domain.repositories

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.entities.Plant

interface OpenAIRepository {
    suspend fun getImageDetails(image: String): Either<Failure, Plant>
}
