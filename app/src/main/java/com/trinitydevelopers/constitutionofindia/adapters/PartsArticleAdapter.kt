package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.model.ArticleItem

class PartsArticleAdapter(private val parts: List<ArticleItem>
, private val onItemClick: (ArticleItem) -> Unit): RecyclerView.Adapter<PartsArticleAdapter.PartsArticleViewHolder>() {
    class PartsArticleViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val title=itemView.findViewById<TextView>(R.id.partsArticle_title)
        val description=itemView.findViewById<TextView>(R.id.partsArticle_description)
        val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

        init {
            // Optionally, set up animation here
            fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartsArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_parts_article, parent, false)
         return PartsArticleViewHolder(view)

    }

    override fun getItemCount(): Int {
        return parts.size
    }

    override fun onBindViewHolder(holder: PartsArticleViewHolder, position: Int) {
        val partItem = parts[position]
        holder.title.text=parts[position].title
        holder.description.text=parts[position].small_description
        holder.itemView.clearAnimation() // Clear any previous animations
        holder.itemView.startAnimation(holder.fadeInAnimation)
        holder.itemView.setOnClickListener {

            onItemClick(partItem)
        }
    }
}