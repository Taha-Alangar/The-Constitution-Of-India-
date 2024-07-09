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
import com.trinitydevelopers.constitutionofindia.adapters.ScheduleAdapter
import com.trinitydevelopers.constitutionofindia.databinding.FragmentSchedulesBinding
import com.trinitydevelopers.constitutionofindia.model.ScheduleItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchedulesFragment : Fragment() {
    private lateinit var binding:FragmentSchedulesBinding
    private lateinit var loadingAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentSchedulesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingAnimationView = view.findViewById(R.id.loadingAnimationView)
        binding.imageView13.setOnClickListener {
            requireActivity().onBackPressed()
        }
        setUpRecyclerView()
        fetchSchedulesTitle()

        binding.scheduleRetry.setOnClickListener {
            fetchSchedulesTitle()
        }
    }

    private fun fetchSchedulesTitle() {
        showLoading()

        val call = RetrofitInstance.api.getSchedules()
        call.enqueue(object : Callback<List<ScheduleItem>> {
            override fun onResponse(
                call: Call<List<ScheduleItem>>,
                response: Response<List<ScheduleItem>>,
            ) {
                if (isAdded) {
                    hideLoading()
                    if (response.isSuccessful) {
                        val schedules = response.body()
                        schedules?.let {
                            binding.scheduleRV.visibility = View.VISIBLE
                            binding.scheduleRV.adapter = ScheduleAdapter(it) { scheduleItem ->
                                navigateToScheduleDetail(scheduleItem)
                            }
                        } ?: showError()
                    } else {
                        showError()
                    }
                }
            }

            override fun onFailure(call: Call<List<ScheduleItem>>, t: Throwable) {
                if (isAdded) {
                    hideLoading()
                    showError()
                    Toast.makeText(requireContext(), "Check Your Internet Connection...", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.scheduleRV.visibility = View.GONE
        binding.scheduleErrorImg.visibility = View.GONE
        binding.scheduleRetry.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
    }

    private fun showError() {
        binding.scheduleErrorImg.visibility = View.VISIBLE
        binding.scheduleRetry.visibility = View.VISIBLE
    }
    private fun navigateToScheduleDetail(it: ScheduleItem) {
        val scheduleFragment=SchedulesDetailFragment().apply {
            arguments = Bundle().apply {
                putString("scheduleId", it.id)
                putString("scheduleTitle", it.title)
                putString("scheduleSmallDescription", it.small_description)
            }
        }
            // Perform fragment transaction
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container,scheduleFragment) // Replace fragment_container with your actual container ID
                ?.addToBackStack(null) // Optional: Add fragment to back stack
                ?.commit()

    }

    private fun setUpRecyclerView() {
        binding.scheduleRV.layoutManager=
            LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }


        }