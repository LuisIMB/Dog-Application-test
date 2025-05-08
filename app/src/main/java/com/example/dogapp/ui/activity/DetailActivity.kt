package com.example.dogapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dogapp.databinding.ActivityDetailBinding
import com.example.dogapp.ui.adapter.ImagePagerAdapter
import com.example.dogapp.viewmodel.DetailViewModel
import com.example.dogapp.viewmodel.MainViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    companion object {
        const val INTENT_BREED = "intent_breed"
        const val INTENT_SUBBREED = "intent_subbreed"
        const val RESPONSE_BREED = 0
        const val RESPONSE_SUBBREED = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[DetailViewModel::class]
        setContentView(binding.root)

        val breed = intent.getStringExtra(INTENT_BREED)
        val subBreed = intent.getStringExtra(INTENT_SUBBREED)

        supportActionBar?.title = if (subBreed != null) "$subBreed $breed" else breed

        viewModel.loadImages(breed ?: "", subBreed)

        viewModel.images.observe(this) { imageUrls ->
            val adapter = ImagePagerAdapter(imageUrls)
            binding.viewPager.adapter = adapter
            binding.viewPager.isUserInputEnabled = imageUrls.size > 1

        }

    }

    override fun onBackPressed() {
        val breedName = supportActionBar?.title.toString()
        val resultIntent = Intent().apply {
            putExtra("dog_name", breedName)
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }


}
