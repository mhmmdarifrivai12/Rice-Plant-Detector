package com.bangkit.ripad.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ripad.data.remote.repository.Repository
import com.bangkit.ripad.data.remote.response.history.DeleteResponse
import com.bangkit.ripad.data.remote.response.history.HistoryItem
import com.bangkit.ripad.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repository = Repository()
    private val _history = MutableLiveData<List<HistoryItem>?>()
    val history: LiveData<List<HistoryItem>?> = _history
    private val _isLoading = SingleLiveEvent<Boolean>()
    private val  _deleteResponse= MutableLiveData<DeleteResponse>()
    val deleteResponse: LiveData<DeleteResponse> get() = _deleteResponse
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _error = MutableLiveData<String>()
    private val error: LiveData<String> get() = _error

    fun getHistory(token: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val validToken = "Bearer $token"
                val result = repository.getHistory(validToken)
                _history.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.d("Error History View Model", "error : $e ")
            }finally {
                _isLoading.postValue(false)
                Log.d("Error History View Model", "error : $error ")
            }

        }
    }
    fun deleteHistory(id: String, token: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteHistory(id, token)
                _deleteResponse.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.d("Error History View Model", "error : $e ")
            }
        }
    }

}