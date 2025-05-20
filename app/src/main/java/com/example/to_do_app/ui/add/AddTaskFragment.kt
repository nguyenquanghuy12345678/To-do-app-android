package com.example.to_do_app.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.Task
import com.example.to_do_app.databinding.FragmentAddTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy selectedDate từ Bundle
        val selectedDate = arguments?.getString("selectedDate")
        if (selectedDate != null) {
            binding.etDate.setText(selectedDate)
        }

        binding.btnSaveTask.setOnClickListener {
            val title = binding.etTaskTitle.text.toString()
            val description = binding.etTaskDescription.text.toString()
            val startTime = binding.etStartTime.text.toString()
            val endTime = binding.etEndTime.text.toString()
            val date = binding.etDate.text.toString()
            val status = binding.etStatus.text.toString()

            val task = Task(
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                date = date,
                status = status
            )

            CoroutineScope(Dispatchers.IO).launch {
                DatabaseProvider.getDatabase(requireContext()).taskDao().insert(task)
                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}