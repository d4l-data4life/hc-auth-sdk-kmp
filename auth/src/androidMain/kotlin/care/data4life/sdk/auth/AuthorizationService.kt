/*
 * Copyright (c) 2021 D4L data4life gGmbH / All rights reserved.
 *
 * D4L owns all legal rights, title and interest in and to the Software Development Kit ("SDK"),
 * including any intellectual property rights that subsist in the SDK.
 *
 * The SDK and its documentation may be accessed and used for viewing/review purposes only.
 * Any usage of the SDK for other purposes, including usage for the development of
 * applications/third-party applications shall require the conclusion of a license agreement
 * between you and D4L.
 *
 * If you are interested in licensing the SDK for your own applications/third-party
 * applications and/or if you’d like to contribute to the development of the SDK, please
 * contact D4L by email to help@data4life.care.
 */

package care.data4life.sdk.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import care.data4life.sdk.auth.storage.InMemoryAuthStorage
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.json.JSONException

actual class AuthorizationService internal constructor(
    private val appAuthService: AppAuthService,
    private val configuration: AuthorizationConfiguration,
    private val storage: AuthorizationContract.Storage
) : AuthorizationContract.Service {

    @JvmOverloads
    constructor(
        context: Context,
        configuration: AuthorizationConfiguration,
        storage: AuthorizationContract.Storage = InMemoryAuthStorage()
    ) : this(AppAuthService(context), configuration, storage)

    private val appAuthServiceConfig: AuthorizationServiceConfiguration =
        AuthorizationServiceConfiguration(
            Uri.parse(configuration.authorizationEndpoint + Authorization.OAUTH_PATH_AUTHORIZE),
            Uri.parse(configuration.tokenEndpoint + Authorization.OAUTH_PATH_TOKEN)
        )

    fun loginIntent(
        scopes: Set<String>?,
        publicKey: String
    ): Intent {
        val scopesSet: Set<String> = scopes ?: Authorization.defaultScopes

        val additionalParameters = HashMap<String, String>()
        additionalParameters[PARAMETER_PUBLIC_KEY] = publicKey

        val request = AuthorizationRequest.Builder(
            appAuthServiceConfig,
            configuration.clientId,
            ResponseTypeValues.CODE,
            Uri.parse(configuration.callbackUrl)
        )
            .setAdditionalParameters(additionalParameters)
            .setScopes(scopesSet.toList())
            .build()

        return appAuthService.getAuthorizationRequestIntent(request)
    }

    fun finishLogin(authData: Intent, callback: Callback) {
        val authResponse = AuthorizationResponse.fromIntent(authData)
        val authException = net.openid.appauth.AuthorizationException.fromIntent(authData)
        if (authResponse != null) {
            val authState = AuthState(authResponse, authException)
            val request = authResponse.createTokenExchangeRequest()
            val clientAuth = ClientSecretBasic(configuration.clientSecret)

            appAuthService.performTokenRequest(request, clientAuth) { tokenResponse, ex ->
                authState.update(tokenResponse, ex)
                writeAuthState(authState)
                if (tokenResponse != null) {
                    callback.onSuccess()
                } else {
                    callback.onError(Throwable(ex))
                }
            }
        } else {
            callback.onError(Throwable(authException))
        }
    }

    @Throws(AuthorizationException.FailedToRestoreAccessToken::class)
    actual override fun getAccessToken(alias: String): String {
        val state = readAuthState()

        state?.accessToken?.let {
            return it
        }

        throw AuthorizationException.FailedToRestoreAccessToken()
    }

    @Throws(AuthorizationException.FailedToRestoreRefreshToken::class)
    actual override fun getRefreshToken(alias: String): String {
        val state = readAuthState()

        state?.refreshToken?.let {
            return it
        }

        throw AuthorizationException.FailedToRestoreRefreshToken()
    }

    @Throws(AuthorizationException.FailedToRefreshAccessToken::class)
    actual override fun refreshAccessToken(alias: String): String {
        var accessToken: String?
        runBlocking {
            val state = requestAccessToken()
            writeAuthState(state)
            accessToken = state.accessToken
        }

        accessToken?.let {
            return it
        }

        throw AuthorizationException.FailedToRefreshAccessToken()
    }

    private suspend fun requestAccessToken(): AuthState {
        val state = readAuthState()
        val request = state?.createTokenRefreshRequest()
        val clientAuth: ClientAuthentication = ClientSecretBasic(configuration.clientSecret)
        request?.let {
            val result = requestTokenResponse(request, clientAuth)
            state.update(result.tokenResponse, result.exception)
            result.exception?.let { throw it }

            return@requestAccessToken state
        }

        throw AuthorizationException.FailedToRestoreRefreshToken()
    }

    private data class TokenRequestData(
        val tokenResponse: TokenResponse?,
        val exception: net.openid.appauth.AuthorizationException?
    )

    private suspend fun requestTokenResponse(
        request: TokenRequest,
        clientAuth: ClientAuthentication
    ): TokenRequestData {
        return suspendCoroutine { continuation ->
            val callback = AuthorizationService.TokenResponseCallback { response, ex ->
                continuation.resumeWith(kotlin.Result.success(TokenRequestData(response, ex)))
            }

            appAuthService.performTokenRequest(request, clientAuth, callback)
        }
    }

    private fun readAuthState(): AuthState? {
        val authStateJson = storage.readAuthState(USER_ALIAS)
        try {
            return authStateJson?.let { AuthState.jsonDeserialize(it) }
        } catch (e: JSONException) {
            throw AuthorizationException.FailedToRestoreAuthState()
        }
    }

    actual override fun isAuthorized(alias: String): Boolean =
        storage.readAuthState(USER_ALIAS) != null

    private fun writeAuthState(state: AuthState) {
        storage.writeAuthState(USER_ALIAS, state.jsonSerializeString())
    }

    actual override fun clear() {
        storage.clear()
    }

    companion object {
        private const val EXTRA_SCOPES = "SCOPES"

        private const val PARAMETER_PUBLIC_KEY = "public_key"

        internal const val USER_ALIAS = "care.data4life.auth.auth_state"
    }

    interface Callback {
        fun onSuccess()
        fun onError(error: Throwable)
    }
}
