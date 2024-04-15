package com.example.assignmentimageapp

import com.example.assignmentimageapp.model.ImageModel
import com.example.assignmentimageapp.network.ImageApi
import com.example.assignmentimageapp.network.RetrofitClient
import retrofit2.Response

class ImageRepository {

    private val api: ImageApi = RetrofitClient.imageApi

    suspend fun getImage(page: Int): Response<ImageModel> {
        return api.getImage(page)
    }
}