package com.example.projet_dm.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateBody(
    @SerialName("email")
    val type: String,
    @SerialName("full_name")
    val name: String,
    @SerialName("current_password")
    val password: String
) : java.io.Serializable