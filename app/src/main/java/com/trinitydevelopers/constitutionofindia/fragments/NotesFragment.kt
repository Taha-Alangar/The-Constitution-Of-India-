package com.trinitydevelopers.constitutionofindia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.adapters.NotesAdapter
import com.trinitydevelopers.constitutionofindia.database.AppDatabase
import com.trinitydevelopers.constitutionofindia.database.notes.NotesEntity
import com.trinitydevelopers.constitutionofindia.databinding.FragmentNotesBinding
import kotlinx.coroutines.launch


class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    private lateinit var db:AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())
        setupRecyclerView()
        fetchNotes()

        binding.notesBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.floatingActionButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddNotesFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    private fun onNoteClick(note: NotesEntity) {
        val bundle = Bundle().apply {
            putInt("noteId", note.id)
            putString("noteTitle", note.title)
            putString("noteDescription", note.description)
            putString("noteDate", note.date)
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NotesUpdateFragment().apply { arguments = bundle })
            .addToBackStack(null)
            .commit()
    }
    private fun fetchNotes() {
        lifecycleScope.launch {
            val notesList = db.notesDao().getAllNotes()
            binding.notesRV.adapter = NotesAdapter(notesList, ::onNoteClick)
            binding.notesRV.adapter?.notifyDataSetChanged()
        }
    }



private fun setupRecyclerView() {
        binding.notesRV.layoutManager = GridLayoutManager(requireContext(),2)
    }
}