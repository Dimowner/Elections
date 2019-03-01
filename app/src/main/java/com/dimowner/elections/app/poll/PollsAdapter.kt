/*
 *  Copyright 2019 Dmitriy Ponomarenko
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership. The ASF licenses this
 *  file to you under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package com.dimowner.elections.app.poll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dimowner.elections.R
import com.dimowner.elections.data.model.Candidate

private const val VIEW_TYPE_NORMAL = 1
private const val VIEW_TYPE_FOOTER = 2

class PollsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var data: MutableList<Candidate> = ArrayList()

	private var itemClickListener: ItemClickListener? = null

	private var selectedItem = -1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return if (viewType == VIEW_TYPE_NORMAL) {
			val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_poll, parent, false)
			ItemViewHolder(v)
		} else {
			val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_footer, parent, false)
			FooterViewHolder(v)
		}
	}

	override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
		if (h.itemViewType == VIEW_TYPE_NORMAL) {
			val pos = h.adapterPosition
			val holder = h as ItemViewHolder
			holder.name.text = data[pos].firstName + " " + data[pos].surName
			holder.description.text = if (data[pos].party.isNotBlank()) data[pos].party else "Samovidvijenets"
			holder.image.setImageResource(R.mipmap.ic_elections)

			if (pos == selectedItem) {
				holder.itemPanel.setBackgroundResource(R.drawable.ripple_yellow)
			} else {
				holder.itemPanel.setBackgroundResource(R.drawable.ripple_blue)
			}

			holder.itemPanel.setOnClickListener { v ->
				run {
					setActiveItem(pos)
					itemClickListener?.onItemClick(v, pos, selectedItem != -1)
				}
			}
		} else {
			//Do nothing
		}
	}

	private fun setActiveItem(activeItem: Int) {
		if (this.selectedItem == activeItem) {
			this.selectedItem = -1
			notifyItemChanged(activeItem)
		} else {
			val prev = this.selectedItem
			this.selectedItem = activeItem
			notifyItemChanged(prev)
			notifyItemChanged(activeItem)
		}
	}

	override fun getItemViewType(position: Int): Int {
		return if (position >= data.size) VIEW_TYPE_FOOTER else VIEW_TYPE_NORMAL
	}

	override fun getItemCount(): Int {
		return data.size + 1
	}

	fun setData(list: List<Candidate>) {
		if (data.isEmpty()) {
			this.data.addAll(list)
			notifyDataSetChanged()
		} else {
			val diff = CandDiffUtilCallback(data, list)
			val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diff)
			this.data.clear()
			this.data.addAll(list)
			diffResult.dispatchUpdatesTo(this)
		}
	}

	fun setItemClickListener(itemClickListener: ItemClickListener) {
		this.itemClickListener = itemClickListener
	}

	internal inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
		var name: TextView = view.findViewById(R.id.list_item_name)
		var description: TextView = view.findViewById(R.id.list_item_description)
		var image: ImageView = view.findViewById(R.id.list_item_image)
		var txtVal: TextView = view.findViewById(R.id.list_item_value)
		var itemPanel: LinearLayout = view.findViewById(R.id.item_panel)
	}

	internal inner class FooterViewHolder internal constructor(internal val view: View) : RecyclerView.ViewHolder(view)

	interface ItemClickListener {
		fun onItemClick(view: View, position: Int, selected: Boolean)
	}
}