package com.example.projet_dm.tasklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dm.data.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TasksListViewModel : ViewModel() {
    private val webService = Api.tasksWebService

    public val tasksStateFlow = MutableStateFlow<List<Task>>(emptyList())

    fun refresh() {
        viewModelScope.launch {
            val response = webService.fetchTasks() // Call HTTP (opération longue)
            if (!response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            val fetchedTasks = response.body()!!
            tasksStateFlow.value = fetchedTasks // on modifie le flow, ce qui déclenche ses observers
        }
    }

    // à compléter plus tard:
    fun add(task: Task) {
        viewModelScope.launch {
            val response = Api.tasksWebService.create(task)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            val createdTask = response.body()!!
            tasksStateFlow.value += createdTask
        }
    }

    fun edit(task: Task) {
        viewModelScope.launch {
            val response = Api.tasksWebService.update(task, task.id)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            val updatedTask = response.body()!!
            tasksStateFlow.value = tasksStateFlow.value.map { if (it.id == updatedTask.id) updatedTask else it }
        }
    }

    fun remove(task: Task) {
        viewModelScope.launch {
            val response = Api.tasksWebService.delete(task.id)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }
            tasksStateFlow.value -= task
        }
    }
}