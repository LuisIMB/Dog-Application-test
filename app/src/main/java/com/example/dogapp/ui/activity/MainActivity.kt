package com.example.dogapp.ui.activity

import androidx.activity.viewModels
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dogapp.data.model.DogItem
import com.example.dogapp.databinding.ActivityMainBinding
import com.example.dogapp.ui.adapter.DogAdapter
import com.example.dogapp.viewmodel.MainViewModel
import androidx.appcompat.widget.SearchView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Dog collection"

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class]
        setContentView(binding.root)
        binding.dogRecyclerView.layoutManager = LinearLayoutManager(this)
        var fullDogList: List<DogItem> = emptyList()

        viewModel.filteredDogs.observe(this) { dogList ->
            fullDogList = dogList
            adapter = DogAdapter(dogList) { item ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.INTENT_BREED, item.breed)
                    putExtra(DetailActivity.INTENT_SUBBREED, item.subBreed)
                    //startActivity(this) otra posibilidad
                }
                val responseCode = if (item.subBreed != null) {
                    DetailActivity.RESPONSE_SUBBREED
                } else {
                    DetailActivity.RESPONSE_BREED
                }

                startActivityForResult(intent, responseCode)
            }
            binding.dogRecyclerView.adapter = adapter
        }
        viewModel.randomDogImage.observe(this) { dogImage ->
            Glide.with(this)
                .load(dogImage)
                .into(binding.imageViewRandom)
        }
        binding.buttonRandomImage.setOnClickListener {
            viewModel.getRandomDogImage()
        }
        binding.imageViewRandom.setOnClickListener {
            val imageUrl = viewModel.randomDogImage.value
            if (!imageUrl.isNullOrEmpty()) {
                val intent = Intent(this, RandomImageDetailActivity::class.java).apply {
                    putExtra(RandomImageDetailActivity.INTENT_IMAGE, imageUrl)
                }
                startActivity(intent)
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterDogs(newText.orEmpty())
                return true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val dogName = data?.getStringExtra("dog_name")
            when (requestCode) {
                DetailActivity.RESPONSE_BREED -> {
                    Toast.makeText(this, "You viewed breed: $dogName", Toast.LENGTH_SHORT).show()
                }
                DetailActivity.RESPONSE_SUBBREED -> {
                    Toast.makeText(this, "You viewed sub-breed: $dogName", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "An error has occurred", Toast.LENGTH_LONG).show()
        }
    }

}
