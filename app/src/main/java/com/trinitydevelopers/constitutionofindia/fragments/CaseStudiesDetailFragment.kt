package com.trinitydevelopers.constitutionofindia.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.trinitydevelopers.constitutionofindia.databinding.FragmentCaseStudiesDetailBinding
import kotlinx.coroutines.launch
import java.util.Locale


class CaseStudiesDetailFragment : Fragment() {
    private lateinit var binding: FragmentCaseStudiesDetailBinding
    private lateinit var db: AppDatabase
    private var caseStudyId: String? = null
    private var caseStudyTitle: String? = null
    private var caseStudySmallDescription: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCaseStudiesDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize database
        // Initialize database
        db = AppDatabase.getDatabase(requireContext())

        // Set up views
        setupViews()

        // Retrieve data from arguments
        retrieveArguments()

        // Set up listeners
        setupListeners()

        // Check bookmark state
        checkBookmarkState()
    }
    private fun setupViews() {
        binding.imageView9.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun retrieveArguments() {
        caseStudyId = arguments?.getString("caseStudyId")
        caseStudyTitle = arguments?.getString("caseStudyTitle")
        caseStudySmallDescription = arguments?.getString("caseStudySmallDescription")

        binding.caseStudyTitle.text = caseStudyTitle
        binding.caseStudyDescription.text = caseStudySmallDescription
    }

    private fun setupListeners() {
        binding.caseStudyShareBtn.setOnClickListener {
            shareCaseStudy()
        }

        binding.bookmarkIcon.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun shareCaseStudy() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        val shareMessage = "${caseStudyTitle}\n${caseStudySmallDescription}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toggleBookmark() {
        if (caseStudyId != null && caseStudyTitle != null && caseStudySmallDescription != null) {
            val bookmark = BookmarkEntity(
                entityId = caseStudyId!!,
                title = caseStudyTitle!!,
                smallDescription = caseStudySmallDescription!!,
                type = "case_study",
                description = ""
            )

            lifecycleScope.launch {
                val existingBookmark = db.bookmarkDao().getBookmarkById(caseStudyId!!, "case_study")
                if (existingBookmark == null) {
                    db.bookmarkDao().insertBookmark(bookmark)
                    Toast.makeText(requireContext(), "Bookmarked", Toast.LENGTH_SHORT).show()
                    binding.bookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    db.bookmarkDao().deleteBookmark(existingBookmark)
                    Toast.makeText(requireContext(), "Unbookmarked", Toast.LENGTH_SHORT).show()
                    binding.bookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error handling bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBookmarkState() {
        lifecycleScope.launch {
            if (caseStudyId != null) {
                val bookmark = db.bookmarkDao().getBookmarkById(caseStudyId!!, "case_study")
                if (bookmark != null) {
                    binding.bookmarkIcon.setImageResource(R.drawable.bookmark_added_icon)
                } else {
                    binding.bookmarkIcon.setImageResource(R.drawable.add_bookmark)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}