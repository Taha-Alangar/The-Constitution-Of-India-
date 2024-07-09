package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.model.NewsArticlesItem

class NewArticleAdapter(private val articles: List<NewsArticlesItem>
, private val listener: (NewsArticlesItem) -> Unit): RecyclerView.Adapter<NewArticleAdapter.NewArticleViewHolder>() {
    class NewArticleViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val image=itemView.findViewById<ImageView>(R.id.newArticle_image)
        val title=itemView.findViewById<TextView>(R.id.newArticle_title)
        val date=itemView.findViewById<TextView>(R.id.newArticle_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewArticleViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.viewholder_new_articles,parent,false)
        return NewArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: NewArticleViewHolder, position: Int) {
        holder.title.text=articles[position].title
        holder.date.text=articles[position].date
        holder.itemView.setOnClickListener {
            listener(articles[position])
        }


    }
}