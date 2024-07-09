package com.trinitydevelopers.constitutionofindia.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.adapters.NewArticleAdapter
import com.trinitydevelopers.constitutionofindia.databinding.FragmentHomeBinding
import com.trinitydevelopers.constitutionofindia.model.NewsArticlesItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var loadingAnimationView: LottieAnimationView
    private lateinit var scaleUp: Animation
    private lateinit var leftToRight: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        setAnimations()
        setClickListeners()

        setUpImageSlider()
        setUpSpannableText()

        setUpRecyclerView()
        fetchNewsArticles()
        binding.retryImg.setOnClickListener {
            fetchNewsArticles()
        }
    }

    private fun initializeViews() {
        loadingAnimationView = binding.loadingAnimationView
    }

    private fun setAnimations() {
        scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        leftToRight = AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)

        // Start animations for home icons
        binding.apply {
            imgHomePreamble.startAnimation(scaleUp)
            imgHomeParts.startAnimation(scaleUp)
            imgHomeSchedules.startAnimation(scaleUp)
            imgHomeAmendments.startAnimation(scaleUp)
            imgHomeCaseStudy.startAnimation(scaleUp)
            imgHomeBookmark.startAnimation(scaleUp)
        }
    }

    private fun setClickListeners() {
        binding.apply {
            imgHomePreamble.setOnClickListener { navigateToFragment(PreambleFragment()) }
            imgHomeParts.setOnClickListener { navigateToFragment(PartsFragment()) }
            imgHomeSchedules.setOnClickListener { navigateToFragment(SchedulesFragment()) }
            imgHomeAmendments.setOnClickListener { navigateToFragment(AmendmentsFragment()) }
            imgHomeCaseStudy.setOnClickListener { navigateToFragment(CaseStudiesFragment()) }
            imgHomeBookmark.setOnClickListener { navigateToFragment(BookMarkFragment()) }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setUpImageSlider() {
        val imageList = arrayListOf(
            SlideModel(R.drawable.banner_three, ScaleTypes.FIT),
            SlideModel(R.drawable.banner_two, ScaleTypes.FIT),
            SlideModel(R.drawable.banner_one, ScaleTypes.FIT)
        )

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                Toast.makeText(requireContext(), "Selected image $position", Toast.LENGTH_SHORT).show()
            }

            override fun doubleClick(position: Int) {
                // Handle double click if needed
            }
        })
    }

    private fun setUpSpannableText() {
        val text = "सत्यमेव जयते"
        val spannableString = SpannableString(text)

        val saffronColor = ContextCompat.getColor(requireContext(), R.color.Saffron2)
        val blueColor = ContextCompat.getColor(requireContext(), R.color.Deep_Blue)
        val greenColor = ContextCompat.getColor(requireContext(), R.color.Green)

        spannableString.setSpan(ForegroundColorSpan(saffronColor), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(blueColor), 4, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(greenColor), 8, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textView14.text = spannableString
    }

    private fun setUpRecyclerView() {
        binding.newArticlesRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchNewsArticles() {
        showLoading()
        loadingAnimationView.visibility = View.VISIBLE

        val call = RetrofitInstance.api.getNewsArticles()
        call.enqueue(object : Callback<List<NewsArticlesItem>> {
            override fun onResponse(call: Call<List<NewsArticlesItem>>, response: Response<List<NewsArticlesItem>>) {
                loadingAnimationView.visibility = View.GONE
                hideLoading()
                if (response.isSuccessful && isAdded) {

                    val articles = response.body()
                    articles?.let {
                        binding.newArticlesRV.visibility = View.VISIBLE

                        binding.newArticlesRV.adapter = NewArticleAdapter(it) { article ->
                            navigateToNewsArticlesDetail(article)
                        }
                    }
                }else{
                    showError()

                }
            }

            override fun onFailure(call: Call<List<NewsArticlesItem>>, t: Throwable) {
                loadingAnimationView.visibility = View.GONE
                if (isAdded) {
                    hideLoading()
                    showError()
                    Toast.makeText(requireContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.newArticlesRV.visibility = View.GONE
        binding.retryImg.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
    }

    private fun showError() {
        binding.retryImg.visibility = View.VISIBLE
    }
    private fun navigateToNewsArticlesDetail(article: NewsArticlesItem) {
        val newsArticlesDetailFragment = NewsArticlesDetailFragment().apply {
            arguments = Bundle().apply {
                putString("id", article.id)
                putString("title", article.title)
                putString("description", article.small_description)
                putString("date", article.date)
            }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, newsArticlesDetailFragment)
            .addToBackStack(null)
            .commit()
    }
}


