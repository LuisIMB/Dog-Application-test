package com.example.dogapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dogapp.databinding.ActivityRandomImagesGridBinding
import com.example.dogapp.ui.activity.DetailActivity.Companion.INTENT_BREED
import com.example.dogapp.ui.activity.DetailActivity.Companion.INTENT_SUBBREED
import com.example.dogapp.ui.adapter.DogImageGridAdapter
import com.example.dogapp.ui.adapter.ImagePagerAdapter
import com.example.dogapp.viewmodel.RandomImageDetailViewModel
import com.example.dogapp.viewmodel.MainViewModel

class RandomImagesBreedDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomImagesGridBinding
    private lateinit var viewModel: RandomImageDetailViewModel
    private lateinit var dogGridAdapter: DogImageGridAdapter
    companion object {
        const val INTENT_NUMBER = "intent_number"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRandomImagesGridBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[RandomImageDetailViewModel::class]
        setContentView(binding.root)

        val number = intent.getIntExtra(INTENT_NUMBER, 2)
        val breed = intent.getStringExtra(INTENT_BREED)
        val subBreed = intent.getStringExtra(INTENT_SUBBREED)

        //hacer un fetch al API del breed en el que me encuentro (+ subbreed si aplica) del numero necesario
        viewModel.randomImages.observe(this) { imageList ->
            dogGridAdapter = DogImageGridAdapter(imageList) { item ->
                val intent = Intent(this, RandomImagesBreedDetailActivity::class.java).apply {
                    //startActivity(this) otra posibilidad
                }
            }
            binding.imageGridRecyclerView.adapter = dogGridAdapter
        }
        if (!viewModel.randomImages.isInitialized) //evitar que se regeneren al rotar pantalla
            viewModel.fetchRandomImages(breed!!, subBreed, number)

        viewModel.randomImages.observe(this) { imageUrls ->
            val adapter = ImagePagerAdapter(imageUrls)

            binding.imageGridRecyclerView.adapter = adapter

        }
        supportActionBar?.title = breed
    }
}
