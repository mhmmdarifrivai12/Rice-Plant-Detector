package com.bangkit.ripad.ui.detaildiseases

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ripad.R
import com.bangkit.ripad.databinding.ActivityDetailDeseaseBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class DetailDiseaseActivity : AppCompatActivity() {
    private val viewModel: DetailDiseasesViewModel by lazy {
        ViewModelProvider(this)[DetailDiseasesViewModel::class.java]
    }
    private var _binding: ActivityDetailDeseaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailDeseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        binding.svMain.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.response.observe(this) { response ->
            with(binding) {
                svMain.visibility = View.VISIBLE
                itemTitle.text = response.history?.predictionResult?.label
                Glide.with(this@DetailDiseaseActivity)
                    .load(response.history?.imageUrl)
                    .into(itemImg)
                tvItemCiriCiri.text = response.history?.predictionResult?.ciriCiri
                tvItemGejala.text = response.history?.predictionResult?.gejala
                tvItemPenyebab.text = response.history?.predictionResult?.penyebab
                tvItemPerawatan.text = response.history?.predictionResult?.perawatan
                itemRecomendation.text = response.history?.predictionResult?.rekomendasiObat

            }
        }
        id = intent.getStringExtra("id").toString()
        getUserToken { token ->
            if (token != null) {
                viewModel.getDetail(id, token)
            }else{
                showNoInternetError()
            }
        }

        // Observasi error dari ViewModel
        viewModel.error.observe(this) { errorMessage ->
            // Tampilkan pesan error ke pengguna
            Log.e("DetailDiseaseActivity", "Error: $errorMessage")
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }


    }

    // Tambahkan Menu ke Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return true
    }

    // Handle Klik Menu dan Tombol Back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // Tombol Back
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.action_delete -> { // Menu Delete
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                getUserToken { token ->
                    if (token != null) {
                        viewModel.deleteHistory(id, token)
                        viewModel.deleteResponse.observe(this){ deleteResponse ->
                            Toast.makeText(this, deleteResponse.message, Toast.LENGTH_SHORT).show()
                        }
                        onBackPressedDispatcher.onBackPressed()
                    }else{
                        Snackbar.make(binding.root, "Deleted Failed Network Not Available", Snackbar.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun getUserToken(callback: (String?) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                callback(result.token)
            }
            ?.addOnFailureListener {
                callback(null)
            }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun showNoInternetError(){
        Snackbar.make(binding.root, "Error: Network not available", Snackbar.LENGTH_SHORT).show()
    }
}