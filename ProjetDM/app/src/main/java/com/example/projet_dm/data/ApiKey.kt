package com.example.projet_dm.data;

import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;

@Serializable
data class ApiKey (
    @SerialName("access_token")
    val access_token: String
)
