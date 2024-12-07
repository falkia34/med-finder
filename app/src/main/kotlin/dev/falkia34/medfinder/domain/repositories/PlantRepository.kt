package dev.falkia34.medfinder.domain.repositories

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.entities.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun countPlants(): Either<Failure, Long>

    suspend fun getPlants(startCursor: String?, limit: Long): Either<Failure, List<Plant>>

    suspend fun getPlant(id: String): Either<Failure, Plant>

    suspend fun addPlant(plant: Plant): Either<Failure, Plant>

    suspend fun deletePlant(id: String): Either<Failure, Plant>
}
