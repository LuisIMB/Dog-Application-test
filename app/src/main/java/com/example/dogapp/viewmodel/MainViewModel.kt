package com.example.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.dogapp.data.api.DogApiService
import com.example.dogapp.data.api.RetrofitInstance
import com.example.dogapp.data.repository.DogRepository
import com.example.dogapp.data.model.DogItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var loadJob: Job? = null

    private val repository = DogRepository(application, RetrofitInstance.apiService)

    private val _dogItems = MutableLiveData<List<DogItem>>()
    val dogItems: LiveData<List<DogItem>> get() = _dogItems
    private val _filteredDogs = MutableLiveData<List<DogItem>>()
    val filteredDogs: LiveData<List<DogItem>> get() = _filteredDogs
    private val _randomDogImage = MutableLiveData<String>()
    val randomDogImage: LiveData<String> get() = _randomDogImage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadDogData()
    }

    private fun loadDogData() {
        loadJob = viewModelScope.launch {
            _isLoading.value = true

            // Fetch and cache from API into DB
            repository.fetchAndCacheAllBreeds()
            // Load from DB
            val dogs = repository.getAllDogItems()
            _dogItems.value = dogs
            _filteredDogs.value = dogs

            _isLoading.value = false
        }
    }
    fun getRandomDogImage() {
        viewModelScope.launch {
            val dog = repository.fetchRandomDogImage()
            _randomDogImage.value = dog
        }
    }
    fun cancelLoading() {
        loadJob?.cancel()
    }

    fun filterDogs(query: String) {
        val currentList = _dogItems.value.orEmpty()
        val lowerQuery = query.lowercase()

        val filtered = currentList.filter { dog ->
            val breedMatch = dog.breed.lowercase().contains(lowerQuery)
            val subBreedMatch = dog.subBreed?.lowercase()?.contains(lowerQuery) == true
            breedMatch || subBreedMatch
        }
        _filteredDogs.value = filtered
    }



}
