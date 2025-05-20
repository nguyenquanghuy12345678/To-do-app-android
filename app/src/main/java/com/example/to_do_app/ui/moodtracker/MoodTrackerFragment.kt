package com.example.to_do_app.ui.moodtracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.Mood
import com.example.to_do_app.databinding.FragmentMoodTrackerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MoodTrackerFragment : Fragment() {

    private var _binding: FragmentMoodTrackerBinding? = null
    private val binding get() = _binding!!
    private lateinit var moodAdapter: MoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo adapter
        moodAdapter = MoodAdapter()
        binding.rvMoodHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodAdapter
        }

        // Load lịch sử tâm trạng
        DatabaseProvider.getDatabase(requireContext()).moodDao().getAllMoods().observe(viewLifecycleOwner) { moods ->
            moodAdapter.submitList(moods)
        }

        // Load thống kê tâm trạng
        updateMoodStats()

        // Xử lý các nút tâm trạng
        binding.btnHappy.setOnClickListener { saveMood("Happy") }
        binding.btnSad.setOnClickListener { saveMood("Sad") }
        binding.btnNeutral.setOnClickListener { saveMood("Neutral") }
        binding.btnStressed.setOnClickListener { saveMood("Stressed") }
        binding.btnAngry.setOnClickListener { saveMood("Angry") }
    }

    private fun saveMood(moodType: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val note = binding.etMoodNote.text.toString()
        val mood = Mood(date = today, moodType = moodType, note = note)

        CoroutineScope(Dispatchers.Main).launch {
            DatabaseProvider.getDatabase(requireContext()).moodDao().insertMood(mood)
            binding.etMoodNote.text.clear()
            updateMoodStats()
        }
    }

    private fun updateMoodStats() {
        CoroutineScope(Dispatchers.Main).launch {
            val moodDao = DatabaseProvider.getDatabase(requireContext()).moodDao()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -30)
            val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val happyCount = moodDao.getMoodCount("Happy", startDate, endDate)
            val sadCount = moodDao.getMoodCount("Sad", startDate, endDate)
            val neutralCount = moodDao.getMoodCount("Neutral", startDate, endDate)
            val stressedCount = moodDao.getMoodCount("Stressed", startDate, endDate)
            val angryCount = moodDao.getMoodCount("Angry", startDate, endDate)
            val total = happyCount + sadCount + neutralCount + stressedCount + angryCount

            val stats = if (total > 0) {
                "Mood Statistics (Last 30 Days):\n" +
                        "Happy: ${happyCount * 100 / total}%\n" +
                        "Sad: ${sadCount * 100 / total}%\n" +
                        "Neutral: ${neutralCount * 100 / total}%\n" +
                        "Stressed: ${stressedCount * 100 / total}%\n" +
                        "Angry: ${angryCount * 100 / total}%"
            } else {
                "No mood data for the last 30 days."
            }

            binding.tvMoodStats.text = stats
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}