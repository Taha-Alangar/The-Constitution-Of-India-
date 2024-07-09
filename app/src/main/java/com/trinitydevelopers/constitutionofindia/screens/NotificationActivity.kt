package com.trinitydevelopers.constitutionofindia.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.adapters.NotificationAdapter
import com.trinitydevelopers.constitutionofindia.databinding.ActivityNotificationBinding
import com.trinitydevelopers.constitutionofindia.model.NotificationItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var loadingAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView7.setOnClickListener {
            onBackPressed()
        }
        loadingAnimationView = findViewById(R.id.loadingAnimationView)

        setUpRecyclerView()
        fetchNotification()
        binding.notificationErrorRetryBtn.setOnClickListener {
            fetchNotification()
        }
    }

    private fun fetchNotification() {
        showLoading()
        // Show loading animation
        loadingAnimationView.visibility = View.VISIBLE

        val call = RetrofitInstance.api.getNotifications()
        call.enqueue(object : Callback<List<NotificationItem>> {
            override fun onResponse(
                call: Call<List<NotificationItem>>,
                response: Response<List<NotificationItem>>
            ) {
                hideLoading()

                // Hide loading animation
                loadingAnimationView.visibility = View.GONE
                val notificationList = response.body()
                if (notificationList != null) {
                    binding.notificationRV.visibility = View.VISIBLE

                    val adapter = NotificationAdapter(notificationList)
                    binding.notificationRV.adapter = adapter
                }
                else{
                    showError()

                }
            }

            override fun onFailure(call: Call<List<NotificationItem>>, t: Throwable) {
                // Hide loading animation on failure
                loadingAnimationView.visibility = View.GONE

                hideLoading()
                showError()
                Toast.makeText(this@NotificationActivity, "Check Your Internet Connection" +
                        "", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.notificationRV.visibility = View.GONE
        binding.notificationErrorImage.visibility = View.GONE
        binding.notificationErrorRetryBtn.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
    }

    private fun showError() {
        binding.notificationErrorImage.visibility = View.VISIBLE
        binding.notificationErrorRetryBtn.visibility = View.VISIBLE
    }
    private fun setUpRecyclerView() {
        binding.notificationRV.layoutManager = LinearLayoutManager(this)
    }
}