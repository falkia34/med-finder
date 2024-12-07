package dev.falkia34.medfinder.presentation.ui.home

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dev.falkia34.medfinder.databinding.CardItemBinding
import dev.falkia34.medfinder.domain.entities.Plant

class PlantAdapter(
    val onItemClick: (String) -> Unit, val onItemDelete: (Int) -> Unit,
) : ListAdapter<Plant, PlantAdapter.CardPlantViewHolder>(PlantItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardPlantViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CardPlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardPlantViewHolder, position: Int) {
        val plant = getItem(position)

        holder.bind(plant)
    }

    inner class CardPlantViewHolder(var binding: CardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            val imageBytes = Base64.decode(plant.image, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            binding.imageFeatured.contentDescription = plant.name
            binding.textName.text = plant.name
            binding.textLatinName.text = plant.latinName

            Glide.with(binding.root).load(decodedImage).apply(RequestOptions().override(500, 500))
                .into(binding.imageFeatured)

            binding.card.setOnClickListener {
                onItemClick(plant.id)
            }
        }
    }

    class PlantItemCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem == newItem
    }
}
