package com.example.projet_dm.login

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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.projet_dm.data.Api
import com.example.projet_dm.data.User
import com.example.projet_dm.ui.theme.ProjetDMTheme
import kotlinx.coroutines.launch
import net.openid.appauth.*

class LoginActivity : ComponentActivity() {
    companion object {
        val TOKEN_PREF_KEY = stringPreferencesKey("TOKEN")
    }
    val Context.dataStore by preferencesDataStore(name = "settings")

    private val authService by lazy { AuthorizationService(this) }
    val requestToken = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val authMethod = ClientSecretPost("c74e5e261be54a77b39877b0a123b6e4")
        val authException = AuthorizationException.fromIntent(it.data)
        if (authException != null) throw authException
        val authResponse = AuthorizationResponse.fromIntent(it.data!!) ?: throw IllegalStateException("No auth response data")
        val tokenRequest = authResponse.createTokenExchangeRequest()
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
            "6ea37fd9bf6f4c47abd230729e5d5a25",
            ResponseTypeValues.CODE,
            Uri.parse("http://localhost:3000/receive_code/")
        )
        val authRequest = builder
            .setScopes("data:read_write,data:delete")
            .build()

        val authRequestIntent =
            authService.getAuthorizationRequestIntent(authRequest)!! // never returns null (from Java)

        requestToken.launch(authRequestIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            ProjetDMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    println("TEST")
                    requestToken()
                    println(dataStore.data)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}