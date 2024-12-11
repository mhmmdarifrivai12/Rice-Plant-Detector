package com.bangkit.ripad.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ripad.R
import com.bangkit.ripad.databinding.FragmentHomeBinding
import com.bangkit.ripad.ui.detaildiseases.DetailDiseaseActivity
import com.bangkit.ripad.ui.filtercalculator.FilterCalculatorActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var currentImageUri: Uri? = null
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    // Launcher untuk izin kamera
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(requireContext(),"Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher untuk mengambil foto dari kamera
    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            processImage(currentImageUri)
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher untuk memilih gambar dari galeri
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            processImage(uri)
        } else {
            Toast.makeText(requireContext(),"No media selected", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.btnScan.setOnClickListener {
            showImageSourceDialog()
        }

        binding.cardCalculator.setOnClickListener {
            val intent = Intent(requireContext(), FilterCalculatorActivity::class.java)
            startActivity(intent)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        viewModel.error.observe(viewLifecycleOwner){ error ->
            Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.response.observe(viewLifecycleOwner) { response ->
            response.predictionId?.let { toDetail(it) }
        }
        // Panggil teks dari resources dan set ke TextView
        binding.tvwelcome1a.text = getString(R.string.teks_hello)
        binding.tvwelcome1b.text = getString(R.string.teks_farmer)
        binding.tvdescwelcome.text = getString(R.string.teks_description_home)
        return binding.root
    }

    // Dialog pemilihan sumber gambar (kamera atau galeri)
    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startCamera()
                    1 -> startGallery()
                }
            }
            .show()
    }

    // Fungsi untuk memulai kamera
    private fun startCamera() {
        if (isPermissionGranted()) {
            currentImageUri = getImageUri()
            launcherCamera.launch(currentImageUri)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Fungsi untuk memulai galeri
    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    // Cek apakah izin kamera diberikan
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Buat URI sementara untuk kamera
    private fun getImageUri(): Uri {
        val imageFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )
    }

    // Proses gambar (URI)
    private fun processImage(imageUri: Uri?) {
        imageUri?.let { uri ->
            runDiagnosis(uri)

        } ?: Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        val window = requireActivity().window
        val decorView = window.decorView

        // Deteksi mode tema
        val isDarkMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            // Mode Dark: Atur status bar menyatu dengan elemen dengan teks terang
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        } else {
            // Mode Light: Atur status bar menyatu dengan elemen dengan teks gelap
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.TRANSPARENT
        }
    }
    private fun runDiagnosis(imageUri: Uri) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.getIdToken(true)?.addOnSuccessListener { result ->
            var token = result.token
            Log.d("Token", ": $token")

            if (token != null) {
                token = "Bearer $token"
                // Menyisipkan token ke dalam header Authorization
                val imagePart = prepareImageFilePart(requireContext(), "image", imageUri)
                if (imagePart != null) {
                    viewModel.predictImage(imagePart, token)
                } else {
                    Toast.makeText(requireContext(), "Failed to prepare image file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @Suppress("SameParameterValue")
    private fun prepareImageFilePart(context: Context, partName: String, uri: Uri): MultipartBody.Part? {
        try {
            val file = uriToFile(context, uri)
            // Tentukan MIME type berdasarkan ekstensi file
            val mimeType = getMimeType(file.extension)
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val extension = getFileExtension(uri) // Menangani ekstensi file
        val file = File(context.cacheDir, "uploaded_image$extension") // Simpan dengan ekstensi yang benar
        val outputStream = file.outputStream()

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return file
    }

    private fun getFileExtension(uri: Uri): String {
        return when (uri.toString().substringAfterLast(".")) {
            "png" -> ".png"
            "jpg", "jpeg" -> ".jpg"
            else -> ".jpg" // Default jika ekstensi tidak dikenali
        }
    }

    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            else -> "image/jpeg" // Default MIME type jika ekstensi tidak dikenali
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun toDetail(id :String){
        val intent = Intent(requireContext(), DetailDiseaseActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}