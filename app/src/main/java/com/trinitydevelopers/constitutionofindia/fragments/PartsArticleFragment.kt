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
import com.trinitydevelopers.constitutionofindia.adapters.PartsArticleAdapter
import com.trinitydevelopers.constitutionofindia.databinding.FragmentPartsArticleBinding
import com.trinitydevelopers.constitutionofindia.model.ArticleItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PartsArticleFragment : Fragment() {
    private lateinit var binding:FragmentPartsArticleBinding
    private lateinit var loadingAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentPartsArticleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingAnimationView = view.findViewById(R.id.loadingAnimationView)

        val partId = arguments?.getString("partId")

        binding.imageView15.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val partTitle = arguments?.getString("partTitle")
        val partSmallDescription = arguments?.getString("partSmallDescription")
        binding.textView11.text=partTitle
        binding.textView12.text=partSmallDescription

setUpPartArticleRecyclerView()
        fetchArticles(partId)
    }

    private fun fetchArticles(partId: String?) {

        // Show loading animation
        loadingAnimationView.visibility = View.VISIBLE

        val call=RetrofitInstance.api.getArticles(partId!!)
        call.enqueue(object : Callback<List<ArticleItem>?> {
            override fun onResponse(
                call: Call<List<ArticleItem>?>,
                response: Response<List<ArticleItem>?>,
            ) {
                // Hide loading animation
                loadingAnimationView.visibility = View.GONE
                if (isAdded) {
                    if (response.isSuccessful) {
                        val articles = response.body()
                        if (articles != null) {
                            binding.partArticleRv.adapter = PartsArticleAdapter(articles){
                                navigateToPartsDetail(it)
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<ArticleItem>?>, t: Throwable) {

                // Hide loading animation
                loadingAnimationView.visibility = View.GONE
                if (isAdded) {
                    Toast.makeText(requireContext(),"Failed to fetch articles", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateToPartsDetail(it: ArticleItem) {
        val partsDetailFragment = PartsDetailFragment().apply {
            arguments = Bundle().apply {
                putString("partId", it.id)
                putString("partTitle", it.title)
                putString("partSmallDescription", it.small_description)
                putString("partDescription", it.description)

                // Add any other data you want to pass
            }
        }

        // Perform fragment transaction
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, partsDetailFragment) // Replace fragment_container with your actual container ID
            ?.addToBackStack(null) // Optional: Add fragment to back stack
            ?.commit()

    }

    private fun setUpPartArticleRecyclerView() {
        binding.partArticleRv.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }
}