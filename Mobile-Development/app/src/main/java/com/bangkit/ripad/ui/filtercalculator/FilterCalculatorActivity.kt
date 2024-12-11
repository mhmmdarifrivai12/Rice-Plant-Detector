package com.bangkit.ripad.ui.filtercalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.ripad.R
import com.bangkit.ripad.ui.detailresultcalculator.DetailResultCalculatorActivity

class FilterCalculatorActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter_calculator)
        // Inisialisasi view
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val confirmButton = findViewById<Button>(R.id.btnConfirm)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Menggunakan WindowInsets untuk menyesuaikan margin bawah
        ViewCompat.setOnApplyWindowInsetsListener(confirmButton) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = systemBarsInsets.bottom + 24 // Tambahkan padding 24dp
            view.layoutParams = layoutParams
            insets
        }
        // Event untuk tombol "Confirm"
        confirmButton.setOnClickListener {
            // Cari RadioButton yang dipilih
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                // Dapatkan teks dari RadioButton yang dipilih
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedDisease = selectedRadioButton.text.toString()

                val intent = Intent(this, DetailResultCalculatorActivity::class.java)
                intent.putExtra("SELECTED_DISEASE", selectedDisease) // Kirim data penyakit yang dipilih
                startActivity(intent)
            } else {
                // Jika tidak ada RadioButton yang dipilih, tampilkan pesan
                Toast.makeText(this, "Silakan pilih jenis penyakit terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
