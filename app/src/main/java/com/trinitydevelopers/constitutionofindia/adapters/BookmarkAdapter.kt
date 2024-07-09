    package com.trinitydevelopers.constitutionofindia.adapters

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.view.animation.AnimationUtils
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.trinitydevelopers.constitutionofindia.R
    import com.trinitydevelopers.constitutionofindia.database.bookmark.BookmarkEntity

    class BookmarkAdapter(private val bookmarks: List<BookmarkEntity>
    , private val onBookmarkClick: (BookmarkEntity) -> Unit): RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

        class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.bookmark_title)
            val smallDescription: TextView = itemView.findViewById(R.id.bookmark_smallDescription)
            val description: TextView = itemView.findViewById(R.id.bookmark_description)

            val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

            init {
                // Optionally, set up animation here
                fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
            }
            fun bind(bookmark: BookmarkEntity, onBookmarkClick: (BookmarkEntity) -> Unit) {
                title.text = bookmark.title

                // Handle small description visibility
                if (bookmark.smallDescription.isNullOrEmpty()) {
                    smallDescription.visibility = View.GONE
                } else {
                    smallDescription.visibility = View.VISIBLE
                    smallDescription.text = bookmark.smallDescription
                }


                // Handle description visibility
                if (bookmark.description.isNullOrEmpty()) {
                    description.visibility = View.GONE
                } else {
                    description.visibility = View.VISIBLE
                    description.text = bookmark.description
                }

                // Handle item click
                itemView.setOnClickListener { onBookmarkClick(bookmark) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_bookmark, parent, false)
            return BookmarkViewHolder(view)
        }

        override fun getItemCount(): Int = bookmarks.size

        override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
            val bookmark = bookmarks[position]

            holder.itemView.clearAnimation() // Clear any previous animations
            holder.itemView.startAnimation(holder.fadeInAnimation)
            holder.bind(bookmark, onBookmarkClick)
        }
    }