package dev.falkia34.medfinder.infrastructure.repositories

import android.content.Context
import arrow.core.Either
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.chat.TextPart
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.di.IODispatcher
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.entities.Plant
import dev.falkia34.medfinder.domain.repositories.OpenAIRepository
import dev.falkia34.medfinder.infrastructure.dto.OpenAIPlant
import dev.falkia34.medfinder.utils.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi

class OpenAIRepositoryImpl @Inject constructor(
    private val openAI: OpenAI,
    @ApplicationContext private val context: Context,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OpenAIRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getImageDetails(image: String): Either<Failure, Plant> {
        return withContext(dispatcher) {
            try {
                val completion: ChatCompletion = openAI.chatCompletion(
                    ChatCompletionRequest(
                        model = ModelId("gpt-4o"),
                        temperature = 1.0,
                        maxTokens = 2048,
                        topP = 1.0,
                        frequencyPenalty = 0.0,
                        presencePenalty = 0.0,
                        messages = listOf(
                            ChatMessage.User(
                                content = listOf(
                                    ImagePart(
                                        url = context.getString(R.string.prompt_image, image)
                                    ),
                                    TextPart(
                                        text = context.getString(R.string.prompt_image_details)
                                    ),
                                )
                            )
                        ),
                        responseFormat = ChatResponseFormat.JsonObject,
                    )
                )
                val content = completion.choices[0].message.content

                if (content != null) {
                    val decodedContent = Json.decodeFromString<OpenAIPlant>(content)

                    if (decodedContent.is_plant && decodedContent.is_medicinal_plant) {
                        val result = Plant(
                            Uuid.v7().toString(),
                            image,
                            decodedContent.name,
                            decodedContent.latin_name,
                            decodedContent.description,
                            decodedContent.disease,
                        )

                        return@withContext Either.Right(result)
                    } else {
                        return@withContext Either.Left(Failure.NoContent())
                    }
                } else {
                    return@withContext Either.Left(Failure.NoContent())
                }
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }
}