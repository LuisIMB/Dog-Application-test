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
        fun bind(item: String, isSelected: Boolean) {
            val displayName = item.replaceFirstChar { it.uppercaseChar() }

            binding.textViewBreed.text = displayName

            //binding.root.setBackgroundDrawable(R.drawable.dog_search_selected)
/*            binding.root.setBackgroundColor(
                if (isSelected) Color.RED else Color.TRANSPARENT
            )*/
            val context = binding.root.context
            val drawableRes = if (isSelected) {
                R.drawable.dog_search_selected
            } else {
                R.drawable.dog_search_unselected
            }
            binding.root.background = ContextCompat.getDrawable(context, drawableRes)

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition

                if (adapterPosition == selectedPosition) {
                    // Deselect if clicked again
                    selectedPosition = RecyclerView.NO_POSITION
                    notifyItemChanged(previousPosition)
                    onItemClick(" ")
                } else {
                    // Select new item
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClick(item)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(items[position], position==selectedPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    override fun getItemCount(): Int = items.size
}
