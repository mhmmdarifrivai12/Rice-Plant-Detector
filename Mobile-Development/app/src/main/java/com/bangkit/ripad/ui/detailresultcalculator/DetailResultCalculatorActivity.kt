package com.bangkit.ripad.ui.detailresultcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bangkit.ripad.R

class DetailResultCalculatorActivity : AppCompatActivity() {

    private val viewModel: DetailResultCalculatorViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_result_calculator)

        val inputLuasLahan = findViewById<EditText>(R.id.inputLuasLahan)
        val calculateButton = findViewById<Button>(R.id.btnCalculate)
        val recommendationSection = findViewById<ConstraintLayout>(R.id.recommendationSection)
        val recommendationContent = findViewById<TextView>(R.id.recommendationContent)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Ambil nama penyakit dari Intent
        val selectedDisease = intent.getStringExtra("SELECTED_DISEASE")

        calculateButton.setOnClickListener {
            val luasLahanText = inputLuasLahan.text.toString()

            if (luasLahanText.isEmpty()) {
                Toast.makeText(this, "Harap masukkan luas lahan!", Toast.LENGTH_SHORT).show()
            } else if (selectedDisease != null) {
                val luasLahan = luasLahanText.toDouble()
                viewModel.calculateRecommendations(luasLahan, selectedDisease)
            } else {
                Toast.makeText(this, "Penyakit tidak ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe perubahan rekomendasi
        viewModel.recommendations.observe(this) { recommendations ->
            if (recommendations.isNotEmpty()) {
                recommendationContent.text = recommendations
                recommendationSection.visibility = View.VISIBLE
            } else {
                recommendationContent.text = "Data tidak ditemukan untuk penyakit ini."
                recommendationSection.visibility = View.VISIBLE
            }
        }
    }
}
