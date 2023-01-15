package com.example.projet_dm.login

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.projet_dm.data.Api
import com.example.projet_dm.ui.theme.ProjetDMTheme
import kotlinx.coroutines.launch
import net.openid.appauth.*

class LoginActivity : ComponentActivity() {
    companion object {
        val TOKEN_PREF_KEY = stringPreferencesKey("TOKEN")
    }
    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val authService by lazy { AuthorizationService(this) }
    private val requestToken = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
        println("TEST 0")
        val authMethod = ClientSecretPost(Api.APP_SECRET)
        val authException = AuthorizationException.fromIntent(it.data)
        if (authException != null) throw authException
        println("TEST 1")
        val authResponse = AuthorizationResponse.fromIntent(it.data!!) ?: throw IllegalStateException("No auth response data")
        val tokenRequest = authResponse.createTokenExchangeRequest()
        println("TEST 2")
        authService.performTokenRequest(tokenRequest, authMethod) { response, exception ->
            if (exception != null) throw exception
            lifecycleScope.launch {
                // response and token can't be null because exception was null
                dataStore.edit { it[TOKEN_PREF_KEY] = response!!.accessToken!! }
            }
            finish()
        }
    }

    fun requestToken() {
        val configuration = AuthorizationServiceConfiguration(
            Uri.parse("https://todoist.com/oauth/authorize/"),
            Uri.parse("https://todoist.com/oauth/access_token/"),
        )
        val builder = AuthorizationRequest.Builder(
            configuration,
            Api.APP_ID,
            ResponseTypeValues.CODE,
            Uri.parse("http://localhost:3000/receive_code/")
        )
        val authRequest = builder
            .setScopes("data:read_write,data:delete")
            .build()

        val authRequestIntent =
            authService.getAuthorizationRequestIntent(authRequest)!! // never returns null (from Java)

        authService.dispose()
        println("TEST -1")
        requestToken.launch(authRequestIntent)
    }

    @SuppressLint("FlowOperatorInvokedInComposition", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetDMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    requestToken()
                }
            }
        }
    }
}