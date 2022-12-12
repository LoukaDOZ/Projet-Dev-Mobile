package com.example.projet_dm.tasklist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("content")
    val title: String,
    @SerialName("description")
    val description: String = "Default description"
) : java.io.Serializable
