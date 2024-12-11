package com.bangkit.ripad.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ripad.data.remote.repository.Repository
import com.bangkit.ripad.data.remote.response.predict.Response
import com.bangkit.ripad.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class HomeViewModel : ViewModel() {
    private val repository = Repository()

    private val _response = SingleLiveEvent<Response>()
    val response: LiveData<Response> get() = _response
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = SingleLiveEvent<String>()
    val error: LiveData<String> get() = _error

    fun predictImage(image: MultipartBody.Part, token: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val result = repository.predictImage(image, token)
            result.fold(
                onSuccess = { successResponse ->
                    _response.postValue(successResponse) // Set data sukses ke LiveData
                    Log.d("HomeViewModel", "Success: ${successResponse.message}")
                },
                onFailure = { throwable ->
                    if(throwable.message == "Prediction failed, This is not a Padi,")
                        _error.postValue("Selected Image Is Not Paddy")
                    else{
                        _error.postValue(throwable.message ?: "Unknown error") // Set error message ke LiveData
                        Log.d("HomeViewModel", "Error: ${throwable.message}")
                    }

                }
            )
            _isLoading.postValue(false)
        }
    }

}