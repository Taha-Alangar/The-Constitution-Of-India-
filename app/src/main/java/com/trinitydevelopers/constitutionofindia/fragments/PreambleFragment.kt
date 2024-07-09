package com.trinitydevelopers.constitutionofindia.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.databinding.FragmentPreambleBinding
import com.trinitydevelopers.constitutionofindia.model.PreambleItem
import com.trinitydevelopers.constitutionofindia.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class PreambleFragment : Fragment() {
    private lateinit var binding: FragmentPreambleBinding
    private lateinit var textToSpeech: TextToSpeech
    private var isSpeaking = false
    private var speechPosition = 0
    private lateinit var loadingAnimationView: LottieAnimationView
    private var isTranslatedToHindi = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPreambleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements and components
        initializeViews()

        // Initialize TextToSpeech
        initializeTextToSpeech()

        // Fetch preamble data
        fetchPreamble()
        binding.btnTranslate.setOnClickListener {
            toggleTranslation()
        }
    }

    private fun toggleTranslation() {
        if (isTranslatedToHindi) {
            // Switch to English
            binding.preambleDescription.text = getString(R.string.preamble_description_english)
            isTranslatedToHindi = false
        } else {
            // Switch to Hindi
            binding.preambleDescription.text = getString(R.string.preamble_description_hindi)
            isTranslatedToHindi = true
        }
    }
    private fun initializeViews() {
        loadingAnimationView = binding.loadingAnimationView
        binding.preambaleBackBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnPlay.setOnClickListener {
            togglePlayPause()
        }
        binding.btnShare.setOnClickListener {
            sharePreamble()
        }
        binding.preambleRetry.setOnClickListener {
            fetchPreamble()
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
                            binding.btnPlay.setImageResource(R.drawable.play_icon)
                            isSpeaking = false
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    private fun fetchPreamble() {
        showLoading()

        val call = RetrofitInstance.api.getPreamble()
        call.enqueue(object : Callback<List<PreambleItem>> {
            override fun onResponse(call: Call<List<PreambleItem>>, response: Response<List<PreambleItem>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val preambleList = response.body()
                    if (preambleList != null && preambleList.isNotEmpty()) {
                        val preamble = preambleList[0]
                        Picasso.get().load(preamble.image).into(binding.preambleImage)
                        binding.preambleTitle.text = preamble.title
                        binding.preambleDescription.text = preamble.description
                    } else {
                        showError()
                    }
                } else {
                    Log.e("PreambleFragment", "Failed to fetch preamble: ${response.code()}")
                    showError()
                }
            }

            override fun onFailure(call: Call<List<PreambleItem>>, t: Throwable) {
                hideLoading()
                Toast.makeText(requireContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                Log.e("PreambleFragment", "Failed to fetch preamble", t)
                showError()
            }
        })
    }

    private fun showLoading() {
        loadingAnimationView.visibility = View.VISIBLE
        binding.preambleScrollView.visibility = View.GONE
        binding.preambleErrorImg.visibility = View.GONE
        binding.preambleRetry.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingAnimationView.visibility = View.GONE
        binding.preambleScrollView.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.preambleErrorImg.visibility = View.VISIBLE
        binding.preambleRetry.visibility = View.VISIBLE


    }


    private fun togglePlayPause() {
        val text = binding.preambleDescription.text.toString()
        if (isSpeaking) {
            textToSpeech.stop()
            binding.btnPlay.setImageResource(R.drawable.play_icon)
        } else {
            if (speechPosition == 0) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID")
            } else {
                val params = Bundle()
                params.putInt(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speechPosition)
                textToSpeech.speak(text.substring(speechPosition), TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
            }
            binding.btnPlay.setImageResource(R.drawable.pause_btn)
        }
        isSpeaking = !isSpeaking
    }

    private fun sharePreamble() {
        val preambleTitle = binding.preambleTitle.text.toString()
        val preambleDescription = binding.preambleDescription.text.toString()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareMessage = "$preambleTitle\n$preambleDescription"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
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
        binding.btnPlay.setImageResource(R.drawable.play_icon)
        super.onPause()
    }
}