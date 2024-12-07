package com.example.med_finder

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlantAdapter(private var plantList: List<PlantData>) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    fun updateList(newList: List<PlantData>) {
        plantList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.bind(plant)
    }

    override fun getItemCount(): Int = plantList.size

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardImage: ImageView = itemView.findViewById(R.id.cardImage)
        private val cardText: TextView = itemView.findViewById(R.id.cardText)

        fun bind(plant: PlantData) {
            cardText.text = plant.name
            if (!plant.imageUri.isNullOrEmpty()) {
                // Load image from URI using Glide
                Glide.with(itemView.context)
                    .load(Uri.parse(plant.imageUri)) // Pastikan URI valid
                    .into(cardImage)
            } else if (plant.imageRes != 0) {
                // Load image from drawable resource
                cardImage.setImageResource(plant.imageRes)
            } else {
                // Default image
                cardImage.setImageResource(R.drawable.default_image)
            }
        }
    }
}