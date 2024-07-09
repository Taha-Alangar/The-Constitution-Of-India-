package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.model.AmendmentsItem

class AmendmentAdapter(private val amendments: List<AmendmentsItem>
, private val onItemClick : (AmendmentsItem) -> Unit): RecyclerView.Adapter<AmendmentAdapter.AmendmentViewHolder>() {
    class AmendmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.amendment_title)
         val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

        init {
            // Optionally, set up animation here
            fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmendmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_amendments, parent, false)
         return AmendmentViewHolder(view)

    }

    override fun getItemCount(): Int {
        return amendments.size
    }

    override fun onBindViewHolder(holder: AmendmentViewHolder, position: Int) {
        holder.title.text=amendments[position].title
        holder.itemView.clearAnimation() // Clear any previous animations
        holder.itemView.startAnimation(holder.fadeInAnimation)
        holder.itemView.setOnClickListener {
            onItemClick(amendments[position])
        }
    }
}