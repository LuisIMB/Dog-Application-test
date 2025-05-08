package com.example.dogapp.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dogapp.databinding.ActivityDetailBinding
import com.example.dogapp.ui.adapter.ImagePagerAdapter
import com.example.dogapp.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val breed = intent.getStringExtra("breed")
        val subBreed = intent.getStringExtra("subBreed")

        supportActionBar?.title = if (subBreed != null) "$subBreed $breed" else breed

        viewModel.loadImages(breed ?: "", subBreed)

        viewModel.images.observe(this) { imageUrls ->
            val adapter = ImagePagerAdapter(imageUrls)
            binding.viewPager.adapter = adapter
            binding.viewPager.isUserInputEnabled = imageUrls.size > 1

        }

    }
}
