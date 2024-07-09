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
import com.trinitydevelopers.constitutionofindia.databinding.FragmentSchedulesDetailBinding
import kotlinx.coroutines.launch
import java.util.Locale

class SchedulesDetailFragment : Fragment() {
    private lateinit var binding: FragmentSchedulesDetailBinding
    private lateinit var db: AppDatabase
    private var scheduleId: String? = null
    private var scheduleTitle: String? = null
    private var scheduleSmallDescription: String? = null
    private lateinit var textToSpeech: TextToSpeech
    private var isSpeaking = false
    private var speechPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentSchedulesDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        // Initialize UI elements and components
        initializeViews()

        // Initialize TextToSpeech
        initializeTextToSpeech()

        // Retrieve data from arguments and set to views
        retrieveAndSetData()

        // Set up click listeners
        setUpClickListeners()

        // Check and update bookmark state
        checkBookmarkState()
    }
    private fun initializeViews() {
        binding.imageView17.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale("hi", "IN")
                textToSpeech.setSpeechRate(0.8f)
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        activity?.runOnUiThread {
                            binding.scheduleBtnPlay.setImageResource(R.drawable.play_icon)
                            isSpeaking = false
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    private fun retrieveAndSetData() {
        scheduleId = arguments?.getString("scheduleId")
        scheduleTitle = arguments?.getString("scheduleTitle")
        scheduleSmallDescription = arguments?.getString("scheduleSmallDescription")

        binding.scheduleDetailTitle.text = scheduleTitle
        binding.scheduleDetailDescription.text = scheduleSmallDescription
    }

    private fun setUpClickListeners() {
        binding.scheduleBtnPlay.setOnClickListener {
            togglePlayPause()
        }

        binding.scheduleBtnShare.setOnClickListener {
            shareSchedule()
        }

        binding.scheduleBookmarkIcon.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun togglePlayPause() {
        val text = binding.scheduleDetailDescription.text.toString()
        if (isSpeaking) {
            textToSpeech.stop()
            binding.scheduleBtnPlay.setImageResource(R.drawable.play_icon)
        } else {
            if (speechPosition == 0) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID")
            } else {
                val params = Bundle()
                params.putInt(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speechPosition)
                textToSpeech.speak(text.substring(speechPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
            binding.scheduleBtnPlay.setImageResource(R.drawable.pause_btn)
        }
        isSpeaking = !isSpeaking
    }

    private fun shareSchedule() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        // Combine image, title, and description into a single string
        val shareMessage = "${scheduleTitle}\n${scheduleSmallDescription}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toggleBookmark() {
        if (scheduleId != null && scheduleTitle != null && scheduleSmallDescription != null) {
            val bookmark = BookmarkEntity(
                entityId = scheduleId!!,
                title = scheduleTitle!!,
                smallDescription = scheduleSmallDescription!!,
                type = "schedule",
                description = ""
            )

            lifecycleScope.launch {
                val existingBookmark = db.bookmarkDao().getBookmarkById(scheduleId!!, "schedule")
                if (existingBookmark == null) {
                    db.bookmarkDao().insertBookmark(bookmark)
                    Toast.makeText(requireContext(), "Bookmarked", Toast.LENGTH_SHORT).show()
                    binding.scheduleBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    db.bookmarkDao().deleteBookmark(existingBookmark)
                    Toast.makeText(requireContext(), "Unbookmarked", Toast.LENGTH_SHORT).show()
                    binding.scheduleBookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error handling bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBookmarkState() {
        lifecycleScope.launch {
            if (scheduleId != null) {
                val bookmark = db.bookmarkDao().getBookmarkById(scheduleId!!, "schedule")
                if (bookmark != null) {
                    binding.scheduleBookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    binding.scheduleBookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        }
    }

    override fun onDestroyView() {
        // Shutdown TextToSpeech engine
        textToSpeech.shutdown()
        super.onDestroyView()
    }

    override fun onPause() {
        // Stop TextToSpeech when fragment is paused
        textToSpeech.stop()
        isSpeaking = false
        binding.scheduleBtnPlay.setImageResource(R.drawable.play_icon)
        super.onPause()
    }
    }
