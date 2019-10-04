package com.trending.data.remote

import com.trending.data.model.StandardResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Ashish on 3/10/19.
 */

interface ApiService {

    @GET("repositories")
    fun getTrendingRepositoryList(@Query("language") language: String,
                                  @Query("since") since: String): Call<List<StandardResult>>

}