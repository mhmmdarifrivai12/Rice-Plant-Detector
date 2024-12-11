package com.bangkit.ripad.ui.detaildiseases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ripad.data.remote.repository.Repository
import com.bangkit.ripad.data.remote.response.detail.DetailResponse
import com.bangkit.ripad.data.remote.response.history.DeleteResponse
import com.bangkit.ripad.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class DetailDiseasesViewModel: ViewModel() {
    private val repository = Repository()

    private val _response = SingleLiveEvent<DetailResponse>()
    val response: SingleLiveEvent<DetailResponse> get() = _response
    private val  _deleteResponse= SingleLiveEvent<DeleteResponse>()
    val deleteResponse: LiveData<DeleteResponse> get() = _deleteResponse
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun deleteHistory(id: String, token: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteHistory(id, token)
                _deleteResponse.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.d("DetailDiseasesViewModel", "error : $e ")
            }
        }
    }
    fun getDetail(id: String, token: String){
        viewModelScope.launch {
            try {
                val result = repository.getHistoryById(id, token)
                _response.postValue(result)
                _isLoading.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.d("DetailDiseasesViewModel", "error : $e ")
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

}