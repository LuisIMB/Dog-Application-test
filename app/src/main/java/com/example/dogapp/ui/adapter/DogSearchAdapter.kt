package com.example.dogapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogapp.data.model.DogItem
import com.example.dogapp.databinding.ItemNameBinding



class DogSearchAdapter(
    private var items: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DogSearchAdapter.DogViewHolder>() {

    inner class DogViewHolder(val binding: ItemNameBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val displayName = item.replaceFirstChar { it.uppercaseChar() }

            binding.textViewBreed.text = displayName
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
