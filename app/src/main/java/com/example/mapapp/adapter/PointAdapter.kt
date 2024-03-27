package com.example.mapapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mapapp.R
import com.example.mapapp.databinding.CardPointBinding
import com.example.mapapp.dto.UserPoint

interface OnInteractionListener {
    fun onRemove(point: UserPoint)
    fun onEdit(point: UserPoint)
    fun onPoint(point: UserPoint)
}

class PointAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<UserPoint, PointViewHolder>(PointDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = CardPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = getItem(position)
        holder.bind(point)
    }
}

class PointViewHolder(
    private val binding: CardPointBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(point: UserPoint) {
        with(binding) {
            pointName.text = point.name
            pointMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.point_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.edit -> {
                                onInteractionListener.onEdit(point)
                                true
                            }

                            R.id.remove -> {
                                onInteractionListener.onRemove(point)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            root.setOnClickListener {
                onInteractionListener.onPoint(point)
            }
        }
    }
}

object PointDiffCallBack : DiffUtil.ItemCallback<UserPoint>() {
    override fun areItemsTheSame(oldItem: UserPoint, newItem: UserPoint) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: UserPoint, newItem: UserPoint) = oldItem == newItem
}