package com.example.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.api.RetrofitInstance
import com.example.dogapp.data.repository.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RandomImageDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DogRepository(application, RetrofitInstance.apiService)

    private val _randomImages = MutableLiveData<List<String>>()
    val randomImages: LiveData<List<String>> get() = _randomImages
    private val _names = MutableLiveData<List<String>>()
    val names: LiveData<List<String>> get() = _names

    fun loadBreed(imageUrl: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getBreedAndSubBreedByImageUrl(imageUrl)
            }
            _names.value = result
        }
    }

    fun fetchRandomImages(breed: String, subBreed: String?, amount: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.fetchRandomDogImagesForBreed(breed, subBreed, amount)
            }
            _randomImages.value = result
        }
    }
}
