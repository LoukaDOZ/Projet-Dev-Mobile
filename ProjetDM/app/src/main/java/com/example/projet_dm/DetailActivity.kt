package com.example.projet_dm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projet_dm.tasklist.Task
import com.example.projet_dm.ui.theme.ProjetDMTheme
import java.util.*

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*var shareDescription = ""
            var sharing = false
            var editTask: Task? = null

            when (intent?.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                            shareDescription = it
                            sharing = true
                        }
                    }
                }
            }

            if(sharing)
                editTask = Task(id = UUID.randomUUID().toString(), title="", description = shareDescription)
            else {
                if(intent.getSerializableExtra("task") != null){
                    editTask = intent.getSerializableExtra("task") as Task
                }
            }*/

            val task = intent.getSerializableExtra("task")
            var editTask: Task? = null
            if(task != null) editTask = task as Task

            ProjetDMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Detail("Android", editTask
                    ) { newTask ->
                        intent.putExtra("task", newTask)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun Detail(name: String, editTask: Task? = null, onValidate: (Task) -> Unit = {}) {
    var newTask by remember {
        mutableStateOf(Task(id = editTask?.id ?: UUID.randomUUID().toString(), title = editTask?.title ?: "", description = editTask?.description ?: ""))
    }

    Column (Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)){
        Text(text = "Task Detail", style = MaterialTheme.typography.h1)
        OutlinedTextField(value = newTask.title, onValueChange = { v -> newTask = newTask.copy(title = v) }, label = {Text("title")})
        OutlinedTextField(value = newTask.description, onValueChange = { value -> newTask = newTask.copy(description = value) }, label = {Text("description")})
        Button(onClick = { onValidate(newTask) }) {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjetDMTheme {
        Detail("Android")
    }
}