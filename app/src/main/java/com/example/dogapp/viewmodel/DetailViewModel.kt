package com.example.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.db.DogDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DogDatabaseHelper(application)

    private val _images = MutableLiveData<List<String>>()
    val images: LiveData<List<String>> get() = _images

    fun loadImages(breed: String, subBreed: String?) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                dbHelper.getImagesForBreed(breed, subBreed)
            }
            _images.value = result
        }
    }
}
