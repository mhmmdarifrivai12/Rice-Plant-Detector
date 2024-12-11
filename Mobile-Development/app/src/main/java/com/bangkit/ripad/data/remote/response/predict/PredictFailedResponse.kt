package com.bangkit.ripad.data.remote.response.predict

import com.google.gson.annotations.SerializedName

data class PredictFailedResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
