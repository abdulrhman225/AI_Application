package com.example.ai

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.material3.Text
import androidx.core.graphics.drawable.toIcon
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AIModel:ViewModel() {
    private val _mutableImage = MutableStateFlow("")
    private val _mutable = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)
    val mutable:StateFlow<String> = _mutable
    val mutableImage:StateFlow<String> = _mutableImage
    val isLoading:StateFlow<Boolean> = _isLoading

    fun getResponse(model :GenerativeModel , request :String){
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = model.generateContent(
                content {
                    text(request)
                }
            )
            withContext(Dispatchers.Main) {
                _isLoading.value = false
                _mutable.value = response.text.toString()
            }
        }
    }


    fun getImageResponse(model :GenerativeModel , request :String , image: Uri? , context:Context){
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = model.generateContent(
                content {
                    image(MediaStore.Images.Media.getBitmap(context.contentResolver,image))
                    text(request)

                }
            )
            withContext(Dispatchers.Main) {
                _isLoading.value = false
                _mutableImage.value = response.text.toString()
            }
        }
    }
}