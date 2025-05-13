package com.example.dogapp.ui.activity

import android.R
import androidx.activity.viewModels
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.dogapp.data.model.DogItem
import com.example.dogapp.databinding.ActivityMainBinding
import com.example.dogapp.ui.adapter.DogAdapter
import com.example.dogapp.viewmodel.MainViewModel
import androidx.appcompat.widget.SearchView
import com.example.dogapp.ui.adapter.DogSpinnerAdapter
import android.widget.AdapterView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private lateinit var viewModel: MainViewModel

    companion object {
        private const val DOG_NAME = "dog_name"
        private const val SELECTED_DOG_NAME = "selected_dog_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate of mainActivity executed")

        supportActionBar?.title = "Dog collection"
        //Basic initialization steps
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class]
        setContentView(binding.root)
        binding.dogRecyclerView.layoutManager = LinearLayoutManager(this)

        //Dog adapter set up
        adapter = DogAdapter(
            emptyList(),
            emptyList(),
            onDogItemClick = { item ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.INTENT_BREED, item.breed)
                    putExtra(DetailActivity.INTENT_SUBBREED, item.subBreed)
                }
                val responseCode = if (item.subBreed != null) {
                    DetailActivity.RESPONSE_SUBBREED
                } else {
                    DetailActivity.RESPONSE_BREED
                }
                startActivityForResult(intent, responseCode)
            },
            onNameClick = { selectedName ->
                viewModel.setSelectedDogName(selectedName)
                viewModel.filterDogs(selectedName)
            }
        )
        //Prepare the components that use the adapter
        binding.dogRecyclerView.adapter = adapter
        binding.dogRecyclerView.post {
            adapter.restoreScrollPosition(viewModel.savedScrollPosition)
        }

        viewModel.filteredDogs.observe(this) { dogList ->
            val dogNames = viewModel.spinnerList.value.orEmpty()
            adapter.updateData(dogList, dogNames)
        }

        viewModel.spinnerList.observe(this) { newNames ->
            val dogList = viewModel.filteredDogs.value.orEmpty()
            adapter.updateData(dogList, newNames)
        }

        val restoredName = savedInstanceState?.getString(SELECTED_DOG_NAME)
        restoredName?.let {
            viewModel.filterDogs(it)
            adapter.setSelectedDogName(it)
        }
        //Spinner set up
        viewModel.spinnerList.observe(this) { dogStrings ->
            val spinnerAdapter = DogSpinnerAdapter(this, listOf("Select a breed") + dogStrings)
            binding.spinnerDogList.adapter = spinnerAdapter

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDogList.adapter = spinnerAdapter
        }

        binding.spinnerDogList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long
            ) {
                if (position == 0) return

                val selectedText = parent.getItemAtPosition(position) as String
                val parts = selectedText.split(" ")

                val (subBreed, breed) = if (parts.size == 2) {
                    parts[0] to parts[1]
                } else {
                    null to parts[0]
                }

                val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.INTENT_BREED, breed)
                    putExtra(DetailActivity.INTENT_SUBBREED, subBreed)
                }

                val responseCode = if (subBreed != null) {
                    DetailActivity.RESPONSE_SUBBREED
                } else {
                    DetailActivity.RESPONSE_BREED
                }

                startActivityForResult(intent, responseCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        //Random image button + image combo
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

        //Search view (barra de búsqueda) set up
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterDogs(newText.orEmpty())
                return true
            }
        })
        //Selected dog name update (used to remember last selected dog for screen rotation)
        viewModel.selectedDogName.observe(this) { selectedName ->
            adapter.setSelectedDogName(selectedName.orEmpty())
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("Entered onsaveinstancestate")
        viewModel.savedScrollPosition = adapter.saveScrollPosition()
        outState.putString(SELECTED_DOG_NAME, viewModel.selectedDogName.value)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val dogName = data?.getStringExtra(DOG_NAME)
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
