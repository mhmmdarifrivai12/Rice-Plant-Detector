package com.bangkit.ripad.data.remote.response.history

import com.google.gson.annotations.SerializedName

data class DeleteResponse (
    @SerializedName("status")
    var status: Boolean? = null,

    @SerializedName("message")
    val message: String? = null
)

