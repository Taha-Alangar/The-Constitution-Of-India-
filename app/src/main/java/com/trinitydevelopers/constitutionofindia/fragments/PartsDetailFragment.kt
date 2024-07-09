package com.trinitydevelopers.constitutionofindia.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.database.AppDatabase
import com.trinitydevelopers.constitutionofindia.database.bookmark.BookmarkEntity
import com.trinitydevelopers.constitutionofindia.databinding.FragmentPartsDetailBinding
import kotlinx.coroutines.launch
import java.util.Locale

class PartsDetailFragment : Fragment() {
    private lateinit var binding: FragmentPartsDetailBinding
    private var partId: String? = null
    private var partTitle: String? = null
    private var partSmallDescription: String? = null
    private var partDescription: String? = null
    private lateinit var textToSpeech: TextToSpeech
    private var isSpeaking = false
    private var speechPosition = 0

    private lateinit var db: AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPartsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize database
        db = AppDatabase.getDatabase(requireContext())

        // Initialize TextToSpeech
        setupTextToSpeech()

        // Set up views and listeners
        setupViewsAndListeners()

        // Retrieve data from arguments
        retrieveArguments()

        // Set data to views (if any)
        setDataToViews()

        // Check bookmark state
        checkBookmarkState()
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
                            binding.partsPlayeBtn.setImageResource(R.drawable.play_icon)
                            isSpeaking = false
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    private fun setupViewsAndListeners() {
        binding.imageView16.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.partsPlayeBtn.setOnClickListener {
            togglePlayPause()
        }

        binding.partsShareBtn.setOnClickListener {
            sharePart()
        }

        binding.partsBookmarkIcon.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun retrieveArguments() {
        partId = arguments?.getString("partId")
        partTitle = arguments?.getString("partTitle")
        partSmallDescription = arguments?.getString("partSmallDescription")
        partDescription = arguments?.getString("partDescription")
    }

    private fun setDataToViews() {
        binding.partDetailTitle.text = partTitle
        binding.partDetailSmallDescription.text = partSmallDescription
        binding.partDetailDescription.text = partDescription
    }

    private fun togglePlayPause() {
        val text = binding.partDetailDescription.text.toString()
        if (isSpeaking) {
            textToSpeech.stop()
            binding.partsPlayeBtn.setImageResource(R.drawable.play_icon)
        } else {
            if (speechPosition == 0) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID")
            } else {
                val params = Bundle()
                params.putInt(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speechPosition)
                textToSpeech.speak(text.substring(speechPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
            binding.partsPlayeBtn.setImageResource(R.drawable.pause_btn)
        }
        isSpeaking = !isSpeaking
    }

    private fun sharePart() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        // Combine image, title, and description into a single string
        val shareMessage = "${partTitle}\n${partSmallDescription}\n${partDescription}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toggleBookmark() {
        if (partId != null && partTitle != null && partSmallDescription != null && partDescription != null) {
            val bookmark = BookmarkEntity(
                entityId = partId!!,
                title = partTitle!!,
                smallDescription = partSmallDescription!!,
                type = "parts",
                description = partDescription!!
            )

            lifecycleScope.launch {
                val existingBookmark = db.bookmarkDao().getBookmarkById(partId!!, "parts")
                if (existingBookmark == null) {
                    db.bookmarkDao().insertBookmark(bookmark)
                    Toast.makeText(requireContext(), "Bookmarked", Toast.LENGTH_SHORT).show()
                    binding.partsBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    db.bookmarkDao().deleteBookmark(existingBookmark)
                    Toast.makeText(requireContext(), "Unbookmarked", Toast.LENGTH_SHORT).show()
                    binding.partsBookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error handling bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBookmarkState() {
        lifecycleScope.launch {
            if (partId != null) {
                val bookmark = db.bookmarkDao().getBookmarkById(partId!!, "parts")
                if (bookmark != null) {
                    binding.partsBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    binding.partsBookmarkIcon.setImageResource(R.drawable.add_bookmark)
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
        binding.partsPlayeBtn.setImageResource(R.drawable.play_icon)
    }
}