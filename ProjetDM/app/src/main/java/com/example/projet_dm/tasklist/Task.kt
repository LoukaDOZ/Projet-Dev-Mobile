package com.example.projet_dm.tasklist

data class Task(val id: String, val title: String, val description: String = "Default description") : java.io.Serializable
