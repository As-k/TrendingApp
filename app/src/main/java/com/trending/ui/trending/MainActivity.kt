package com.trending.ui.trending

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.trending.R
import com.trending.data.model.StandardResult
import com.trending.databinding.ActivityMainBinding
import com.trending.databinding.ContentMainBinding
import com.trending.ui.trending_adapter.TrendingAdapter
import com.trending.utils.CommonUtils


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val TAG: String = "MainActivity"
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var contentMainBinding: ContentMainBinding
    lateinit var retryButton: Button
    lateinit var standardResultList: List<StandardResult>
    lateinit var trendingAdapter: TrendingAdapter
    private var popupWindow: PopupWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        contentMainBinding =
            DataBindingUtil.findBinding(binding.contentLayout.root)!!

        contentMainBinding.swipeContainer.setOnRefreshListener(this)
        contentMainBinding.swipeContainer.setColorSchemeResources(
            R.color.black,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        contentMainBinding.swipeContainer.post {
            contentMainBinding.swipeContainer.isRefreshing = true
            // Fetching data from server
            makeCallApi()
        }

        retryButton = findViewById(R.id.retry_button)
        val actionPopupMenuImage = findViewById<ImageView>(R.id.action_popup_menu_image)
        retryButton.setOnClickListener {
            makeCallApi()
        }
        actionPopupMenuImage.setOnClickListener {
            if (popupWindow != null) {
                if (popupWindow!!.isShowing)
                // Set a dismiss listener for popup window
                    popupWindow!!.dismiss()
                else
                    callPopupMenu()
            } else
                callPopupMenu()
        }
        bindObservables()
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    override fun onRefresh() {
        // Fetching data from server
        makeCallApi()
    }

    private fun bindObservables() {
        makeCallApi()
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                contentMainBinding.trendingRecyclerView.visibility = View.GONE
                contentMainBinding.shimmerRecyclerView.showShimmerAdapter()
            } else {
                contentMainBinding.trendingRecyclerView.visibility = View.VISIBLE
                contentMainBinding.shimmerRecyclerView.hideShimmerAdapter()
            }
            contentMainBinding.swipeContainer.isRefreshing = false
        })

        //observing list from viewModel
        viewModel.responseTrendingList.observe(this, Observer {
            if (it.isNotEmpty()) {
                this.standardResultList = it
                val layoutManager = LinearLayoutManager(this)
                contentMainBinding.trendingRecyclerView.layoutManager = layoutManager
//                contentMainBinding.trendingRecyclerView.setHasFixedSize(true)
                trendingAdapter =
                    TrendingAdapter(this@MainActivity, standardResultList)
                contentMainBinding.trendingRecyclerView.adapter = trendingAdapter
                contentMainBinding.trendingRecyclerView.itemAnimator = DefaultItemAnimator()
                trendingAdapter.notifyDataSetChanged()
                Log.d(TAG, "original list: $standardResultList")
            } else {
                Toast.makeText(this, "No item found", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.error.observe(this, Observer {
            Log.d(TAG, "" + it)
        })
    }

    @SuppressLint("InflateParams")
    private fun callPopupMenu() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val popupMenu = inflater.inflate(R.layout.popup_menu_layout, null)

        // Initialize a new instance of popup window
        popupWindow = PopupWindow(
            popupMenu, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow!!.elevation = 8.0f
        }

        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow!!.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.END
            popupWindow!!.exitTransition = slideOut

        }

        // Get the widgets reference from custom view
        val sortByStar = popupMenu.findViewById<TextView>(R.id.action_sort_by_star)
        val sortByName = popupMenu.findViewById<TextView>(R.id.action_sort_by_name)

        // Set click listener for popup window's sort by star view
        sortByStar.setOnClickListener {
            // sort the list from star
            val list = standardResultList.sortedWith(Comparator { o1, o2 ->
                return@Comparator o2.stars!!.compareTo(o1.stars!!)
            })
            trendingAdapter = TrendingAdapter(this@MainActivity, list)
            contentMainBinding.trendingRecyclerView.adapter = trendingAdapter
            trendingAdapter.notifyDataSetChanged()
            popupWindow!!.dismiss()
        }

        // Set a click listener for popup's sort by name view
        sortByName.setOnClickListener {
            // sort the list from star
            val list = standardResultList.sortedWith(Comparator { o1, o2 ->
                o1.name!!.compareTo(o2.name!!, true)
            })
            trendingAdapter = TrendingAdapter(this@MainActivity, list)
            contentMainBinding.trendingRecyclerView.adapter = trendingAdapter
            trendingAdapter.notifyDataSetChanged()
            popupWindow!!.dismiss()

        }

        val toolbar = findViewById<ConstraintLayout>(R.id.const_toolbar_layout)
        // Finally, show the popup window on app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(toolbar)
        }
        popupWindow!!.showAtLocation(
            toolbar, // Location to display popup window
            Gravity.RIGHT or Gravity.TOP, // Exact position of layout to display popup
            40, // X offset
            150 // Y offset
        )

    }

    private fun makeCallApi() {
        // checking network connection
        if (isNetworkConnected()) {
            viewModel.getTrendingList()
            contentMainBinding.noInternetConnectionLayout.visibility = View.GONE
            contentMainBinding.trendingRecyclerView.visibility = View.VISIBLE
            contentMainBinding.shimmerRecyclerView.visibility = View.VISIBLE
        } else {
            contentMainBinding.swipeContainer.isRefreshing = false
            contentMainBinding.noInternetConnectionLayout.visibility = View.VISIBLE
            contentMainBinding.trendingRecyclerView.visibility = View.GONE
            contentMainBinding.shimmerRecyclerView.visibility = View.GONE
        }
    }

    //checking network connection
    private fun isNetworkConnected(): Boolean {
        return CommonUtils.isNetworkConnected(this)
    }

    //when back pressed
    override fun onBackPressed() {
        if (popupWindow != null)
            if (popupWindow!!.isShowing)
                popupWindow!!.dismiss()
            else
                super.onBackPressed()
        else
            super.onBackPressed()
    }


    //when clicked on recycler view (trending) item
    fun onItemClicked(position: Int, standardResult: StandardResult, mExpandedPosition: Int) {
        if (popupWindow != null)
            if (popupWindow!!.isShowing)
                popupWindow!!.dismiss()
    }

}
