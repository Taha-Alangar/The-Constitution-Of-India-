package com.trinitydevelopers.constitutionofindia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.adapters.BookmarkAdapter
import com.trinitydevelopers.constitutionofindia.database.AppDatabase
import com.trinitydevelopers.constitutionofindia.database.bookmark.BookmarkEntity
import com.trinitydevelopers.constitutionofindia.databinding.FragmentBookMarkBinding
import kotlinx.coroutines.launch

class BookMarkFragment : Fragment() {
    private lateinit var binding:FragmentBookMarkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentBookMarkBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        loadBookmarkedItems()
        binding.imageView13.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setUpRecyclerView() {
        binding.bookmarkRV.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadBookmarkedItems() {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            try {
                val bookmarks = db.bookmarkDao().getAllBookmarks()
                binding.bookmarkRV.adapter = BookmarkAdapter(bookmarks) { bookmark ->
                    navigateToDetailFragment(bookmark)
                }
            } catch (e: Exception) {
                // Handle exception (e.g., show error message)
                Toast.makeText(requireContext(), "Error loading bookmarks", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetailFragment(bookmark: BookmarkEntity) {
        when (bookmark.type) {
            "case_study" -> {
                val caseStudiesFragment = CaseStudiesDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("caseStudyId", bookmark.entityId)
                        putString("caseStudyTitle", bookmark.title)
                        putString("caseStudySmallDescription", bookmark.smallDescription)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, caseStudiesFragment)
                    .addToBackStack(null)
                    .commit()
            }
            "news_article" -> {
                val newsArticleFragment = NewsArticlesDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("id", bookmark.entityId)
                        putString("title", bookmark.title)
                        putString("date", bookmark.smallDescription)
                        putString("description", bookmark.description)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, newsArticleFragment)
                    .addToBackStack(null)
                    .commit()
            }
            "amendment" -> {
                val amendmentsFragment = AmendmentsDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("amendmentId", bookmark.entityId)
                        putString("amendmentTitle", bookmark.title)
                        putString("amendmentSmallDescription", bookmark.smallDescription)
                        putString("amendmentDescription", bookmark.description)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, amendmentsFragment)
                    .addToBackStack(null)
                    .commit()
            }
            "schedule" -> {
                val schedulesFragment = SchedulesDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("scheduleId", bookmark.entityId)
                        putString("scheduleTitle", bookmark.title)
                        putString("scheduleSmallDescription", bookmark.smallDescription)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, schedulesFragment)
                    .addToBackStack(null)
                    .commit()
            }
            "parts" -> {
                val partsFragment = PartsDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("partId", bookmark.entityId)
                        putString("partTitle", bookmark.title)
                        putString("partSmallDescription", bookmark.smallDescription)
                        putString("partDescription", bookmark.description)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, partsFragment)
                    .addToBackStack(null)
                    .commit()
            }
            // Add logic for other bookmark types if needed
        }
    }}