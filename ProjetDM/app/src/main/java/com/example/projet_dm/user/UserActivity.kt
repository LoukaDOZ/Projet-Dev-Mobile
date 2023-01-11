package com.example.projet_dm.user

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.example.projet_dm.data.User
import com.google.android.material.snackbar.Snackbar
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*


class UserActivity : ComponentActivity() {
    // propriété: une URI dans le dossier partagé "Images"
    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }
    private val viewModel: UserViewModel by viewModels()

    private var requestPermissionsLambda: () -> Unit = {}
    private var browsePictureLambda: () -> Unit = {}

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }
            val composeScope = rememberCoroutineScope()

            /*val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap = it
                composeScope.launch {
                    Api.userWebService.updateAvatar(bitmap!!.toRequestBody())
                }
            }*/

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) uri = captureUri
            }

            val browsePicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                if (it != null) uri = it
            }

            val requestPermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if(granted) browsePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                else showMessage("Without access to medias, you won't be able to change your profile picture")
            }

            val user = intent.getSerializableExtra("user")
            var editUser by remember {
                if(user != null) {
                    uri = Uri.parse((user as User).avatar)
                    mutableStateOf(user as User)
                }
                else mutableStateOf(User("", "", ""))
            }


            browsePictureLambda = {
                browsePicture.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            requestPermissionsLambda = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    requestPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = uri,
                    contentDescription = null
                )
                OutlinedTextField(value = editUser.email,
                    onValueChange = { v -> editUser = editUser.copy(email = v) }, label = {Text("email")})
                OutlinedTextField(value = editUser.name,
                    onValueChange = { v -> editUser = editUser.copy(name = v) }, label = {Text("name")})
                Button(
                    onClick = {
                        //takePicture.launch(null)
                        takePicture.launch(captureUri)
                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        pickPhotoWithPermission()
                    },
                    content = { Text("Pick photo") }
                )
                Button(
                    onClick = {
                        viewModel.edit(editUser)
                        if(!(uri?.equals(Uri.parse(editUser.avatar))!!))
                            viewModel.editAvatar(uri!!.toRequestBody())
                        setResult(RESULT_OK, intent)
                        finish()
                    },
                    content = { Text("Submit") }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pickPhotoWithPermission() {
        var camPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            camPermission = Manifest.permission.READ_MEDIA_IMAGES

        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> browsePictureLambda()
            isExplanationNeeded -> showDialog("The application needs an access to medias so you can pick a photo and change your profile picture")
            else -> requestPermissionsLambda()
        }
    }

    private var dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                requestPermissionsLambda()
            }
            DialogInterface.BUTTON_NEGATIVE -> {}
        }
    }

    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message).setPositiveButton("Ok", dialogClickListener)
            .setNegativeButton("Cancel", dialogClickListener).show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
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