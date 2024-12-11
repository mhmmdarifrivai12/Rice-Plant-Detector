package com.bangkit.ripad.data.remote.response.detail

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("history")
	val history: History? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Timestamp(

	@field:SerializedName("_nanoseconds")
	val nanoseconds: Int? = null,

	@field:SerializedName("_seconds")
	val seconds: Int? = null
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

data class History(

	@field:SerializedName("predictionResult")
	val predictionResult: PredictionResult? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: Timestamp? = null
)
