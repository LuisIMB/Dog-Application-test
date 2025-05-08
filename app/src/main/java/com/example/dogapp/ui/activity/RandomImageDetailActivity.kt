package com.example.dogapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dogapp.databinding.ActivityRandomImageDetailBinding
import com.example.dogapp.ui.adapter.ImagePagerAdapter
import com.example.dogapp.viewmodel.RandomImageDetailViewModel
import com.example.dogapp.viewmodel.MainViewModel

class RandomImageDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomImageDetailBinding
    private lateinit var viewModel: RandomImageDetailViewModel
    companion object {
        const val INTENT_IMAGE = "image_url"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRandomImageDetailBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[RandomImageDetailViewModel::class]
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra(INTENT_IMAGE)
        if (imageUrl != null) {
            // Show image
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imageViewFull)

            // Ask ViewModel to query the DB
            viewModel.loadBreed(imageUrl)

            // Observe the name data
            viewModel.names.observe(this) { nameList ->
                val breed =
                    nameList.getOrNull(0)?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown"
                val subBreed = nameList.getOrNull(1)?.replaceFirstChar { it.uppercaseChar() }

                val displayName = if (!subBreed.isNullOrEmpty()) {
                    "$subBreed $breed"
                } else {
                    breed
                }

                binding.textViewDogName.text = displayName
                supportActionBar?.title = displayName
            }
        }
    }
}
