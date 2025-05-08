package com.example.dogapp.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import com.example.dogapp.data.model.*

interface DogApiService {

    @GET("breeds/list/all")
    suspend fun getAllBreeds(): BreedsResponse

    @GET("breed/{breed}/images/random")
    suspend fun getRandomImage(@Path("breed") breed: String): ImageResponse

    @GET("breed/{breed}/images")
    suspend fun getImagesForBreed(@Path("breed") breed: String): ImageResponse

    @GET("breed/{breed}/{subBreed}/images")
    suspend fun getImagesForSubBreed(
        @Path("breed") breed: String,
        @Path("subBreed") subBreed: String
    ): ImageResponse

}
