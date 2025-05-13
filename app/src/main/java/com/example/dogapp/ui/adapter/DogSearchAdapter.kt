package com.example.dogapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dogapp.R
import com.example.dogapp.databinding.ItemNameBinding

class DogSearchAdapter(
    private var items: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DogSearchAdapter.DogViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class DogViewHolder(val binding: ItemNameBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val position = bindingAdapterPosition
            val isSelected = position == selectedPosition

            val displayName = item.replaceFirstChar { it.uppercaseChar() }
            binding.textViewBreed.text = displayName

            val context = binding.root.context
            val drawableRes = if (isSelected) {
                R.drawable.dog_search_selected
            } else {
                R.drawable.dog_search_unselected
            }
            binding.root.background = ContextCompat.getDrawable(context, drawableRes)

            binding.root.setOnClickListener {
                val clickedPosition = bindingAdapterPosition
                if (clickedPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                val prevSelected = selectedPosition

                if (clickedPosition == selectedPosition) {
                    selectedPosition = RecyclerView.NO_POSITION
                    notifyItemChanged(prevSelected)
                    onItemClick(" ")
                } else {
                    selectedPosition = clickedPosition
                    notifyItemChanged(prevSelected)
                    notifyItemChanged(selectedPosition)
                    onItemClick(item)
                }
            }
            println("Bind: $displayName, selected=$isSelected, position=$position, selectedPosition=$selectedPosition")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setSelectedDogName(name: String) {
        println("Entered setSelectedDogName with $name")
        val newPosition = items.indexOf(name)
        if (newPosition != selectedPosition) {
            val prev = selectedPosition
            selectedPosition = newPosition
            if (prev != RecyclerView.NO_POSITION) notifyItemChanged(prev)
            if (newPosition != RecyclerView.NO_POSITION) notifyItemChanged(newPosition)
        }
    }


}
