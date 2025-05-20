package com.example.to_do_app.ui.calendar

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var calendarAdapter: CalendarDayAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
    private var calendar = Calendar.getInstance()
    private var selectedDate: String = dateFormat.format(Date())
    private var currentDate: String = dateFormat.format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView for tasks
        taskAdapter = TaskAdapter()
        binding.rvTodayTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }

        // Setup RecyclerView for calendar days
        calendarAdapter = CalendarDayAdapter { day ->
            selectedDate = day
            calendarAdapter.setSelectedDate(selectedDate)
            loadTasks(selectedDate)
        }
        binding.llDays.apply {
            layoutManager = GridLayoutManager(context, 7) // 7 cột cho 7 ngày trong tuần
            adapter = calendarAdapter
        }

        // Setup calendar
        updateMonthDisplay()
        setupDays()
        loadTasks(selectedDate)

        // Calendar navigation
        binding.ivPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            setupDays()
        }

        binding.ivNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            setupDays()
        }

        // Date picker dialog
        binding.tvMonth.setOnClickListener {
            showDatePickerDialog()
        }

        // Back button
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateMonthDisplay() {
        binding.tvMonth.text = monthFormat.format(calendar.time)
    }

    private fun setupDays() {
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0: Sun, 1: Mon, ...

        val daysList = mutableListOf<String>()
        // Thêm các ngày trống trước ngày đầu tiên của tháng
        for (i in 0 until firstDayOfWeek) {
            daysList.add("")
        }
        // Thêm các ngày trong tháng
        for (day in 1..daysInMonth) {
            val tempCalendar = calendar.clone() as Calendar
            tempCalendar.set(Calendar.DAY_OF_MONTH, day)
            daysList.add(dateFormat.format(tempCalendar.time))
        }
        // Thêm các ngày trống sau ngày cuối cùng của tháng để đủ hàng
        while (daysList.size % 7 != 0) {
            daysList.add("")
        }

        calendarAdapter.submitList(daysList)
        calendarAdapter.setSelectedDate(selectedDate)
        calendarAdapter.setCurrentDate(currentDate)
    }

    private fun showDatePickerDialog() {
        val selectedCalendar = Calendar.getInstance()
        dateFormat.parse(selectedDate)?.let { selectedCalendar.time = it }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = dateFormat.format(calendar.time)
                updateMonthDisplay()
                setupDays()
                loadTasks(selectedDate)
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadTasks(date: String) {
        val taskDao = DatabaseProvider.getDatabase(requireContext()).taskDao()
        taskDao.getTasksByDate(date).observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            updateEmptyState(tasks.isEmpty())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvTodayTasks.visibility = View.GONE
            binding.tvTodayTasks.text = "No Tasks for $selectedDate"
        } else {
            binding.rvTodayTasks.visibility = View.VISIBLE
            binding.tvTodayTasks.text = "Tasks for $selectedDate"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}