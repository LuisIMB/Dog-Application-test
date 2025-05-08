package com.example.dogapp.ui.activity

import androidx.activity.viewModels
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogapp.databinding.ActivityMainBinding
import com.example.dogapp.ui.adapter.DogAdapter
import com.example.dogapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Dog collection"

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dogRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.dogItems.observe(this) { dogList ->
            adapter = DogAdapter(dogList) { item ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("breed", item.breed)
                    putExtra("subBreed", item.subBreed)
                }
                startActivity(intent)
            }
            binding.dogRecyclerView.adapter = adapter
        }
    }
}
