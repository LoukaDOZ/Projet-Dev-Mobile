package com.example.projet_dm.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdate(
    @SerialName("commands")
    val commands: List<UserUpdateCommand>
) : java.io.Serializable