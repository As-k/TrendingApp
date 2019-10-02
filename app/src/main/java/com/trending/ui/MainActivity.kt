package com.trending.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.trending.R
import com.trending.databinding.ActivityMainBinding
import com.trending.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }

        var contentMainBinding =
            DataBindingUtil.findBinding<ContentMainBinding>(binding!!.contentLayout.root)

        contentMainBinding!!.shimmerRecyclerView.visibility = View.VISIBLE
        Handler().postDelayed(
            Runnable { contentMainBinding!!.shimmerRecyclerView.hideShimmerAdapter() },
            2000
        )
//        setSupportActionBar(binding!!.toolbar)
    }

}
