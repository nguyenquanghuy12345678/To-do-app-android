package com.example.to_do_app.ui.travelplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.R
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.TravelPlan
import com.example.to_do_app.databinding.FragmentTravelPlannerBinding
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TravelPlannerFragment : Fragment() {

    private var _binding: FragmentTravelPlannerBinding? = null
    private val binding get() = _binding!!
    private val travelPlans = mutableListOf<TravelPlan>()
    private lateinit var adapter: TravelPlanAdapter
    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTravelPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo Room DAO
        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()

        // Khởi tạo RecyclerView
        adapter = TravelPlanAdapter(travelPlans)
        binding.rvTravelPlans.layoutManager = LinearLayoutManager(context)
        binding.rvTravelPlans.adapter = adapter

        // Load dữ liệu từ Room
        lifecycleScope.launch {
            travelPlanDao.getAllTravelPlans().collectLatest { plans ->
                travelPlans.clear()
                travelPlans.addAll(plans)
                adapter.notifyDataSetChanged()
            }
        }

        // Xử lý nút Add New Plan
        binding.btnAddTravelPlan.setOnClickListener {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("editTravelPlan")
            findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
        }

        // Nhận dữ liệu từ AddTravelPlanFragment (thêm mới)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TravelPlan>("travelPlan")
            ?.observe(viewLifecycleOwner) { travelPlan ->
                lifecycleScope.launch {
                    travelPlanDao.insert(travelPlan)
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("travelPlan")
                }
            }

        // Nhận dữ liệu từ AddTravelPlanFragment (chỉnh sửa du lịch)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TravelPlan>("updateTravelPlan")
            ?.observe(viewLifecycleOwner) { travelPlan ->
                lifecycleScope.launch {
                    travelPlanDao.insert(travelPlan)
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("updateTravelPlan")
                }
            }

        // Thêm mới: Nhận dữ liệu từ AddTravelPlanFragment (thêm mới/chỉnh sửa địa điểm làm việc)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TravelPlan>("updateWorkLocation")
            ?.observe(viewLifecycleOwner) { travelPlan ->
                lifecycleScope.launch {
                    travelPlanDao.insert(travelPlan)
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("updateWorkLocation")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//class TravelPlannerFragment : Fragment() {   TỐT - KO WEATHER API
//
//    private var _binding: FragmentTravelPlannerBinding? = null
//    private val binding get() = _binding!!
//    private val travelPlans = mutableListOf<TravelPlan>()
//    private lateinit var adapter: TravelPlanAdapter
//    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTravelPlannerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo Room DAO
//        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()
//
//        // Khởi tạo RecyclerView
//        adapter = TravelPlanAdapter(travelPlans)
//        binding.rvTravelPlans.layoutManager = LinearLayoutManager(context)
//        binding.rvTravelPlans.adapter = adapter
//
//        // Load dữ liệu từ Room
//        lifecycleScope.launch {
//            travelPlanDao.getAllTravelPlans().collectLatest { plans ->
//                travelPlans.clear()
//                travelPlans.addAll(plans)
//                adapter.notifyDataSetChanged()
//            }
//        }
//
//        // Xử lý nút Add New Plan
//        binding.btnAddTravelPlan.setOnClickListener {
//            // Xóa dữ liệu editTravelPlan để tránh trường hợp chế độ chỉnh sửa
//            findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("editTravelPlan")
//            findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
//        }
//
//        // Nhận dữ liệu từ AddTravelPlanFragment (thêm mới)
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TravelPlan>("travelPlan")
//            ?.observe(viewLifecycleOwner) { travelPlan ->
//                lifecycleScope.launch {
//                    travelPlanDao.insert(travelPlan)
//                    // Xóa dữ liệu sau khi xử lý
//                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("travelPlan")
//                }
//            }
//
//        // Nhận dữ liệu từ AddTravelPlanFragment (chỉnh sửa)
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TravelPlan>("updateTravelPlan")
//            ?.observe(viewLifecycleOwner) { travelPlan ->
//                lifecycleScope.launch {
//                    travelPlanDao.insert(travelPlan) // Room sẽ tự động cập nhật nhờ onConflict
//                    // Xóa dữ liệu sau khi xử lý
//                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<TravelPlan>("updateTravelPlan")
//                }
//            }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class TravelPlannerFragment : Fragment() {
//
//    private var _binding: FragmentTravelPlannerBinding? = null
//    private val binding get() = _binding!!
//    private val travelPlans = mutableListOf<TravelPlan>()
//    private lateinit var adapter: TravelPlanAdapter
//    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTravelPlannerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo Room DAO
//        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()
//
//        // Khởi tạo RecyclerView
//        adapter = TravelPlanAdapter(travelPlans)
//        binding.rvTravelPlans.layoutManager = LinearLayoutManager(context)
//        binding.rvTravelPlans.adapter = adapter
//
//        // Load dữ liệu từ Room
//        lifecycleScope.launch {
//            travelPlanDao.getAllTravelPlans().collectLatest { plans ->
//                travelPlans.clear()
//                travelPlans.addAll(plans)
//                adapter.notifyDataSetChanged()
//            }
//        }
//
//        // Xử lý nút Add New Plan
//        binding.btnAddTravelPlan.setOnClickListener {
//            val action = TravelPlannerFragmentDirections.actionTravelPlannerFragmentToAddTravelPlanFragment(
//                travelPlanJson = null // Chế độ thêm mới
//            )
//            findNavController().navigate(action)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class TravelPlannerFragment : Fragment() {     dùng: savedstateHandle
//
//    private var _binding: FragmentTravelPlannerBinding? = null
//    private val binding get() = _binding!!
//    private val travelPlans = mutableListOf<TravelPlan>()
//    private lateinit var adapter: TravelPlanAdapter
//    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTravelPlannerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo Room DAO
//        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()
//
//        // Khởi tạo RecyclerView
//        adapter = TravelPlanAdapter(travelPlans)
//        binding.rvTravelPlans.layoutManager = LinearLayoutManager(context)
//        binding.rvTravelPlans.adapter = adapter
//
//        // Load dữ liệu từ Room
//        lifecycleScope.launch {
//            travelPlanDao.getAllTravelPlans().collectLatest { plans ->
//                travelPlans.clear()
//                travelPlans.addAll(plans)
//                adapter.notifyDataSetChanged()
//            }
//        }
//
//        // Xử lý nút Add New Plan
//        binding.btnAddTravelPlan.setOnClickListener {
//            findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
//        }
//
//        // Nhận dữ liệu từ AddTravelPlanFragment
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("travelPlan")
//            ?.observe(viewLifecycleOwner) { travelPlanJson ->
//                // Deserialize chuỗi JSON thành TravelPlan
//                val travelPlan = Gson().fromJson(travelPlanJson, TravelPlan::class.java)
//                lifecycleScope.launch {
//                    travelPlanDao.insert(travelPlan)
//                }
//            }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class TravelPlannerFragment : Fragment() {     loi lan 1 tai model
//
//    private var _binding: FragmentTravelPlannerBinding? = null
//    private val binding get() = _binding!!
//    private val travelPlans = mutableListOf<TravelPlan>()
//    private lateinit var adapter: TravelPlanAdapter
//    private lateinit var travelPlanDao: com.example.to_do_app.data.dao.TravelPlanDao
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTravelPlannerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo Room DAO
//        travelPlanDao = DatabaseProvider.getDatabase(requireContext()).travelPlanDao()
//
//        // Khởi tạo RecyclerView
//        adapter = TravelPlanAdapter(travelPlans)
//        binding.rvTravelPlans.layoutManager = LinearLayoutManager(context)
//        binding.rvTravelPlans.adapter = adapter
//
//        // Load dữ liệu từ Room
//        lifecycleScope.launch {
//            travelPlanDao.getAllTravelPlans().collectLatest { plans ->
//                travelPlans.clear()
//                travelPlans.addAll(plans)
//                adapter.notifyDataSetChanged()
//            }
//        }
//
//        // Xử lý nút Add New Plan
//        binding.btnAddTravelPlan.setOnClickListener {
//            findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
//        }
//
//        // Nhận dữ liệu từ AddTravelPlanFragment
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<TravelPlan>("travelPlan")
//            ?.observe(viewLifecycleOwner) { travelPlan ->
//                lifecycleScope.launch {
//                    travelPlanDao.insert(travelPlan)
//                }
//            }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}