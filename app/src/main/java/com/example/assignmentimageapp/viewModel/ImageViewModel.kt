package com.example.assignmentimageapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentimageapp.model.ImageModelItem
import com.example.assignmentimageapp.ImageRepository
import com.example.assignmentimageapp.network.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {
    private val _imageLoadingState: MutableStateFlow<NetworkResponse<ArrayList<ImageModelItem>>?> =
        MutableStateFlow(NetworkResponse.Loading())
    val imageLoadingState: MutableStateFlow<NetworkResponse<ArrayList<ImageModelItem>>?> = _imageLoadingState
    val nextloading= MutableStateFlow(false)

    // Pagination parameters
    private var currentPage = 1
    private var isFetching = false

    fun getImage() {
        if (isFetching) return // Prevent duplicate calls

        viewModelScope.launch {
            try {
                isFetching = true
                if(currentPage>2){
                    nextloading.value=true
                }
                val response = repository.getImage(page = currentPage)
                if (response.isSuccessful) {
                    val newData = response.body() ?: return@launch
                    val currentData = (_imageLoadingState.value as? NetworkResponse.Success)?.data
                    val combinedData = currentData?.plus(newData) ?: newData
                    _imageLoadingState.value = NetworkResponse.Success(combinedData as ArrayList)
                    currentPage++ // Increment page number for next call
                } else {
                    _imageLoadingState.value = NetworkResponse.Failure(response.message())
                }
            } catch (e: Exception) {
                _imageLoadingState.value =
                    NetworkResponse.Failure(e.message ?: "Something went wrong")
            } finally {
                nextloading.value=false
                isFetching = false // Reset fetching flag
            }
        }
    }
}