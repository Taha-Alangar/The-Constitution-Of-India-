package com.trinitydevelopers.constitutionofindia.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.database.AppDatabase
import com.trinitydevelopers.constitutionofindia.database.bookmark.BookmarkEntity
import com.trinitydevelopers.constitutionofindia.databinding.FragmentNewsArticlesDetailBinding
import kotlinx.coroutines.launch
import java.util.Locale


class NewsArticlesDetailFragment : Fragment() {
    private lateinit var binding: FragmentNewsArticlesDetailBinding
    private var newsArticleId: String? = null
    private var newsArticleTitle: String? = null
    private var newsArticleDate: String? = null
    private var newsArticleDescription: String? = null
    private lateinit var db: AppDatabase
    private lateinit var textToSpeech: TextToSpeech
    private var isSpeaking = false
    private var speechPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentNewsArticlesDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize database
        db = AppDatabase.getDatabase(requireContext())

        // Set up views
        setupViews()

        // Initialize TextToSpeech
        setupTextToSpeech()

        // Retrieve data from arguments
        retrieveArguments()

        // Set up listeners
        setupListeners()

        // Check bookmark state
        checkBookmarkState()
    }
    private fun setupViews() {
        binding.imageView11.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale("hi", "IN")
                textToSpeech.setSpeechRate(0.8f)
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        activity?.runOnUiThread {
                            binding.newArticleBtnPlay.setImageResource(R.drawable.play_icon)
                            isSpeaking = false
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    private fun retrieveArguments() {
        newsArticleId = arguments?.getString("id")
        newsArticleTitle = arguments?.getString("title")
        newsArticleDate = arguments?.getString("date")
        newsArticleDescription = arguments?.getString("description")

        binding.newArticleDetailTitle.text = newsArticleTitle
        binding.newArticleDetailDate.text = newsArticleDate
        binding.newArticleDetailDescription.text = newsArticleDescription
    }

    private fun setupListeners() {
        binding.newArticleBtnPlay.setOnClickListener {
            togglePlayPause()
        }

        binding.newArticleShareBtn.setOnClickListener {
            shareNewsArticle()
        }

        binding.newsArticleBookmarkIcon.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun togglePlayPause() {
        val text = binding.newArticleDetailDescription.text.toString()
        if (isSpeaking) {
            textToSpeech.stop()
            binding.newArticleBtnPlay.setImageResource(R.drawable.play_icon)
        } else {
            if (speechPosition == 0) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID")
            } else {
                val params = Bundle()
                params.putInt(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speechPosition)
                textToSpeech.speak(text.substring(speechPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
            binding.newArticleBtnPlay.setImageResource(R.drawable.pause_btn)
        }
        isSpeaking = !isSpeaking
    }

    private fun shareNewsArticle() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        // Combine image, title, and description into a single string
        val shareMessage = "${newsArticleTitle}\n${newsArticleDate}\n${newsArticleDescription}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toggleBookmark() {
        if (newsArticleId != null && newsArticleTitle != null && newsArticleDate != null && newsArticleDescription != null) {
            val bookmark = BookmarkEntity(
                entityId = newsArticleId!!,
                title = newsArticleTitle!!,
                smallDescription = newsArticleDate!!,
                type = "news_article",
                description = newsArticleDescription!!
            )

            lifecycleScope.launch {
                val existingBookmark = db.bookmarkDao().getBookmarkById(newsArticleId!!, "news_article")
                if (existingBookmark == null) {
                    db.bookmarkDao().insertBookmark(bookmark)
                    Toast.makeText(requireContext(), "Bookmarked", Toast.LENGTH_SHORT).show()
                    binding.newsArticleBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    db.bookmarkDao().deleteBookmark(existingBookmark)
                    Toast.makeText(requireContext(), "Unbookmarked", Toast.LENGTH_SHORT).show()
                    binding.newsArticleBookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error handling bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBookmarkState() {
        lifecycleScope.launch {
            if (newsArticleId != null) {
                val bookmark = db.bookmarkDao().getBookmarkById(newsArticleId!!, "news_article")
                if (bookmark != null) {
                    binding.newsArticleBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    binding.newsArticleBookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        }
    }

    override fun onDestroyView() {
        textToSpeech.shutdown()
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        textToSpeech.stop()
        isSpeaking = false
        binding.newArticleBtnPlay.setImageResource(R.drawable.play_icon)
    }
}