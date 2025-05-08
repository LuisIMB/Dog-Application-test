package com.example.dogapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogapp.data.model.DogItem
import com.example.dogapp.databinding.ItemDogBinding



class DogAdapter(
    private var items: List<DogItem>,
    private val onItemClick: (DogItem) -> Unit
) : RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    inner class DogViewHolder(val binding: ItemDogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DogItem) {
            Log.d("DogAdapter", "Loading image: ${item.imageUrl}")
            val displayName = if (!item.subBreed.isNullOrEmpty()) {
                val capitalizedSub = item.subBreed.replaceFirstChar { it.uppercaseChar() }
                "$capitalizedSub ${item.breed}"
            } else {
                item.breed.replaceFirstChar { it.uppercaseChar() }
            }

            binding.textViewBreed.text = displayName

            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .into(binding.imageViewDog)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    fun updateData(newList: List<DogItem>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    override fun getItemCount(): Int = items.size
}
