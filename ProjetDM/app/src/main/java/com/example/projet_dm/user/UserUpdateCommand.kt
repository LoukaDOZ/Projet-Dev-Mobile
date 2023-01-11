package com.example.projet_dm.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateCommand(
    @SerialName("type")
    val type: String,
    @SerialName("uuid")
    val uuid: String,
    @SerialName("args")
    val args: UserUpdateBody
) : java.io.Serializable