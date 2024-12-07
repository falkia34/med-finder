package dev.falkia34.medfinder.infrastructure.repositories

import android.util.Log
import arrow.core.Either
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dev.falkia34.medfinder.di.IODispatcher
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.entities.Plant
import dev.falkia34.medfinder.domain.repositories.PlantRepository
import dev.falkia34.medfinder.infrastructure.dto.FirestorePlant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val PLANT_COLLECTION = "plants"

class PlantRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PlantRepository {
    private val plantRef get() = firestore.collection(PLANT_COLLECTION)

    override suspend fun countPlants(): Either<Failure, Long> {
        return withContext(dispatcher) {
            try {
                val count =
                    plantRef.whereEqualTo("user_id", firebaseAuth.currentUser?.uid!!).count().get(
                        AggregateSource.SERVER
                    ).await().count

                return@withContext Either.Right(count)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun getPlants(
        startCursor: String?,
        limit: Long,
    ): Either<Failure, List<Plant>> {
        return withContext(dispatcher) {
            try {
                var query =
                    plantRef.whereEqualTo("user_id", firebaseAuth.currentUser?.uid!!).limit(limit)
                        .orderBy(FieldPath.documentId())

                if (startCursor != null) {
                    query = query.startAfter(startCursor)
                }

                val plants = query.get().await().map { plantSnapshot ->
                    val firestorePlant = plantSnapshot.toObject<FirestorePlant>()

                    Plant(
                        firestorePlant.id,
                        firestorePlant.image,
                        firestorePlant.name,
                        firestorePlant.latinName,
                        firestorePlant.description,
                        firestorePlant.disease
                    )
                }

                return@withContext Either.Right(plants)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun getPlant(id: String): Either<Failure, Plant> {
        return withContext(dispatcher) {
            try {
                val firestorePlant =
                    plantRef.document(id).get().await().toObject<FirestorePlant>()!!
                val plant = Plant(
                    firestorePlant.id,
                    firestorePlant.image,
                    firestorePlant.name,
                    firestorePlant.latinName,
                    firestorePlant.description,
                    firestorePlant.disease
                )

                return@withContext Either.Right(plant)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun addPlant(plant: Plant): Either<Failure, Plant> {
        return withContext(dispatcher) {
            try {
                val firestorePlant = hashMapOf(
                    "user_id" to firebaseAuth.currentUser?.uid!!,
                    "image" to plant.image,
                    "name" to plant.name,
                    "latin_name" to plant.latinName,
                    "description" to plant.description,
                    "disease" to plant.disease
                )

                plantRef.document(plant.id).set(firestorePlant).await()

                return@withContext Either.Right(plant)
            } catch (e: Exception) {
                Log.d("PlantRepository", e.toString())
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun deletePlant(id: String): Either<Failure, Plant> {
        return withContext(dispatcher) {
            try {
                val firestorePlant =
                    plantRef.document(id).get().await().toObject<FirestorePlant>()!!
                val plant = Plant(
                    firestorePlant.id,
                    firestorePlant.image,
                    firestorePlant.name,
                    firestorePlant.latinName,
                    firestorePlant.description,
                    firestorePlant.disease
                )

                plantRef.document(id).delete().await()

                return@withContext Either.Right(plant)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }
}
