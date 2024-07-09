package com.trinitydevelopers.constitutionofindia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.database.AppDatabase
import com.trinitydevelopers.constitutionofindia.database.notes.NotesEntity
import com.trinitydevelopers.constitutionofindia.databinding.FragmentAddNotesBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNotesFragment : Fragment() {
private lateinit var binding:FragmentAddNotesBinding
private lateinit var db: AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentAddNotesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db=AppDatabase.getDatabase(requireContext())

        binding.imageView6.setOnClickListener {
            requireActivity().onBackPressed()
        }
        insertNotes()
    }

    private fun insertNotes() {
        binding.saveNotes.setOnClickListener {
            val title = binding.edtAddNotesTitle.text.toString()
            val description = binding.edtAddNotesDescription.text.toString()
            val date = getCurrentDateTime()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val notesEntity = NotesEntity(title = title, description = description, date = date)
                db.notesDao().insertNote(notesEntity)
                requireActivity().onBackPressed()
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a", Locale.getDefault())
        return sdf.format(Date())
    }

}