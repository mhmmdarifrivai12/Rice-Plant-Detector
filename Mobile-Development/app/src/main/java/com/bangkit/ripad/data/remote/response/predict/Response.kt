package com.bangkit.ripad.data.remote.response.predict

import com.google.gson.annotations.SerializedName


data class Response(

	@field:SerializedName("predictionResult")
	val predictionResult: PredictionResult? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("predictionId")
	val predictionId: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class PredictionResult(

	@field:SerializedName("ciri_ciri")
	val ciriCiri: String? = null,

	@field:SerializedName("penyebab")
	val penyebab: String? = null,

	@field:SerializedName("rekomendasi_obat")
	val rekomendasiObat: String? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("perawatan")
	val perawatan: String? = null,

	@field:SerializedName("gejala")
	val gejala: String? = null
)
