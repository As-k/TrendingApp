package com.trending.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Created by Ashish on 3/10/19.
 */

class CommonUtils {

    companion object {

        @BindingAdapter("imageUrl")
        fun loadImage(view: CircleImageView, imageUrl: String) {
            Glide.with(view.context)
                .asBitmap()
                .load(imageUrl)
                .into(view)
        }

        @BindingAdapter("app:civ_bgColor")
        fun setBackgroundColor(imageView: CircleImageView, civ_bgColor: String) {
            imageView.borderColor = Color.parseColor(civ_bgColor)
        }

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val activeNetwork = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }
            return false
        }
    }

}