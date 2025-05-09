package com.example.dogapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.graphics.Color


class DogSpinnerAdapter(
    context: Context,
    private val breeds: List<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, breeds) {


    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }


    override fun isEnabled(position: Int): Boolean {
        return position != 0  // Disable the first item
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.setTextColor(if (isEnabled(position)) Color.BLACK else Color.GRAY)
        return view
    }

}
