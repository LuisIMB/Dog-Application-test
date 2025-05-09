package com.example.dogapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogapp.databinding.ItemImageBinding
import com.example.dogapp.databinding.ItemImageGridBinding

class DogImageGridAdapter(
    private val images: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<DogImageGridAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]

        Glide.with(holder.binding.root)
            .load(imageUrl)
            .centerCrop()
            .into(holder.binding.dogImageView)

        holder.binding.root.setOnClickListener {
            onClick(imageUrl)
        }
    }

    override fun getItemCount() = images.size
}
