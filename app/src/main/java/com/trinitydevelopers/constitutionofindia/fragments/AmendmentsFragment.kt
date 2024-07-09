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
import com.trinitydevelopers.constitutionofindia.adapters.AmendmentAdapter
import com.trinitydevelopers.constitutionofindia.databinding.FragmentAmendmentsBinding
import com.trinitydevelopers.constitutionofindia.model.AmendmentsItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AmendmentsFragment : Fragment() {
    private lateinit var binding: FragmentAmendmentsBinding
    private lateinit var loadingAnimationView: LottieAnimationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentAmendmentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingAnimationView = view.findViewById(R.id.loadingAnimationView)

        setUpRecyclerView()
        fetchAmendmentsTitle()

        binding.imageView13.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.retryButton.setOnClickListener {
            fetchAmendmentsTitle()
        }
    }

    private fun fetchAmendmentsTitle() {
        showLoading()

        val call = RetrofitInstance.api.getAmendments()
        call.enqueue(object : Callback<List<AmendmentsItem>> {
            override fun onResponse(
                call: Call<List<AmendmentsItem>>,
                response: Response<List<AmendmentsItem>>
            ) {
                if (isAdded) {
                    hideLoading()
                    if (response.isSuccessful) {
                        val amendments = response.body()
                        amendments?.let {
                            binding.amendmentRV.visibility = View.VISIBLE
                            binding.amendmentRV.adapter = AmendmentAdapter(it) { item ->
                                navigateToAmendmentDetail(item)
                            }
                        }
                    } else {
                        showError()
                    }
                }
            }

            override fun onFailure(call: Call<List<AmendmentsItem>>, t: Throwable) {
                if (isAdded) {
                    hideLoading()
                    showError()
                    Toast.makeText(requireContext(), "Check Your Internet Connection...", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun navigateToAmendmentDetail(item: AmendmentsItem) {
        val amendmentFragment = AmendmentsDetailFragment().apply {
            arguments = Bundle().apply {
                putString("amendmentId", item.id)
                putString("amendmentTitle", item.title)
                putString("amendmentSmallDescription", item.small_description)
            }
        }
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, amendmentFragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.amendmentRV.visibility = View.GONE
        binding.errorImage.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
    }

    private fun showError() {
        binding.errorImage.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE
    }
    private fun setUpRecyclerView() {
        binding.amendmentRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
}