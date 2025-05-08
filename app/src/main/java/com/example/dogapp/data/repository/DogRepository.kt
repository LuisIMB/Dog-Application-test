package com.example.dogapp.data.repository

import android.content.Context
import com.example.dogapp.data.api.DogApiService
import com.example.dogapp.data.db.DogDatabaseHelper
import com.example.dogapp.data.model.DogItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class DogRepository(
    private val context: Context,
    private val apiService: DogApiService
) {
    private val dbHelper = DogDatabaseHelper(context)

    private fun isFirstRun(): Boolean {
        val prefs = context.getSharedPreferences("dog_app_prefs", Context.MODE_PRIVATE)
        return !prefs.getBoolean("db_initialized", false)
    }

    private fun setInitialized() {
        val prefs = context.getSharedPreferences("dog_app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("db_initialized", true).apply()
    }


    suspend fun fetchAndCacheAllBreeds() = withContext(Dispatchers.IO) {
        if (isFirstRun()) {
            try {
                val breedMap = apiService.getAllBreeds().message

                for ((breed, subBreeds) in breedMap) {
                    if (subBreeds.isEmpty()) {
                        // No sub-breeds
                        val imageList = apiService.getImagesForBreed(breed).message
                        dbHelper.insertBreedWithSubBreedImages(breed, null, imageList)
                    } else {
                        for (sub in subBreeds) {
                            val imageList = apiService.getImagesForSubBreed(breed, sub).message
                            dbHelper.insertBreedWithSubBreedImages(breed, sub, imageList)
                        }
                    }
                }
                setInitialized()
            } catch (e: Exception) {
                e.printStackTrace()
                // Log or show error if needed
            }
        }
        else {
            Log.d("DogRepository", "Database already initialized")
        }
    }

    fun getAllDogItems(): List<DogItem> {
        return dbHelper.getAllDogItems()
    }

    fun getImagesForBreed(breed: String, subBreed: String?): List<String> {
        return dbHelper.getImagesForBreed(breed, subBreed)
    }
}
