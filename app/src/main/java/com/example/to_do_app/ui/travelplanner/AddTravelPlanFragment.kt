package com.example.to_do_app.ui.travelplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.databinding.FragmentAddTravelPlanBinding
import com.example.to_do_app.data.model.TravelPlan
import com.google.gson.Gson
import kotlinx.coroutines.launch
class AddTravelPlanFragment : Fragment() {   // CÓ WEATHER API

    private var _binding: FragmentAddTravelPlanBinding? = null
    private val binding get() = _binding!!
    private var existingTravelPlan: TravelPlan? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTravelPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy dữ liệu từ SavedStateHandle (chế độ chỉnh sửa)
        existingTravelPlan = findNavController().previousBackStackEntry?.savedStateHandle?.get<TravelPlan>("editTravelPlan")
        existingTravelPlan?.let { plan ->
            binding.etDestination.setText(plan.destination)
            binding.etStartDate.setText(plan.startDate)
            binding.etEndDate.setText(plan.endDate)
            binding.etBudget.setText(plan.budget.toString())
            binding.etTodoList.setText(plan.todoList.joinToString(", "))
            binding.etPlacesToVisit.setText(plan.placesToVisit.joinToString(", "))
            binding.tvAddTravelPlanTitle.text = "Edit Travel Plan"
            binding.btnSaveTravelPlan.text = "Update Plan"
            // Thêm mới: Kiểm tra nếu là địa điểm làm việc
            if (plan.isWorkLocation) {
                binding.rgPlanType.check(binding.rbWork.id)
                binding.etBudget.visibility = View.GONE
                binding.etTodoList.visibility = View.GONE
                binding.etPlacesToVisit.visibility = View.GONE
            }
        }

        // Thêm mới: Xử lý thay đổi loại kế hoạch
        binding.rgPlanType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbWork.id) { // Làm việc
                binding.etBudget.visibility = View.GONE
                binding.etTodoList.visibility = View.GONE
                binding.etPlacesToVisit.visibility = View.GONE
            } else { // Du lịch
                binding.etBudget.visibility = View.VISIBLE
                binding.etTodoList.visibility = View.VISIBLE
                binding.etPlacesToVisit.visibility = View.VISIBLE
            }
        }

        // Xử lý nút Save/Update Plan
        binding.btnSaveTravelPlan.setOnClickListener {
            val destination = binding.etDestination.text.toString()
            val startDate = binding.etStartDate.text.toString()
            val endDate = binding.etEndDate.text.toString()
            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
            val todoList = binding.etTodoList.text.toString().split(",").map { it.trim() }
            val placesToVisit = binding.etPlacesToVisit.text.toString().split(",").map { it.trim() }
            // Thêm mới: Xác định loại kế hoạch
            val isWork = binding.rgPlanType.checkedRadioButtonId == binding.rbWork.id

            if (destination.isNotEmpty() && startDate.isNotEmpty()) {
                val travelPlan = if (existingTravelPlan != null) {
                    TravelPlan(
                        id = existingTravelPlan!!.id,
                        destination = destination,
                        startDate = startDate,
                        endDate = endDate,
                        budget = if (isWork) 0.0 else budget,
                        todoList = if (isWork) emptyList() else todoList,
                        placesToVisit = if (isWork) emptyList() else placesToVisit,
                        isWorkLocation = isWork
                    )
                } else {
                    TravelPlan(
                        destination = destination,
                        startDate = startDate,
                        endDate = endDate,
                        budget = if (isWork) 0.0 else budget,
                        todoList = if (isWork) emptyList() else todoList,
                        placesToVisit = if (isWork) emptyList() else placesToVisit,
                        isWorkLocation = isWork
                    )
                }

                // Thêm mới: Sử dụng key khác cho địa điểm làm việc
                val key = if (isWork) "updateWorkLocation" else if (existingTravelPlan != null) "updateTravelPlan" else "travelPlan"
                findNavController().previousBackStackEntry?.savedStateHandle?.set(key, travelPlan)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


//class AddTravelPlanFragment : Fragment() {    -> CHẠY TỐT - CHƯA WEATHER
//
//    private var _binding: FragmentAddTravelPlanBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao
//    private var existingTravelPlan: TravelPlan? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddTravelPlanBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo Room DAO
//        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()
//
//        // Lấy dữ liệu từ SavedStateHandle (chế độ chỉnh sửa)
//        existingTravelPlan = findNavController().previousBackStackEntry?.savedStateHandle?.get<TravelPlan>("editTravelPlan")
//        existingTravelPlan?.let { plan ->
//            binding.etDestination.setText(plan.destination)
//            binding.etStartDate.setText(plan.startDate)
//            binding.etEndDate.setText(plan.endDate)
//            binding.etBudget.setText(plan.budget.toString())
//            binding.etTodoList.setText(plan.todoList.joinToString(", "))
//            binding.etPlacesToVisit.setText(plan.placesToVisit.joinToString(", "))
//            binding.tvAddTravelPlanTitle.text = "Edit Travel Plan"
//            binding.btnSaveTravelPlan.text = "Update Plan"
//        }
//
//        // Xử lý nút Save/Update Plan
//        binding.btnSaveTravelPlan.setOnClickListener {
//            val destination = binding.etDestination.text.toString()
//            val startDate = binding.etStartDate.text.toString()
//            val endDate = binding.etEndDate.text.toString()
//            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
//            val todoList = binding.etTodoList.text.toString().split(",").map { it.trim() }
//            val placesToVisit = binding.etPlacesToVisit.text.toString().split(",").map { it.trim() }
//
//            if (destination.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
//                val travelPlan = if (existingTravelPlan != null) {
//                    // Chế độ chỉnh sửa: giữ nguyên id
//                    TravelPlan(
//                        id = existingTravelPlan!!.id,
//                        destination = destination,
//                        startDate = startDate,
//                        endDate = endDate,
//                        budget = budget,
//                        todoList = todoList,
//                        placesToVisit = placesToVisit
//                    )
//                } else {
//                    // Chế độ thêm mới
//                    TravelPlan(
//                        destination = destination,
//                        startDate = startDate,
//                        endDate = endDate,
//                        budget = budget,
//                        todoList = todoList,
//                        placesToVisit = placesToVisit
//                    )
//                }
//
//                // Phân biệt chế độ thêm mới và chỉnh sửa
//                val key = if (existingTravelPlan != null) "updateTravelPlan" else "travelPlan"
//                findNavController().previousBackStackEntry?.savedStateHandle?.set(key, travelPlan)
//                findNavController().popBackStack()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class AddTravelPlanFragment : Fragment() {
//
//    private var _binding: FragmentAddTravelPlanBinding? = null
//    private val binding get() = _binding!!
//    private val args: AddTravelPlanFragmentArgs by navArgs()
//    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao
//    private var existingTravelPlan: TravelPlan? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddTravelPlanBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo Room DAO
//        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()
//
//        // Kiểm tra nếu có dữ liệu truyền vào (chế độ chỉnh sửa)
//        args.travelPlanJson?.let { json ->
//            existingTravelPlan = Gson().fromJson(json, TravelPlan::class.java)
//            existingTravelPlan?.let { plan ->
//                binding.etDestination.setText(plan.destination)
//                binding.etStartDate.setText(plan.startDate)
//                binding.etEndDate.setText(plan.endDate)
//                binding.etBudget.setText(plan.budget.toString())
//                binding.etTodoList.setText(plan.todoList.joinToString(", "))
//                binding.etPlacesToVisit.setText(plan.placesToVisit.joinToString(", "))
//                binding.tvAddTravelPlanTitle.text = "Edit Travel Plan"
//                binding.btnSaveTravelPlan.text = "Update Plan"
//            }
//        }
//
//        // Xử lý nút Save/Update Plan
//        binding.btnSaveTravelPlan.setOnClickListener {
//            val destination = binding.etDestination.text.toString()
//            val startDate = binding.etStartDate.text.toString()
//            val endDate = binding.etEndDate.text.toString()
//            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
//            val todoList = binding.etTodoList.text.toString().split(",").map { it.trim() }
//            val placesToVisit = binding.etPlacesToVisit.text.toString().split(",").map { it.trim() }
//
//            if (destination.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
//                val travelPlan = if (existingTravelPlan != null) {
//                    // Chế độ chỉnh sửa: giữ nguyên id
//                    TravelPlan(
//                        id = existingTravelPlan!!.id,
//                        destination = destination,
//                        startDate = startDate,
//                        endDate = endDate,
//                        budget = budget,
//                        todoList = todoList,
//                        placesToVisit = placesToVisit
//                    )
//                } else {
//                    // Chế độ thêm mới
//                    TravelPlan(
//                        destination = destination,
//                        startDate = startDate,
//                        endDate = endDate,
//                        budget = budget,
//                        todoList = todoList,
//                        placesToVisit = placesToVisit
//                    )
//                }
//
//                lifecycleScope.launch {
//                    travelPlanDao.insert(travelPlan) // Room sẽ tự động cập nhật nếu id đã tồn tại
//                    findNavController().popBackStack()
//                }
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}


//class AddTravelPlanFragment : Fragment() {         ok: thiếu update
//
//    private var _binding: FragmentAddTravelPlanBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddTravelPlanBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Xử lý nút Save Plan
//        binding.btnSaveTravelPlan.setOnClickListener {
//            val destination = binding.etDestination.text.toString()
//            val startDate = binding.etStartDate.text.toString()
//            val endDate = binding.etEndDate.text.toString()
//            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
//            val todoList = binding.etTodoList.text.toString().split(",").map { it.trim() }
//            val placesToVisit = binding.etPlacesToVisit.text.toString().split(",").map { it.trim() }
//
//            if (destination.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
//                val travelPlan = TravelPlan(
//                    destination = destination,
//                    startDate = startDate,
//                    endDate = endDate,
//                    budget = budget,
//                    todoList = todoList,
//                    placesToVisit = placesToVisit
//                )
//                // Chuyển TravelPlan thành chuỗi JSON
//                val travelPlanJson = Gson().toJson(travelPlan)
//                findNavController().previousBackStackEntry?.savedStateHandle?.set("travelPlan", travelPlanJson)
//                findNavController().popBackStack()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class AddTravelPlanFragment : Fragment() {            Mau 1 loi: model
//
//    private var _binding: FragmentAddTravelPlanBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddTravelPlanBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Xử lý nút Save Plan
//        binding.btnSaveTravelPlan.setOnClickListener {
//            val destination = binding.etDestination.text.toString()
//            val startDate = binding.etStartDate.text.toString()
//            val endDate = binding.etEndDate.text.toString()
//            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
//            val todoList = binding.etTodoList.text.toString().split(",").map { it.trim() }
//            val placesToVisit = binding.etPlacesToVisit.text.toString().split(",").map { it.trim() }
//
//            if (destination.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
//                val travelPlan = TravelPlan(destination = destination, startDate = startDate, endDate = endDate, budget = budget, todoList = todoList, placesToVisit = placesToVisit)
//                findNavController().previousBackStackEntry?.savedStateHandle?.set("travelPlan", travelPlan)
//                findNavController().popBackStack()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}