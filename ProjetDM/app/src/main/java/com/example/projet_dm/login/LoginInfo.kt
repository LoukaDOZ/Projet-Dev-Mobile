package com.example.projet_dm.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String
) : java.io.Serializable