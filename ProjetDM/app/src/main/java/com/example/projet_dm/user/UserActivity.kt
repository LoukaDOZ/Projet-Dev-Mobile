package com.example.projet_dm.user

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.example.projet_dm.data.Api
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }
            val composeScope = rememberCoroutineScope()

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap = it
                composeScope.launch {
                    bitmap?.let { it1 -> Api.userWebService.updateAvatar(it1.toRequestBody()) }
                }
            }

            val browsePicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri = it
                composeScope.launch {
                    uri?.let { it1 -> Api.userWebService.updateAvatar(it1.toRequestBody()) }
                }
            }

            val requestPermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if(granted) browsePicture.launch(null)
            }

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        takePicture.launch(null)
                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        requestPermissions.launch(READ_EXTERNAL_STORAGE)
                    },
                    content = { Text("Pick photo") }
                )
            }
        }
    }

    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use* se charge de faire open et close
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }
}