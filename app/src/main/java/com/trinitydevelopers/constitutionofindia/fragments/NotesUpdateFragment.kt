package com.trinitydevelopers.constitutionofindia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.trinitydevelopers.constitutionofindia.database.AppDatabase
import com.trinitydevelopers.constitutionofindia.databinding.FragmentNotesUpdateDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NotesUpdateFragment : Fragment() {
    private lateinit var binding:FragmentNotesUpdateDetailBinding
    private lateinit var db: AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentNotesUpdateDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db = AppDatabase.getDatabase(requireContext())
        val noteId = arguments?.getInt("noteId") ?: return
        val noteTitle = arguments?.getString("noteTitle") ?: return
        val noteDescription = arguments?.getString("noteDescription") ?: return


        binding.imageView3.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.edtNotedUpdateTitle.setText(noteTitle)
        binding.edtNotedUpdateDescription.setText(noteDescription)

        binding.updateNotes.setOnClickListener {
            val title = binding.edtNotedUpdateTitle.text.toString()
            val description = binding.edtNotedUpdateDescription.text.toString()
            val date = getCurrentDateTime()

            lifecycleScope.launch {
                db.notesDao().updateNote(noteId, title, description)
                requireActivity().onBackPressed()
            }
        }
    }
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a", Locale.getDefault())
        return sdf.format(Date())
    }
}