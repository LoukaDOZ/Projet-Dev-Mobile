package com.example.projet_dm.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dm.data.Api
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val webService = Api.LoginWebService

    fun connectWithLoginInfo(loginInfo : LoginInfo) {
        viewModelScope.launch {
            Log.e("Network", "TEST")
            val response = webService.getApiKey(loginInfo) // Call HTTP (opération longue)
            Log.e("Network", "TEST2")
            if (!response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
                Log.e("Network", "TEST4")
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            //TODO : rendre la clé utilisable pour toujours
            Log.e("Network", "TEST3")
            val key = response.body()!!
            Log.e("Network", "TEST5")
            Log.e("Network", "SUCCESS : $key")
        }
    }
}