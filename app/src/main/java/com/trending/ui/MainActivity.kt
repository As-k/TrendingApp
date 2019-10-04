package com.trending.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.trending.R
import com.trending.data.model.StandardResult
import com.trending.databinding.ActivityMainBinding
import com.trending.databinding.ContentMainBinding
import com.trending.utils.CommonUtils


class MainActivity : AppCompatActivity(), MainAdapter.OnRecyclerItemClickListener {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var contentMainBinding: ContentMainBinding
    lateinit var retryButton: Button
    lateinit var adapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//            window.statusBarColor = Color.parseColor("#FFFFFF")
//        }

        contentMainBinding =
            DataBindingUtil.findBinding(binding.contentLayout.root)!!
        retryButton = findViewById(R.id.retry_button)
        retryButton.setOnClickListener {
            callApi()
        }
        bindObservables()
    }

    private fun bindObservables() {
        callApi()
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                contentMainBinding.trendingRecyclerView.visibility = View.GONE
                contentMainBinding.shimmerRecyclerView.showShimmerAdapter()
            } else {
                contentMainBinding.trendingRecyclerView.visibility = View.VISIBLE
                contentMainBinding.shimmerRecyclerView.hideShimmerAdapter()
            }
        })

        viewModel.responseTrendingList.observe(this, Observer {
            if (it.isNotEmpty()) {
                val layoutManager = LinearLayoutManager(this)
                adapter = MainAdapter(this, it, this)
                contentMainBinding.trendingRecyclerView.layoutManager = layoutManager
                contentMainBinding.trendingRecyclerView.setHasFixedSize(true)
                contentMainBinding.trendingRecyclerView.itemAnimator = DefaultItemAnimator()
                contentMainBinding.trendingRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No item found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callApi() {
        if (isNetworkConnected()) {
            viewModel.getTrendingList()
            contentMainBinding.noInternetConnectionLayout.visibility =
                View.GONE
            contentMainBinding.trendingRecyclerView.visibility = View.VISIBLE
            contentMainBinding.shimmerRecyclerView.visibility = View.VISIBLE
        } else {
            contentMainBinding.noInternetConnectionLayout.visibility =
                View.VISIBLE
            contentMainBinding.trendingRecyclerView.visibility = View.GONE
            contentMainBinding.shimmerRecyclerView.visibility = View.GONE
        }
    }

    private fun isNetworkConnected(): Boolean {
        return CommonUtils.isNetworkConnected(this)
    }

    override fun onRecyclerItemClicked(position: Int, standardResult: StandardResult) {
        Toast.makeText(this, standardResult.name + "  " + position, Toast.LENGTH_SHORT).show()
//        TransitionManager.beginDelayedTransition(contentMainBinding.trendingRecyclerView)
    }

}
