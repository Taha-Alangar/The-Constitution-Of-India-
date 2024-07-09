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
import com.trinitydevelopers.constitutionofindia.adapters.CaseStudyAdapter
import com.trinitydevelopers.constitutionofindia.databinding.FragmentCaseStudiesBinding
import com.trinitydevelopers.constitutionofindia.model.CaseStudyItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CaseStudiesFragment : Fragment() {
    private lateinit var binding:FragmentCaseStudiesBinding
    private lateinit var loadingAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentCaseStudiesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingAnimationView = binding.loadingAnimationView
        setUpRecyclerView()
        fetchCaseStudies()
        // Initialize LottieAnimationView

        binding.imageView13.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.retryButton.setOnClickListener {
            fetchCaseStudies()
        }
    }


    private fun fetchCaseStudies() {
        showLoading()

        val call = RetrofitInstance.api.getCaseStudies()
        call.enqueue(object : Callback<List<CaseStudyItem>> {
            override fun onResponse(
                call: Call<List<CaseStudyItem>>,
                response: Response<List<CaseStudyItem>>,
            ) {
                if (isAdded) {
                    hideLoading()
                    if (response.isSuccessful) {
                        val caseStudies = response.body()
                        caseStudies?.let {
                            binding.caseStudyRV.visibility = View.VISIBLE
                            binding.caseStudyRV.adapter = CaseStudyAdapter(it) { item ->
                                navigateToCaseStudiesDetail(item)
                            }
                        } ?: showError()
                    } else {
                        showError()
                    }
                }
            }

            override fun onFailure(call: Call<List<CaseStudyItem>>, t: Throwable) {
                if (isAdded) {
                    hideLoading()
                    showError()
                    Toast.makeText(
                        requireContext(),
                        "Check Your Internet Connection...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.caseStudyRV.visibility = View.GONE
        binding.caseStudyErrorImg.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
    }

    private fun showError() {
        binding.caseStudyErrorImg.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE
    }


    private fun navigateToCaseStudiesDetail(it: CaseStudyItem) {
        val caseStudiesFragment=CaseStudiesDetailFragment().apply {
            arguments = Bundle().apply {
                putString("caseStudyId", it.id)
                putString("caseStudyTitle", it.title)
                putString("caseStudySmallDescription", it.small_description)
            }
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container,caseStudiesFragment) // Replace fragment_container with your actual container ID
            ?.addToBackStack(null) // Optional: Add fragment to back stack
            ?.commit()

    }

    private fun setUpRecyclerView() {
        binding.caseStudyRV.layoutManager=LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
    }
}