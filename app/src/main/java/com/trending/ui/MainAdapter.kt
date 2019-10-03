package com.trending.ui

import android.content.Context
import android.graphics.Color
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
import com.trending.ui.MainAdapter.ViewHolder
import kotlinx.android.synthetic.main.item_trending_list_layout.view.*


/**
 * Created by Ashish on 3/10/19.
 */

class MainAdapter(
    private var context: Context, private var standardResultList: List<StandardResult>,
    listener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<ViewHolder>() {

    private var listener: OnRecyclerItemClickListener? = listener

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
        val model = standardResultList[position]
        holder.binding.setVariable(BR.position, position)
        holder.binding.setVariable(BR.model, model)
        if (listener != null)
            holder.binding.setVariable(BR.listener, listener)
        holder.binding.executePendingBindings()


        Glide.with(context).asBitmap().load(model.avatar).into(holder.binding.root.profile_pic_view)

        if (model.languageColor != null) {
            holder.binding.root.language_color_image.visibility = View.VISIBLE
            holder.binding.root.language_color_image.borderColor = Color.parseColor(model.languageColor)
        }
        else
            holder.binding.root.language_color_image.visibility = View.GONE
        if (model.language != null)
            holder.binding.root.language_name_text_view.visibility = View.VISIBLE
        else
            holder.binding.root.language_name_text_view.visibility = View.GONE
    }

    interface OnRecyclerItemClickListener {
        fun onRecyclerItemClicked(position: Int, standardResult: StandardResult)
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}