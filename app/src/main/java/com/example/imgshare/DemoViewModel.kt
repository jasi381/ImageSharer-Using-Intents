package com.example.imgshare

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class DemoViewModel(application: Application) : AndroidViewModel(application) {

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error = _error.asStateFlow()

    private fun getBitmapFromUrl(url: String): Bitmap? {
        return try {
            URL(url).openStream().use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun shareImage(data: String?, shareImageLauncher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = data?.let { getBitmapFromUrl(it) }
                val imagesDir = getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val imageFile = File(imagesDir, "shared_images.jpeg")

                FileOutputStream(imageFile).use { outputStream ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                val uri = FileProvider.getUriForFile(
                    getApplication(),
                    "${getApplication<Application>().packageName}.fileprovider",
                    imageFile
                )

                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, uri as Parcelable)
                        putExtra(Intent.EXTRA_TEXT, "Hey check this out!")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Share Image")
                    shareImageLauncher.launch(chooser)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _error.value = e.message
                }
            }
        }
    }

    fun resetError() {
        _error.value = null
    }
}
