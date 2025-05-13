package com.example.dogapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogapp.R
import com.example.dogapp.data.model.DogItem
import com.example.dogapp.databinding.ItemDogBinding

class DogAdapter(
    private var items: List<DogItem>,
    private var dogNames: List<String>,
    private val onDogItemClick: (DogItem) -> Unit,
    private val onNameClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_DOG_ITEM = 1
    }

    private var dogSearchAdapter = DogSearchAdapter(dogNames, onNameClick)
    private var headerLayoutManager: LinearLayoutManager? = null

    inner class DogViewHolder(val binding: ItemDogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DogItem) {
            val displayName = if (!item.subBreed.isNullOrEmpty()) {
                "${item.subBreed.replaceFirstChar { it.uppercaseChar() }} ${item.breed}"
            } else {
                item.breed.replaceFirstChar { it.uppercaseChar() }
            }

            binding.textViewBreed.text = displayName
            Glide.with(binding.root.context).load(item.imageUrl).into(binding.imageViewDog)
            binding.root.setOnClickListener { onDogItemClick(item) }
        }
    }

    inner class HeaderViewHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_DOG_ITEM
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_header, parent, false) as RecyclerView

            headerLayoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
            view.layoutManager = headerLayoutManager
            view.adapter = dogSearchAdapter
            HeaderViewHolder(view)

        } else {
            val binding = ItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DogViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DogViewHolder) {
            holder.bind(items[position - 1])
        }
    }

    fun updateData(newItems: List<DogItem>, newNames: List<String>) {
        items = newItems
        dogNames = newNames
        dogSearchAdapter.updateItems(dogNames)
        notifyDataSetChanged()
    }

    fun setSelectedDogName(name: String) {
        dogSearchAdapter.setSelectedDogName(name)
    }

    fun saveScrollPosition() : Int {
        return headerLayoutManager?.findLastVisibleItemPosition() ?: 0
    }

    fun restoreScrollPosition(position : Int) {
        headerLayoutManager?.scrollToPosition(position)
    }

}
