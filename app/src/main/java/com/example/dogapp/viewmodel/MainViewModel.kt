package com.example.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.dogapp.data.api.DogApiService
import com.example.dogapp.data.api.RetrofitInstance
import com.example.dogapp.data.repository.DogRepository
import com.example.dogapp.data.model.DogItem
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DogRepository(application, RetrofitInstance.apiService)

    private val _dogItems = MutableLiveData<List<DogItem>>()
    val dogItems: LiveData<List<DogItem>> get() = _dogItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadDogData()
    }

    private fun loadDogData() {
        viewModelScope.launch {
            _isLoading.value = true

            // Fetch and cache from API into DB
            repository.fetchAndCacheAllBreeds()

            // Load from DB
            val dogs = repository.getAllDogItems()
            _dogItems.value = dogs

            _isLoading.value = false
        }
    }
}
