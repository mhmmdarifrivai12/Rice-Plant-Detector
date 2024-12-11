package com.bangkit.ripad.data.remote.repository

import com.bangkit.ripad.data.remote.response.detail.DetailResponse
import com.bangkit.ripad.data.remote.response.history.DeleteResponse
import com.bangkit.ripad.data.remote.response.history.HistoryItem
import com.bangkit.ripad.data.remote.response.predict.PredictFailedResponse
import com.bangkit.ripad.data.remote.response.predict.Response
import com.bangkit.ripad.data.remote.retrofit.RetrofitClient
import com.google.gson.Gson
import okhttp3.MultipartBody


class Repository {
    private val apiService = RetrofitClient.getApiService()

    suspend fun predictImage(image: MultipartBody.Part, token: String): Result<Response> {
        val response = apiService.predictImage(image, token) // Retrofit call

        return if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, PredictFailedResponse::class.java)
            Result.failure(Exception(errorResponse.message ?: "Unknown error"))
        }
    }

    suspend fun deleteHistory(id: String, token: String): DeleteResponse {
        return apiService.deleteHistory(id, token)
    }
    suspend fun getHistory(token: String): List<HistoryItem> {
        val response = apiService.getHistory(token)
        return response.history
    }
    suspend fun getHistoryById(id: String, token: String): DetailResponse {
       return apiService.getHistoryById(id, token)
    }
}
