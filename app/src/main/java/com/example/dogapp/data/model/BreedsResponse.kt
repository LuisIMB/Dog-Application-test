package com.example.dogapp.data.model

data class BreedsResponse(
    val status: String,
    val message: Map<String, List<String>>
)

data class ImageResponse(
    val status: String,
    val message: List<String>
)
