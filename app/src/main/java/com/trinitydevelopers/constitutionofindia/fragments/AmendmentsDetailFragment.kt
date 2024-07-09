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
import com.trinitydevelopers.constitutionofindia.databinding.FragmentAmendmentsDetailBinding
import kotlinx.coroutines.launch
import java.util.Locale

class AmendmentsDetailFragment : Fragment() {
    private lateinit var binding: FragmentAmendmentsDetailBinding
    private lateinit var db: AppDatabase
    private lateinit var textToSpeech: TextToSpeech
    private var isSpeaking = false
    private var speechPosition = 0

    private var amendmentId: String? = null
    private var amendmentTitle: String? = null
    private var amendmentSmallDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAmendmentsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())
        setupViews()
        setupTextToSpeech()
        retrieveArguments()
        setupListeners()
        checkBookmarkState()
    }
    private fun setupViews() {
        binding.imageView5.setOnClickListener {
            requireActivity().onBackPressed()
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
                            binding.amendmentsBtnPlay.setImageResource(R.drawable.play_icon)
                            isSpeaking = false
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    private fun retrieveArguments() {
        amendmentId = arguments?.getString("amendmentId")
        amendmentTitle = arguments?.getString("amendmentTitle")
        amendmentSmallDescription = arguments?.getString("amendmentSmallDescription")

        binding.amendmentDetailTitle.text = amendmentTitle
        binding.amendmentDetailDescription.text = amendmentSmallDescription
    }

    private fun setupListeners() {
        binding.amendmentsBtnPlay.setOnClickListener {
            togglePlayPause()
        }

        binding.amendmentShareBtn.setOnClickListener {
            shareAmendment()
        }

        binding.amendmentsBookmarkIcon.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun togglePlayPause() {
        val text = binding.amendmentDetailDescription.text.toString()
        if (isSpeaking) {
            textToSpeech.stop()
            binding.amendmentsBtnPlay.setImageResource(R.drawable.play_icon)
        } else {
            if (speechPosition == 0) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID")
            } else {
                val params = Bundle()
                params.putInt(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speechPosition)
                textToSpeech.speak(text.substring(speechPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
            binding.amendmentsBtnPlay.setImageResource(R.drawable.pause_btn)
        }
        isSpeaking = !isSpeaking
    }

    private fun shareAmendment() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        val shareMessage = "${amendmentTitle}\n${amendmentSmallDescription}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toggleBookmark() {
        if (amendmentId != null && amendmentTitle != null && amendmentSmallDescription != null) {
            val bookmark = BookmarkEntity(
                entityId = amendmentId!!,
                title = amendmentTitle!!,
                smallDescription = amendmentSmallDescription!!,
                type = "amendment",
                description = ""
            )

            lifecycleScope.launch {
                val existingBookmark = db.bookmarkDao().getBookmarkById(amendmentId!!, "amendment")
                if (existingBookmark == null) {
                    db.bookmarkDao().insertBookmark(bookmark)
                    Toast.makeText(requireContext(), "Bookmarked", Toast.LENGTH_SHORT).show()
                    binding.amendmentsBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    db.bookmarkDao().deleteBookmark(existingBookmark)
                    Toast.makeText(requireContext(), "Unbookmarked", Toast.LENGTH_SHORT).show()
                    binding.amendmentsBookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error handling bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBookmarkState() {
        lifecycleScope.launch {
            if (amendmentId != null) {
                val bookmark = db.bookmarkDao().getBookmarkById(amendmentId!!, "amendment")
                if (bookmark != null) {
                    binding.amendmentsBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    binding.amendmentsBookmarkIcon.setImageResource(R.drawable.add_bookmark)
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
        binding.amendmentsBtnPlay.setImageResource(R.drawable.play_icon)
    }
}