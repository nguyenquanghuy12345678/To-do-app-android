package com.example.to_do_app.ui.moments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.to_do_app.data.database.DatabaseProvider
import com.example.to_do_app.data.model.Moment
import com.example.to_do_app.databinding.FragmentAddMomentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddMomentFragment : Fragment() {

    private var _binding: FragmentAddMomentBinding? = null
    private val binding get() = _binding!!
    private var selectedImagePath: String? = null
    private var momentId: Int? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.ivMomentImage.setImageBitmap(bitmap)

                // Lưu ảnh vào bộ nhớ nội bộ
                val fileName = "moment_${System.currentTimeMillis()}.jpg"
                val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                selectedImagePath = file.absolutePath
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMomentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kiểm tra nếu là chỉnh sửa
        momentId = arguments?.getInt("momentId")
        if (momentId != null) {
            binding.etTitle.setText(arguments?.getString("title"))
            binding.etDescription.setText(arguments?.getString("description"))
            binding.etDate.setText(arguments?.getString("date"))
            val imagePath = arguments?.getString("imagePath")
            if (imagePath != null && File(imagePath).exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                binding.ivMomentImage.setImageBitmap(bitmap)
                selectedImagePath = imagePath
            }
        }

        // Chọn ảnh
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        // Lưu khoảng khắc
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val date = binding.etDate.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty()) {
                // Định dạng lại date để đảm bảo đúng định dạng yyyy-MM-dd
                val formattedDate = try {
                    val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
                } catch (e: Exception) {
                    date // Nếu không parse được, giữ nguyên giá trị người dùng nhập
                }

                val moment = Moment(
                    id = momentId ?: 0, // Nếu là chỉnh sửa, giữ nguyên id
                    title = title,
                    description = description,
                    date = formattedDate,
                    imagePath = selectedImagePath
                )

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        if (momentId == null) {
                            // Thêm mới
                            DatabaseProvider.getDatabase(requireContext()).momentDao().insert(moment)
                            Toast.makeText(requireContext(), "Moment added successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            // Cập nhật
                            DatabaseProvider.getDatabase(requireContext()).momentDao().update(moment)
                            Toast.makeText(requireContext(), "Moment updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        findNavController().navigateUp()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to save moment: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}