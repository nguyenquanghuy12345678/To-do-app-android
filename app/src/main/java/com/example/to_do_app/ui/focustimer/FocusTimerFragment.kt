package com.example.to_do_app.ui.focustimer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.R
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.FocusSession
import com.example.to_do_app.databinding.FragmentFocusTimerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FocusTimerFragment : Fragment() {

    private var _binding: FragmentFocusTimerBinding? = null
    private val binding get() = _binding!!
    private var focusTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var totalTimeInMillis: Long = 0L
    private var timeLeftInMillis: Long = 0L
    private var startTime: Long = 0L
    private lateinit var focusSessionAdapter: FocusSessionAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFocusTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo MediaPlayer
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.notification)

        // Setup RecyclerView cho lịch sử phiên làm việc
        focusSessionAdapter = FocusSessionAdapter()
        binding.rvFocusHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = focusSessionAdapter
        }

        // Load lịch sử phiên làm việc
        DatabaseProvider.getDatabase(requireContext()).focusSessionDao().getAllFocusSessions().observe(viewLifecycleOwner) { sessions ->
            focusSessionAdapter.submitList(sessions)
        }

        // Cập nhật giao diện ban đầu
        updateTimerText()
        binding.btnSkip.isEnabled = false

        // Xử lý nút Start/Pause
        binding.btnStartPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // Xử lý nút Skip
        binding.btnSkip.setOnClickListener {
            focusTimer?.cancel()
            isTimerRunning = false
            binding.btnStartPause.text = "Start"
            binding.btnSkip.isEnabled = false
            // Xóa thời gian đã chạy
            timeLeftInMillis = 0L
            totalTimeInMillis = 0L
            binding.etDays.setText("0")
            binding.etHours.setText("0")
            binding.etMinutes.setText("0")
            binding.etSeconds.setText("0")
            updateTimerText()
        }
    }

    private fun startTimer() {
        // Lấy thời gian từ các ô nhập liệu
        val days = binding.etDays.text.toString().toLongOrNull() ?: 0L
        val hours = binding.etHours.text.toString().toLongOrNull() ?: 0L
        val minutes = binding.etMinutes.text.toString().toLongOrNull() ?: 0L
        val seconds = binding.etSeconds.text.toString().toLongOrNull() ?: 0L

        // Tính tổng thời gian (chuyển tất cả về mili giây)
        totalTimeInMillis = (days * 24 * 60 * 60 + hours * 60 * 60 + minutes * 60 + seconds) * 1000L

        if (totalTimeInMillis <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid time", Toast.LENGTH_SHORT).show()
            return
        }

        timeLeftInMillis = totalTimeInMillis
        startTime = System.currentTimeMillis()

        focusTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                mediaPlayer.start() // Phát âm thanh thông báo
                Toast.makeText(requireContext(), "Focus session completed!", Toast.LENGTH_SHORT).show() // Hiển thị thông báo
                isTimerRunning = false
                binding.btnStartPause.text = "Start"
                binding.btnSkip.isEnabled = false
                saveFocusSession("completed")
                timeLeftInMillis = 0L
                totalTimeInMillis = 0L
                binding.etDays.setText("0")
                binding.etHours.setText("0")
                binding.etMinutes.setText("0")
                binding.etSeconds.setText("0")
                updateTimerText()
            }
        }.start()

        isTimerRunning = true
        binding.btnStartPause.text = "Pause"
        binding.btnSkip.isEnabled = true
    }

    private fun pauseTimer() {
        focusTimer?.cancel()
        isTimerRunning = false
        binding.btnStartPause.text = "Start"
        binding.btnSkip.isEnabled = true
    }

    private fun updateTimerText() {
        val days = (timeLeftInMillis / (1000 * 60 * 60 * 24))
        val hours = (timeLeftInMillis / (1000 * 60 * 60)) % 24
        val minutes = (timeLeftInMillis / (1000 * 60)) % 60
        val seconds = (timeLeftInMillis / 1000) % 60
        binding.tvTimer.text = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
    }

    private fun saveFocusSession(status: String) {
        val startTimeStr = dateFormat.format(Date(startTime))
        val durationMillis = totalTimeInMillis - timeLeftInMillis
        val durationMinutes = (durationMillis / 1000 / 60).toInt()

        val focusSession = FocusSession(
            startTime = startTimeStr,
            durationMinutes = durationMinutes,
            status = status
        )
        CoroutineScope(Dispatchers.Main).launch {
            DatabaseProvider.getDatabase(requireContext()).focusSessionDao().insert(focusSession)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        focusTimer?.cancel()
        mediaPlayer.release()
        _binding = null
    }
}