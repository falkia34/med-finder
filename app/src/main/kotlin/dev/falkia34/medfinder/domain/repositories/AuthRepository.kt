package dev.falkia34.medfinder.domain.repositories

import android.content.Context
import arrow.core.Either
import com.google.firebase.auth.AuthResult
import dev.falkia34.medfinder.domain.entities.Failure

interface AuthRepository {
    suspend fun loginWithGoogle(context: Context): Either<Failure, AuthResult>

    suspend fun logout(): Either<Failure, Unit>

    suspend fun getUserUid(): Either<Failure, String>

    suspend fun getLoginStatus(): Either<Failure, Boolean>
}
