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
    private val _spinnerList = MutableLiveData<List<String>>()
    val spinnerList: LiveData<List<String>> get() = _spinnerList
    private val _randomDogImage = MutableLiveData<String>()
    val randomDogImage: LiveData<String> get() = _randomDogImage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedDogName = MutableLiveData<String?>()
    val selectedDogName: LiveData<String?> = _selectedDogName

    var savedScrollPosition : Int = 0



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
            // Generate spinner list here once data is loaded
            _spinnerList.value = dogs.map {
                if (it.subBreed != null) "${it.subBreed} ${it.breed}" else it.breed
            }

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
        var lowerQuery = query.trim().lowercase()
        val test = _selectedDogName.value
        println("_selectedDogName: $test and query: $query")
        if (_selectedDogName.value.orEmpty().isNotBlank() && _selectedDogName.value != lowerQuery) //TODO mirar si va bien
            lowerQuery = _selectedDogName.value!!

        val filtered = if (" " in lowerQuery) {
            val parts = lowerQuery.split(" ", limit = 2)
            val part1 = parts.getOrNull(0).orEmpty()
            val part2 = parts.getOrNull(1).orEmpty()

            currentList.filter { dog ->
                val breed = dog.breed.lowercase()
                val subBreed = dog.subBreed?.lowercase().orEmpty()
                (breed.contains(part1) && subBreed.contains(part2)) ||
                        (breed.contains(part2) && subBreed.contains(part1))
            }
        } else {
            currentList.filter { dog ->
                val breedMatch = dog.breed.lowercase().contains(lowerQuery)
                val subBreedMatch = dog.subBreed?.lowercase()?.contains(lowerQuery) == true
                breedMatch || subBreedMatch
            }
        }

        _filteredDogs.value = filtered
    }

    fun setSelectedDogName(name: String?) {
        _selectedDogName.value = name
    }


}
