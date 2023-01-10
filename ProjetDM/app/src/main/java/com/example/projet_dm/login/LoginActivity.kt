package com.example.projet_dm.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projet_dm.ui.theme.ProjetDMTheme
import java.util.*

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.fragment.app.viewModels
import com.example.projet_dm.R
import com.example.projet_dm.data.Api
import com.example.projet_dm.data.ApiKey
import com.example.projet_dm.tasklist.Task

import com.example.projet_dm.login.LoginViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //TODO
            val viewModel: LoginViewModel by viewModels()
            val loginInfo =  intent.getSerializableExtra("loginInfo")
            val apiKey = intent.getSerializableExtra("apiKey")


            ProjetDMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Login() { loginInfo ->
                        Log.e("Network", "TESTAAAA")
                        getApiKey(loginInfo)
                        Log.e("Network", "TESTBBB")
                        intent.putExtra("apiKey", loginInfo)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }
}

fun getApiKey(loginInfo : LoginInfo): String {
    Log.e("Network", "TEST")
    Log.e("Network", "TEST4")
    val webService = Api.LoginWebService
    Log.e("Network", "TEST2")
    val response = webService.getApiKey(loginInfo) // Call HTTP (opération longue)
    Log.e("Network", "TEST3")
    if (!response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
        Log.e("Network", "Error: ${response.message()}")
        return "FAIL"
    }
    //TODO : rendre la clé utilisable pour toujours

    val key = response.body()!!
    Log.e("Network", "SUCCESS : $key")
    return "$key"
}

@Composable
fun Login(onValidate: (LoginInfo) -> Unit = {}) {
    var newLoginInfo by remember {
        mutableStateOf(LoginInfo("", ""))
    }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val icon = if (passwordVisible)
        painterResource(id = R.drawable.ic_visibility)
    else
        painterResource(id = R.drawable.ic_visibility_off)

    Column (Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)){
        Text(text = "Login", style = MaterialTheme.typography.h1)
        OutlinedTextField(value = newLoginInfo.username,
            onValueChange = { v -> newLoginInfo = newLoginInfo.copy(username = v) },
            label = { Text(text = "Username") },
            placeholder = { Text("Username") }
        )
        OutlinedTextField(value = newLoginInfo.password,
            onValueChange = { v -> newLoginInfo = newLoginInfo.copy(password = v) },
            placeholder = { Text(text = "Password") },
            label = { Text(text = "Password") },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(
                        painter = icon,
                        contentDescription = "Visibility Icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation()
        )
        Button(onClick = { val test = onValidate(newLoginInfo); print("$test"); /*TODO*/ }) {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjetDMTheme {
        Login()
    }
}
