package com.bangkit.ripad.ui.detailresultcalculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailResultCalculatorViewModel : ViewModel() {

    private val _recommendations = MutableLiveData<String>()
    val recommendations: LiveData<String> get() = _recommendations

    private val data = listOf(
        Disease(
            "Bacterial Blight",
            listOf(
                Medicine(
                    "Besun Elite® 300 SC",
                    "150-200 ml",
                    "Larutkan 150-200 ml dalam 15-20 liter air, kemudian semprotkan secara merata pada seluruh bagian tanaman padi, terutama pada daun yang terinfeksi."
                ),
                Medicine(
                    "Kasumin 5 SC",
                    "300 ml",
                    "Semprotkan pada daun yang terinfeksi dengan konsentrasi yang tepat."
                )
            )
        ),
        Disease(
            "Blast",
            listOf(
                Medicine(
                    "Tilt 250 EC",
                    "0.5-1 liter",
                    "Penyemprotan dilakukan pada awal gejala, dengan interval 14-21 hari untuk hasil terbaik."
                ),
                Medicine(
                    "Amistar",
                    "200-250 ml",
                    "Semprotkan pada daun yang terinfeksi, dengan interval aplikasi setiap 2-3 minggu sekali."
                )
            )
        ),
        Disease(
            "Brown Spot",
            listOf(
                Medicine(
                    "Bravo 500 SC",
                    "2-3 liter",
                    "Semprotkan fungisida ini pada daun dan batang yang terinfeksi pada tahap awal gejala. Pengulangan penyemprotan dapat dilakukan setiap 10-14 hari."
                ),
                Medicine(
                    "Daconil 500 SC",
                    "2-3 liter",
                    " Semprotkan fungisida ini pada daun dan batang yang terinfeksi pada tahap awal gejala. Pengulangan penyemprotan dapat dilakukan setiap 10-14 hari."
                )
            )
        ),
        Disease(
            "Tungro",
            listOf(
                Medicine(
                    "Phorate",
                    "3 liter",
                    "Phorate umumnya diaplikasikan dengan cara ditaburkan ke tanah sekitar pangkal tanaman padi. Lakukan aplikasi Phorate pada waktu penanaman atau pada awal fase pertumbuhan tanaman padi (sebelum atau saat tanam)."
                ),
                Medicine(
                    "Carbaryl",
                    "3 liter",
                    "Carbaryl diaplikasikan dengan cara penyemprotan ke seluruh bagian tanaman padi, terutama pada bagian daun dan batang tempat hama sering berada. Penyemprotan dilakukan pada pagi atau sore hari untuk menghindari penguapan yang berlebihan di bawah sinar matahari langsung."
                )
            )
        )
    )

    fun calculateRecommendations(luasLahan: Double, selectedDisease: String) {
        val selectedData = data.find { it.name == selectedDisease }
        if (selectedData != null) {
            val builder = StringBuilder()
            builder.append("Penyakit: ${selectedData.name}\n\n")

            // Loop untuk setiap obat yang terkait dengan penyakit yang dipilih
            for ((index, medicine) in selectedData.medicines.withIndex()) {
                builder.append("Obat ${index + 1}: ${medicine.name}\n")

                // Menghitung dosis dan menambahkan satuan
                val dosisRange = medicine.dosis.split("-")
                val minDosis = dosisRange[0].filter { it.isDigit() || it == '.' }.toDouble()
                val maxDosis = dosisRange.getOrNull(1)?.filter { it.isDigit() || it == '.' }?.toDouble()
                val unit = if (medicine.dosis.contains("ml")) "ml" else "liter"
                val totalDosis = if (maxDosis != null) {
                    "${minDosis * luasLahan} - ${maxDosis * luasLahan} $unit per hektar."
                } else {
                    "${minDosis * luasLahan} $unit per hektar."
                }
                builder.append("  Dosis: $totalDosis\n")

                // Menambahkan cara aplikasi
                builder.append("  Cara aplikasi: ${medicine.description}\n\n")
            }

            _recommendations.value = builder.toString()
        } else {
            _recommendations.value = "Data tidak ditemukan untuk penyakit ini."
        }
    }

    data class Disease(val name: String, val medicines: List<Medicine>)
    data class Medicine(val name: String, val dosis: String, val description: String)
}
