package com.trending.ui.trending_adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trending.BR
import com.trending.R
import com.trending.data.model.StandardResult
import com.trending.ui.trending.MainActivity
import kotlinx.android.synthetic.main.item_trending_list_layout.view.*


/**
 * Created by Ashish on 3/10/19.
 */

class TrendingAdapter(
    private var context: Context, private var standardResultList: List<StandardResult>
) : RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {
    private val TAG = "TrendingAdapter"
    private var mExpandedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DataBindingUtil.bind<ViewDataBinding>(
                LayoutInflater.from(context).inflate(
                    R.layout.item_trending_list_layout,
                    parent,
                    false
                )
            )
        return ViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return standardResultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val standardResult = standardResultList[position]
        holder.binding.setVariable(BR.position, position)
        holder.binding.setVariable(BR.model, standardResult)
        holder.binding.executePendingBindings()

        //checking position when clicked on item
        val isExpanded = position == mExpandedPosition
        if (isExpanded) {
            holder.binding.root.const_layout.visibility = View.VISIBLE
            holder.binding.root.gray_view.visibility = View.GONE
            val paddingDp = 16
            val density = context.resources.displayMetrics.density
            val paddingPixel = (paddingDp * density).toInt()
            holder.binding.root.star_count_text_view.setPadding(0, 0, 0, paddingPixel)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.binding.root.main_item_cons_layout.elevation = 5.0f
            }
        } else {
            holder.binding.root.const_layout.visibility = View.GONE
            holder.binding.root.gray_view.visibility = View.VISIBLE
            holder.binding.root.star_count_text_view.setPadding(0, 0, 0, 0)
            holder.binding.root.main_item_cons_layout.setBackgroundColor(Color.WHITE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.binding.root.main_item_cons_layout.elevation = 0.0f
            }
        }

        holder.binding.root.isActivated = isExpanded

        //storing image
        Glide.with(context).asBitmap().load(standardResult.avatar)
            .into(holder.binding.root.profile_pic_view)

        if (standardResult.languageColor != null) {
            Log.d(TAG, standardResult.language + " " + standardResult.languageColor)
            holder.binding.root.language_color_image.visibility = View.VISIBLE
            if (standardResult.languageColor.length == 4) {
                var colorString = standardResult.languageColor
                colorString =
                    "#" + colorString[1] + colorString[1] + colorString[2] + colorString[2] + colorString[3] + colorString[3]
                Log.d(TAG, "colorString $colorString")
                holder.binding.root.language_color_image.borderColor = Color.parseColor(colorString)
            } else {
                holder.binding.root.language_color_image.borderColor =
                    Color.parseColor(standardResult.languageColor)
            }
        } else {
            holder.binding.root.language_color_image.visibility = View.GONE
        }
        //checking when language there
        if (standardResult.language != null)
            holder.binding.root.language_name_text_view.visibility = View.VISIBLE
        else
            holder.binding.root.language_name_text_view.visibility = View.GONE

        //checking when description there
        if (standardResult.description == null || standardResult.description == "")
            holder.binding.root.description_text_view.visibility = View.GONE
        else
            holder.binding.root.description_text_view.visibility = View.VISIBLE

        //when clicked on view
        holder.binding.root.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            (context as MainActivity).onItemClicked(position, standardResult, mExpandedPosition)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

}