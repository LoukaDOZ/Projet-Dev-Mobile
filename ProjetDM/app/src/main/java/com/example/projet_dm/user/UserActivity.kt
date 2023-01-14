package com.example.projet_dm.user

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import coil.compose.AsyncImage
import com.example.projet_dm.Detail
import com.example.projet_dm.data.User
import com.example.projet_dm.tasklist.Task
import com.example.projet_dm.ui.theme.ProjetDMTheme
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
    private var goToSettingsLambda: () -> Unit = {}

    private var shouldAskPermissionRequest = true;
    private var doUriChanged = false;

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val user = intent.getSerializableExtra("user")
            var editUser: User? = null
            if(user != null) editUser = user as User
            doUriChanged = false

            ProjetDMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    UserDetail("Android", editUser)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun UserDetail(name: String, user: User? = null) {
        var uri: Uri? by remember {
            if(user != null) mutableStateOf(Uri.parse(user.avatar))
            else mutableStateOf(null)
        }

        var editUser by remember {
            if(user != null) mutableStateOf(user)
            else mutableStateOf(User("", "", ""))
        }

        val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                uri = captureUri
                doUriChanged = true
            }
        }

        val browsePicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                uri = it
                doUriChanged = true
            }
        }

        val requestPermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if(granted) browsePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            else {
                if(shouldAskPermissionRequest)
                    pickPhotoWithPermission()
                else
                    shouldAskPermissionRequest = true;
            }
        }

        browsePictureLambda = {
            browsePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requestPermissionsLambda = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        goToSettingsLambda = {
            startActivity(Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            ))
        }

        val mainColor = Color(0xffe44232)
        val textFieldOutline = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = mainColor,
            unfocusedBorderColor = Color.Black,
            textColor = Color.Black,
            cursorColor = mainColor,
            focusedLabelColor = mainColor,
            unfocusedLabelColor = Color.Black
        )
        val buttonColors = ButtonDefaults.buttonColors(Color(0xffe44232), Color.White)

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Edit user",
                    style = MaterialTheme.typography.h2,
                    color = Color.Black
                )
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                )
                Button(
                    onClick = {
                        //takePicture.launch(null)
                        takePicture.launch(captureUri)
                    },
                    content = { Text("Take picture") },
                    colors = buttonColors
                )
                Button(
                    onClick = {
                        requestPermissionsLambda()
                    },
                    content = { Text("Pick photo") },
                    colors = buttonColors
                )
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(value = editUser.email, colors = textFieldOutline,
                    onValueChange = { v -> editUser = editUser.copy(email = v) }, label = {Text("email")})
                OutlinedTextField(value = editUser.name, colors = textFieldOutline,
                    onValueChange = { v -> editUser = editUser.copy(name = v) }, label = {Text("name")})
                Button(
                    onClick = {
                        viewModel.edit(editUser)
                        if(doUriChanged) {
                            viewModel.editAvatar(uri!!.toRequestBody())
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    },
                    content = { Text("Save") },
                    colors = buttonColors
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ProjetDMTheme {
            UserDetail("Android")
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
            isExplanationNeeded -> showDialog("The application needs an access to medias so you can pick a photo and change your profile picture", permissionListener)
            else -> showDialog("The app settings window will open so you can give, to the application, an access to medias in order to pick a photo and change your profile picture", settingsListener)
        }
    }

    private var permissionListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                requestPermissionsLambda()
                shouldAskPermissionRequest = false;
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                showMessage("Without access to medias, you won't be able to change your profile picture")
            }
        }
    }

    private var settingsListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                goToSettingsLambda()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                showMessage("Without access to medias, you won't be able to change your profile picture")
            }
        }
    }

    private fun showDialog(message: String, listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message).setPositiveButton("Ok", listener)
            .setNegativeButton("Cancel", listener).show()
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