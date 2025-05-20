package com.example.to_do_app.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.to_do_app.R
import com.example.to_do_app.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Xử lý nút Profile
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_profileFragment)
        }

        // Xử lý nút Mood Tracker
        binding.btnMoodTracker.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_moodTrackerFragment)
        }

        // Xử lý nút Travel Planner
        binding.btnTravelPlanner.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_travelPlannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}