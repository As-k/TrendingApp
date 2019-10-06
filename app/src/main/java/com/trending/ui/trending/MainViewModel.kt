package com.trending.ui.trending

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trending.data.model.StandardResult
import com.trending.data.remote.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val responseTrendingList = MutableLiveData<List<StandardResult>>()

    // calling trending api
    fun getTrendingList() {
        isLoading.value = true
        ApiClient.getInstance().getAPIService()!!.getTrendingRepositoryList("", "")
            .enqueue(object : Callback<List<StandardResult>> {
                override fun onResponse(
                    call: Call<List<StandardResult>>,
                    response: Response<List<StandardResult>>
                ) {
                    if (response.isSuccessful) {
                        responseTrendingList.value = response.body()

                    } else {
                        error.value = response.message()
                    }
                    isLoading.value = false
                }

                override fun onFailure(call: Call<List<StandardResult>>, t: Throwable) {
                    isLoading.value = false
                    setError(t.message.toString())
                }
            })
    }
    fun setError(error: String) {
        this.error.value = error
    }
}