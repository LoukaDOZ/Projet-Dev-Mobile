package com.example.projet_dm.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dm.data.Api
import com.example.projet_dm.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.*

class UserViewModel : ViewModel() {
    private val webService = Api.userWebService

    public val userStateFlow = MutableStateFlow<User>(User("", "", ""))

    fun refresh() {
        viewModelScope.launch {
            val response = webService.fetchUser() // Call HTTP (opération longue)
            if (!response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            val user = response.body()!!
            userStateFlow.value = user // on modifie le flow, ce qui déclenche ses observers
        }
    }

    fun edit(user: User) {
        val userUpdateBody = UserUpdateBody(user.email, user.name, Api.PASSWORD)
        val userUpdateList = emptyList<UserUpdateCommand>().toMutableList()
        userUpdateList += UserUpdateCommand("user_update", UUID.randomUUID().toString(), userUpdateBody)
        val userUpdate = UserUpdate(userUpdateList)

        viewModelScope.launch {
            val response = webService.update(userUpdate)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            userStateFlow.value = user
        }
    }

    fun editAvatar(avatar: MultipartBody.Part) {
        viewModelScope.launch {
            val response = webService.updateAvatar(avatar)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            val user = response.body()!!
            userStateFlow.value = user
        }
    }
}