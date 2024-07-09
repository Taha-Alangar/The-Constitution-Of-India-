package com.trinitydevelopers.constitutionofindia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.adapters.PartsAdapter
import com.trinitydevelopers.constitutionofindia.databinding.FragmentPartsBinding
import com.trinitydevelopers.constitutionofindia.model.PartsItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PartsFragment : Fragment() {
    private lateinit var binding:FragmentPartsBinding
    private lateinit var loadingAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentPartsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingAnimationView = view.findViewById(R.id.loadingAnimationView)
        binding.imageView13.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setUpPartOrChapterRecyclerView()
        fetchPartsOrChapter()

        binding.partsRetry.setOnClickListener {
            fetchPartsOrChapter()
        }
    }

    private fun fetchPartsOrChapter() {
        showLoading()

        val call = RetrofitInstance.api.getParts()
        call.enqueue(object : Callback<List<PartsItem>> {
            override fun onResponse(
                call: Call<List<PartsItem>>,
                response: Response<List<PartsItem>>,
            ) {
                if (isAdded) {
                    hideLoading()
                    if (response.isSuccessful) {
                        val parts = response.body()
                        parts?.let {
                            binding.partsRV.visibility = View.VISIBLE
                            binding.partsRV.adapter = PartsAdapter(it) { partItem ->
                                navigateToPartsArticle(partItem)
                            }
                        } ?: showError()
                    } else {
                        showError()
                    }
                }
            }

            override fun onFailure(call: Call<List<PartsItem>>, t: Throwable) {
                if (isAdded) {
                    hideLoading()
                    showError()
                    Toast.makeText(requireContext(), "Check Your Internet Connection...", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateToPartsArticle(partItem: PartsItem) {
        // Create instance of PartsArticleFragment and pass data using Bundle
        val partsArticleFragment = PartsArticleFragment().apply {
            arguments = Bundle().apply {
                putString("partId", partItem.id)
                putString("partTitle", partItem.title)
                putString("partSmallDescription", partItem.small_description)
                // Add any other data you want to pass
            }
        }

        // Perform fragment transaction
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, partsArticleFragment) // Replace fragment_container with your actual container ID
            ?.addToBackStack(null) // Optional: Add fragment to back stack
            ?.commit()
    }
    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.partsRV.visibility = View.GONE
        binding.partsErrorImg.visibility = View.GONE
        binding.partsRetry.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
    }

    private fun showError() {
        binding.partsErrorImg.visibility = View.VISIBLE
        binding.partsRetry.visibility = View.VISIBLE
    }
    private fun setUpPartOrChapterRecyclerView() {
        binding.partsRV.layoutManager=LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
    }
}