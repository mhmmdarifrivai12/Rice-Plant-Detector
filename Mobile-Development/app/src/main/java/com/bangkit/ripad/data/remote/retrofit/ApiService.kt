package com.bangkit.ripad.data.remote.retrofit
import com.bangkit.ripad.data.remote.response.detail.DetailResponse
import com.bangkit.ripad.data.remote.response.history.HistoryResponse
import com.bangkit.ripad.data.remote.response.history.DeleteResponse
import com.bangkit.ripad.data.remote.response.predict.Response
import okhttp3.MultipartBody
import retrofit2.http.*
import retrofit2.Response as RetrofitResponse


interface ApiService {
    @Multipart
    @POST("api/predict")
    suspend fun predictImage(
        @Part image: MultipartBody.Part,
        @Header("Authorization") authorization: String
    ): RetrofitResponse<Response>
    @DELETE("api/history/{id}")
    suspend fun deleteHistory(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): DeleteResponse
    @GET("api/history")
    suspend fun getHistory(
        @Header("Authorization") authorization: String
    ): HistoryResponse
    @GET("api/history/{id}")
    suspend fun getHistoryById(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): DetailResponse
}
