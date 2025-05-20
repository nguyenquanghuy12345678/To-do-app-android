package com.example.to_do_app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.combine

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user data
        val userDao = DatabaseProvider.getDatabase(requireContext()).userDao()
        userDao.getUser().asLiveData().observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvName.text = it.name
                binding.tvRole.text = it.role
            }
        }

        // Load stats
        val noteDao = DatabaseProvider.getDatabase(requireContext()).noteDao()
        val taskDao = DatabaseProvider.getDatabase(requireContext()).taskDao()
        noteDao.getAllNotes().combine(taskDao.getRecentTasks()) { notes, tasks ->
            Pair(notes, tasks)
        }.asLiveData().observe(viewLifecycleOwner) { (notes, tasks) ->
            binding.tvTotalNotes.text = notes.size.toString()
            binding.tvTotalTasks.text = tasks.size.toString()
            binding.tvCompletedTasks.text = tasks.count { it.status == "Completed" }.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}