package dev.falkia34.medfinder.application.plant

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.entities.Plant
import dev.falkia34.medfinder.domain.repositories.PlantRepository
import javax.inject.Inject

class AddPlantUseCase @Inject constructor(
    private val plantRepository: PlantRepository,
) {
    suspend operator fun invoke(plant: Plant): Either<Failure, Plant> =
        plantRepository.addPlant(plant)
}
