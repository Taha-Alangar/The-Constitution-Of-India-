package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.database.notes.NotesEntity

class NotesAdapter(private val notesList: List<NotesEntity>,
    val onItemClickListener:(NotesEntity) -> Unit): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title=itemView.findViewById<TextView>(R.id.notesTitle)
        val description=itemView.findViewById<TextView>(R.id.notesDescription)
        val date=itemView.findViewById<TextView>(R.id.notesDate)
        val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

        init {
            // Optionally, set up animation here
            fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_notes, parent, false)
        return NotesViewHolder(view)
    }

    override fun getItemCount(): Int {

        return notesList.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.title.text = notesList[position].title
        holder.description.text = notesList[position].description
        holder.date.text = notesList[position].date
        holder.itemView.clearAnimation() // Clear any previous animations
        holder.itemView.startAnimation(holder.fadeInAnimation)
        holder.itemView.setOnClickListener {
            onItemClickListener(notesList[position])
        }
    }
}