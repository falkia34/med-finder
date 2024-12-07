package dev.falkia34.medfinder.infrastructure.repositories

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import arrow.core.Either
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.di.IODispatcher
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.repositories.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuthRepository {
    override suspend fun loginWithGoogle(context: Context): Either<Failure, AuthResult> {
        return withContext(dispatcher) {
            try {
                val googleIdOption = GetGoogleIdOption.Builder().setAutoSelectEnabled(false)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.google_web_client_id)).build()
                val signInRequest =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val signInResponse = credentialManager.getCredential(context, signInRequest)
                val credential = signInResponse.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val googleAuthCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(googleAuthCredential).await()

                    return@withContext Either.Right(authResult)
                } else {
                    return@withContext Either.Left(Failure.Unknown())
                }
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun logout(): Either<Failure, Unit> {
        return withContext(dispatcher) {
            try {
                firebaseAuth.signOut()

                return@withContext Either.Right(Unit)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun getUserUid(): Either<Failure, String> {
        return withContext(dispatcher) {
            try {
                return@withContext Either.Right(firebaseAuth.currentUser?.uid ?: "")
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun getLoginStatus(): Either<Failure, Boolean> {
        return withContext(dispatcher) {
            try {
                return@withContext Either.Right(firebaseAuth.currentUser != null)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }
}