package com.example.to_do_app.ui.travelplanner

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.to_do_app.R
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.database.UnsplashApiClient
import com.example.to_do_app.data.database.WeatherApiClient
import com.example.to_do_app.data.model.TravelPlan
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TravelPlanAdapter(
    private val travelPlans: MutableList<TravelPlan>
) : RecyclerView.Adapter<TravelPlanAdapter.TravelPlanViewHolder>() {

    inner class TravelPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDestinationImage: ImageView = itemView.findViewById(R.id.ivDestinationImage)
        val tvDestination: TextView = itemView.findViewById(R.id.tvDestination)
        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
        val tvBudget: TextView = itemView.findViewById(R.id.tvBudget)
        val tvTodoList: TextView = itemView.findViewById(R.id.tvTodoList)
        val tvPlacesToVisit: TextView = itemView.findViewById(R.id.tvPlacesToVisit)
        // Thêm mới: TextView để hiển thị thời tiết
        val tvWeather: TextView = itemView.findViewById(R.id.tvWeather)
        val btnUpdate: ImageButton = itemView.findViewById(R.id.btnUpdate)
        val btnViewMap: ImageButton = itemView.findViewById(R.id.btnViewMap)
        val btnShare: ImageButton = itemView.findViewById(R.id.btnShare)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelPlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_travel_plan, parent, false)
        return TravelPlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: TravelPlanViewHolder, position: Int) {
        val travelPlan = travelPlans[position]
        holder.tvDestination.text = travelPlan.destination
        holder.tvDates.text = "${travelPlan.startDate} - ${travelPlan.endDate}"
        holder.tvBudget.text = "Budget: ${travelPlan.budget}"
        holder.tvTodoList.text = "To-Do: ${travelPlan.todoList.joinToString(", ")}"
        holder.tvPlacesToVisit.text = "Places to Visit: ${travelPlan.placesToVisit.joinToString(", ")}"

        // Tải ảnh từ Unsplash
        CoroutineScope(Dispatchers.Main).launch {
            val photoUrl = withContext(Dispatchers.IO) {
                UnsplashApiClient.getPhotoUrl(travelPlan.destination)
            }
            if (photoUrl != null) {
                Glide.with(holder.itemView.context)
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.ivDestinationImage)
            } else {
                holder.ivDestinationImage.setImageResource(R.drawable.placeholder_image)
            }
        }

        // Thêm mới: Tải dữ liệu thời tiết
        CoroutineScope(Dispatchers.Main).launch {
            val weatherInfo = withContext(Dispatchers.IO) {
                WeatherApiClient.getWeather(travelPlan.destination, travelPlan.startDate)
            }
            holder.tvWeather.text = if (travelPlan.isWorkLocation) {
                "Thời tiết làm việc: ${weatherInfo ?: "Unavailable"}"
            } else {
                "Thời tiết: ${weatherInfo ?: "Unavailable"}"
            }
        }

        // Xử lý nút Update
        holder.btnUpdate.setOnClickListener {
            holder.itemView.findNavController().currentBackStackEntry?.savedStateHandle?.set("editTravelPlan", travelPlan)
            holder.itemView.findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
        }

        // Xử lý nút View Map
        holder.btnViewMap.setOnClickListener {
            val query = travelPlan.destination
            val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            holder.itemView.context.startActivity(mapIntent)
        }

        // Xử lý nút Share
        holder.btnShare.setOnClickListener {
            val shareText = """
                Kế hoạch du lịch: ${travelPlan.destination}
                Thời gian: ${travelPlan.startDate} - ${travelPlan.endDate}
                Ngân sách: ${travelPlan.budget}
                Hoạt động: ${travelPlan.todoList.joinToString(", ")}
                Địa điểm tham quan: ${travelPlan.placesToVisit.joinToString(", ")}
            """.trimIndent()
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ kế hoạch du lịch: ${travelPlan.destination}")
            }
            holder.itemView.context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ kế hoạch du lịch"))
        }

        // Xử lý nút Delete
        holder.btnDelete.setOnClickListener {
            Log.d("TravelPlanAdapter", "Deleting travel plan with id: ${travelPlan.id}")
            CoroutineScope(Dispatchers.IO).launch {
                val dao = DatabaseProvider.getDatabase(holder.itemView.context).travelPlanDao()
                dao.deleteById(travelPlan.id)
            }
        }
    }

    override fun getItemCount(): Int = travelPlans.size
}

//class TravelPlanAdapter(    TỐT -> KO WEATHER API
//    private val travelPlans: MutableList<TravelPlan>
//) : RecyclerView.Adapter<TravelPlanAdapter.TravelPlanViewHolder>() {
//
//    inner class TravelPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val ivDestinationImage: ImageView = itemView.findViewById(R.id.ivDestinationImage)
//        val tvDestination: TextView = itemView.findViewById(R.id.tvDestination)
//        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
//        val tvBudget: TextView = itemView.findViewById(R.id.tvBudget)
//        val tvTodoList: TextView = itemView.findViewById(R.id.tvTodoList)
//        val tvPlacesToVisit: TextView = itemView.findViewById(R.id.tvPlacesToVisit)
//        val btnUpdate: ImageButton = itemView.findViewById(R.id.btnUpdate)
//        val btnViewMap: ImageButton = itemView.findViewById(R.id.btnViewMap)
//        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
//
//        val btnShare: ImageButton = itemView.findViewById(R.id.btnShare)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelPlanViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_travel_plan, parent, false)
//        return TravelPlanViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TravelPlanViewHolder, position: Int) {
//        val travelPlan = travelPlans[position]
//        holder.tvDestination.text = travelPlan.destination
//        holder.tvDates.text = "${travelPlan.startDate} - ${travelPlan.endDate}"
//        holder.tvBudget.text = "Budget: ${travelPlan.budget}"
//        holder.tvTodoList.text = "To-Do: ${travelPlan.todoList.joinToString(", ")}"
//        holder.tvPlacesToVisit.text = "Places to Visit: ${travelPlan.placesToVisit.joinToString(", ")}"
//
//        // Tải ảnh từ Unsplash
//        CoroutineScope(Dispatchers.Main).launch {
//            val photoUrl = withContext(Dispatchers.IO) {
//                UnsplashApiClient.getPhotoUrl(travelPlan.destination)
//            }
//            if (photoUrl != null) {
//                Glide.with(holder.itemView.context)
//                    .load(photoUrl)
//                    .placeholder(R.drawable.placeholder_image) // Tạo placeholder nếu cần
//                    .error(R.drawable.error_image) // Tạo error image nếu cần
//                    .into(holder.ivDestinationImage)
//            } else {
//                // Nếu không lấy được ảnh, hiển thị ảnh mặc định
//                holder.ivDestinationImage.setImageResource(R.drawable.placeholder_image)
//            }
//        }
//
//        // Xử lý nút Update
//        holder.btnUpdate.setOnClickListener {
//            holder.itemView.findNavController().currentBackStackEntry?.savedStateHandle?.set("editTravelPlan", travelPlan)
//            holder.itemView.findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
//        }
//
//        // Xử lý nút View Map
//        holder.btnViewMap.setOnClickListener {
//            val query = travelPlan.destination
//            val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            holder.itemView.context.startActivity(mapIntent)
//        }
//
//        // Xử lý nút Delete
//        holder.btnDelete.setOnClickListener {
//            Log.d("TravelPlanAdapter", "Deleting travel plan with id: ${travelPlan.id}")
//            CoroutineScope(Dispatchers.IO).launch {
//                val dao = DatabaseProvider.getDatabase(holder.itemView.context).travelPlanDao()
//                dao.deleteById(travelPlan.id)
//            }
//        }
//
//        // Xử lý nút Share
//        holder.btnShare.setOnClickListener {
//            val shareText = """
//                Kế hoạch du lịch: ${travelPlan.destination}
//                Thời gian: ${travelPlan.startDate} - ${travelPlan.endDate}
//                Ngân sách: ${travelPlan.budget}
//                Hoạt động: ${travelPlan.todoList.joinToString(", ")}
//                Địa điểm tham quan: ${travelPlan.placesToVisit.joinToString(", ")}
//            """.trimIndent()
//            val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                type = "text/plain"
//                putExtra(Intent.EXTRA_TEXT, shareText)
//                putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ kế hoạch du lịch: ${travelPlan.destination}")
//            }
//            holder.itemView.context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ kế hoạch du lịch"))
//        }
//    }
//
//    override fun getItemCount(): Int = travelPlans.size
//}

//class TravelPlanAdapter(             Phiên bản không có Unslash API
//    private val travelPlans: MutableList<TravelPlan>
//) : RecyclerView.Adapter<TravelPlanAdapter.TravelPlanViewHolder>() {
//
//    inner class TravelPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvDestination: TextView = itemView.findViewById(R.id.tvDestination)
//        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
//        val tvBudget: TextView = itemView.findViewById(R.id.tvBudget)
//        val tvTodoList: TextView = itemView.findViewById(R.id.tvTodoList)
//        val tvPlacesToVisit: TextView = itemView.findViewById(R.id.tvPlacesToVisit)
//        val btnUpdate: ImageButton = itemView.findViewById(R.id.btnUpdate)
//        val btnViewMap: ImageButton = itemView.findViewById(R.id.btnViewMap)
//        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelPlanViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_travel_plan, parent, false)
//        return TravelPlanViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TravelPlanViewHolder, position: Int) {
//        val travelPlan = travelPlans[position]
//        holder.tvDestination.text = travelPlan.destination
//        holder.tvDates.text = "${travelPlan.startDate} - ${travelPlan.endDate}"
//        holder.tvBudget.text = "Budget: ${travelPlan.budget}"
//        holder.tvTodoList.text = "To-Do: ${travelPlan.todoList.joinToString(", ")}"
//        holder.tvPlacesToVisit.text = "Places to Visit: ${travelPlan.placesToVisit.joinToString(", ")}"
//
//        // Xử lý nút Update
//        holder.btnUpdate.setOnClickListener {
//            holder.itemView.findNavController().currentBackStackEntry?.savedStateHandle?.set("editTravelPlan", travelPlan)
//            holder.itemView.findNavController().navigate(R.id.action_travelPlannerFragment_to_addTravelPlanFragment)
//        }
//
//        // Xử lý nút View Map
//        holder.btnViewMap.setOnClickListener {
//            val query = travelPlan.destination
//            val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            holder.itemView.context.startActivity(mapIntent)
//        }
//
//        // Xử lý nút Delete
//        holder.btnDelete.setOnClickListener {
//            Log.d("TravelPlanAdapter", "Deleting travel plan with id: ${travelPlan.id}")
//            CoroutineScope(Dispatchers.IO).launch {
//                val dao = DatabaseProvider.getDatabase(holder.itemView.context).travelPlanDao()
//                dao.deleteById(travelPlan.id)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = travelPlans.size
//}