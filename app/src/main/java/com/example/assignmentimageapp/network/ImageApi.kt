package com.example.assignmentimageapp.network

import com.example.assignmentimageapp.model.ImageModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {

    @GET("?client_id=6zHZYXljVS1EBu9a9cAmAKm34LkUgfzbXpofTP7b51s")
    suspend fun getImage(@Query("page") page: Int,@Query("per_page") perPage:Int=20): Response<ImageModel>
}