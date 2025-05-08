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

class RandomImageDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DogDatabaseHelper(application)

    private val _names = MutableLiveData<List<String>>()
    val names: LiveData<List<String>> get() = _names

    fun loadBreed(imageUrl: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                dbHelper.getBreedAndSubBreedByImageUrl(imageUrl)
            }
            _names.value = result
        }
    }
}
