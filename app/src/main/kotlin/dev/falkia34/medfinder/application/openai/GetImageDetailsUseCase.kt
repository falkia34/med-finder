package dev.falkia34.medfinder.application.openai

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.entities.Plant
import dev.falkia34.medfinder.domain.repositories.OpenAIRepository
import javax.inject.Inject

class GetImageDetailsUseCase @Inject constructor(
    private val openAIRepository: OpenAIRepository
) {
    suspend operator fun invoke(image: String): Either<Failure, Plant> =
        openAIRepository.getImageDetails(image)
}
