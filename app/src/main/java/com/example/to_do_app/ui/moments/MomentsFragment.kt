package com.example.to_do_app.ui.moments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.R
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.Moment
import com.example.to_do_app.databinding.FragmentMomentsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MomentsFragment : Fragment() {

    private var _binding: FragmentMomentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var momentsAdapter: MomentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMomentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        momentsAdapter = MomentsAdapter(
            onEditClick = { moment ->
                val bundle = Bundle().apply {
                    putInt("momentId", moment.id)
                    putString("title", moment.title)
                    putString("description", moment.description)
                    putString("date", moment.date)
                    putString("imagePath", moment.imagePath)
                }
                findNavController().navigate(R.id.action_momentsFragment_to_addMomentFragment, bundle)
            },
            onDeleteClick = { moment ->
                CoroutineScope(Dispatchers.Main).launch {
                    DatabaseProvider.getDatabase(requireContext()).momentDao().delete(moment)
                    // Xóa ảnh nếu có
                    moment.imagePath?.let { path ->
                        val file = java.io.File(path)
                        if (file.exists()) file.delete()
                    }
                }
            }
        )
        binding.rvMoments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = momentsAdapter
        }

        // Load moments
        DatabaseProvider.getDatabase(requireContext()).momentDao().getAllMoments().observe(viewLifecycleOwner) { moments ->
            momentsAdapter.submitList(moments)
        }

        // Add new moment
        binding.fabAddMoment.setOnClickListener {
            findNavController().navigate(R.id.action_momentsFragment_to_addMomentFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}