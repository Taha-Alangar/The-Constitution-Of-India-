package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.model.PartsItem

class PartsAdapter(private val parts: List<PartsItem>
                   , private val onItemClick: (PartsItem) -> Unit): RecyclerView.Adapter<PartsAdapter.PartsViewHolder>() {
    class PartsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val title=itemView.findViewById<TextView>(R.id.parts_title)
        val description=itemView.findViewById<TextView>(R.id.parts_description)
        val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

        init {
            // Optionally, set up animation here
            fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_parts, parent, false)
         return PartsViewHolder(view)

    }

    override fun getItemCount(): Int {
        return parts.size
    }

    override fun onBindViewHolder(holder: PartsViewHolder, position: Int) {
        val partItem = parts[position]
        holder.title.text = partItem.title
        holder.description.text = partItem.small_description
        holder.itemView.clearAnimation() // Clear any previous animations
        holder.itemView.startAnimation(holder.fadeInAnimation)
        holder.itemView.setOnClickListener {
            onItemClick(partItem)
        }
    }
}