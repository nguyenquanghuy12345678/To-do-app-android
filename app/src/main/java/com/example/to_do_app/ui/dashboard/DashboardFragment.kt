package com.example.to_do_app.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.R
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.Note
import com.example.to_do_app.databinding.FragmentDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteAdapter: DashboardAdapter
    private var allNotes: List<Note> = emptyList() // Lưu trữ toàn bộ ghi chú để lọc
    private var sortOrder: String = "DESC" // Mặc định sắp xếp mới nhất trước

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        noteAdapter = DashboardAdapter()
        binding.rvRecentNotes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
        }

        // Load recent notes
        loadNotes()

        // Setup lời chào
        setupGreeting()

        // Setup tìm kiếm
        setupSearch()

        // Setup bộ lọc
        setupFilter()

        // FAB clicks
        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addNoteFragment)
        }
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addTaskFragment)
        }
    }

    private fun loadNotes() {
        val noteDao = DatabaseProvider.getDatabase(requireContext()).noteDao()
        noteDao.getAllNotes().asLiveData().observe(viewLifecycleOwner) { notes ->
            allNotes = if (sortOrder == "DESC") {
                notes.sortedByDescending { it.createdAt }
            } else {
                notes.sortedBy { it.createdAt }
            }
            noteAdapter.submitList(allNotes)
            updateEmptyState()
        }
    }

    private fun setupGreeting() {
        // Giả sử bạn có dữ liệu người dùng, nếu không thì để mặc định
        val userName = "Quang Huy" // Thay bằng dữ liệu thực tế nếu có
        binding.tvGreeting.text = "Hello, $userName"
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotes(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterNotes(query: String) {
        val filteredNotes = if (query.isEmpty()) {
            allNotes
        } else {
            allNotes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
        }
        noteAdapter.submitList(filteredNotes)
        updateEmptyState()
    }

    private fun setupFilter() {
        val filterOptions = arrayOf("Newest First", "Oldest First")
        binding.ivFilter.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort Notes")
                .setItems(filterOptions) { _, which ->
                    sortOrder = if (which == 0) "DESC" else "ASC"
                    loadNotes() // Tải lại danh sách với thứ tự mới
                }
                .show()
        }
    }

    private fun updateEmptyState() {
        if (noteAdapter.itemCount == 0) {
            binding.rvRecentNotes.visibility = View.GONE
            binding.tvRecentNotes.text = "No Notes Available"
        } else {
            binding.rvRecentNotes.visibility = View.VISIBLE
            binding.tvRecentNotes.text = "Recent Notes"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//package com.example.to_do_app.ui.dashboard
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.asLiveData
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.to_do_app.R
//import com.example.to_do_app.data.database.DatabaseProvider
//import com.example.to_do_app.databinding.FragmentDashboardBinding
//
//class DashboardFragment : Fragment() {
//
//    private var _binding: FragmentDashboardBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var noteAdapter: DashboardAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Setup RecyclerView
//        noteAdapter = DashboardAdapter()
//        binding.rvRecentNotes.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = noteAdapter
//        }
//
//        // Load recent notes
//        val noteDao = DatabaseProvider.getDatabase(requireContext()).noteDao()
//        noteDao.getAllNotes().asLiveData().observe(viewLifecycleOwner) { notes ->
//            noteAdapter.submitList(notes)
//        }
//
//        // FAB clicks
//        binding.fabAddNote.setOnClickListener {
//            findNavController().navigate(R.id.action_dashboardFragment_to_addNoteFragment)
//        }
//        binding.fabAddTask.setOnClickListener {
//            findNavController().navigate(R.id.action_dashboardFragment_to_addTaskFragment)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}