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

package com.dimowner.elections.app.candidates

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dimowner.elections.R
import com.dimowner.elections.util.AndroidUtils

class CandidatesListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var data: MutableList<CandidatesListItem> = ArrayList()

	private var itemClickListener: ItemClickListener? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		when (viewType) {
			ITEM_TYPE_HEADER -> {
				val view = View(parent.context)
				val height: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					AndroidUtils.getStatusBarHeight(parent.context) + parent.context.resources.getDimension(R.dimen.toolbar_height).toInt()
				} else {
					parent.context.resources.getDimension(R.dimen.toolbar_height).toInt()
				}
				val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
				view.layoutParams = lp
				return UniversalViewHolder(view)
			}
			ITEM_TYPE_FOOTER -> {
				val view = View(parent.context)
				val height: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					AndroidUtils.getNavigationBarHeight(parent.context) + parent.context.resources.getDimension(R.dimen.footer_height).toInt()
				} else {
					parent.context.resources.getDimension(R.dimen.footer_height).toInt()
				}
				val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
				view.layoutParams = lp
				return UniversalViewHolder(view)
			}
			else -> {
				val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_results, parent, false)
				return ItemViewHolder(v)
			}
		}
	}

	override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
		if (h.itemViewType == ITEM_TYPE_NORMAL) {
			val pos = h.adapterPosition
			val holder = h as ItemViewHolder
			holder.name.text = data[pos].name
			holder.txtVal.text = data[pos].votesPercent.toString() + "%"
			holder.txtCount.text = data[pos].votesCount.toString()
			holder.progressVal.progress = data[pos].votesPercent
			holder.description.text = data[pos].party
			if (data[pos].iconUrl.isNotBlank()) {
				Glide.with(holder.image.context)
						.load(data[pos].iconUrl)
						.apply(RequestOptions.circleCropTransform())
						.into(holder.image)
			} else {
				holder.image.setImageResource(AndroidUtils.candidateCodeToResource(data[pos].iconId))
			}
			holder.itemPanel.setOnClickListener { v -> itemClickListener?.onItemClick(v, pos) }
		} else {
			//Do nothing
		}
	}

	override fun getItemViewType(position: Int): Int {
		return data[position].type
	}

	override fun getItemCount(): Int {
		return data.size
	}

	fun setData(list: List<CandidatesListItem>) {
		if (data.isEmpty()) {
			this.data.clear()
			this.data.addAll(list)
			this.data.add(0, CandidatesListItem.createHeaderItem())
			this.data.add(CandidatesListItem.createFooterItem())
			notifyDataSetChanged()
		} else {
			(list as MutableList).add(0, CandidatesListItem.createHeaderItem())
			list.add(CandidatesListItem.createFooterItem())

			val diff = CandidatesDiffUtilCallback(data, list)
			val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diff)
			this.data.clear()
			this.data.addAll(list)

			diffResult.dispatchUpdatesTo(this)
		}
	}

	fun getIconCodeForPosition(pos: Int): String {
		if (data.size > pos) {
			return data[pos].iconId
		}
		return ""
	}

	fun setItemClickListener(itemClickListener: ItemClickListener) {
		this.itemClickListener = itemClickListener
	}

	private inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
		var name: TextView = view.findViewById(R.id.list_item_name)
		var description: TextView = view.findViewById(R.id.list_item_description)
		var image: ImageView = view.findViewById(R.id.list_item_image)
		var txtVal: TextView = view.findViewById(R.id.list_item_value)
		var txtCount: TextView = view.findViewById(R.id.list_item_count)
		var progressVal: ProgressBar = view.findViewById(R.id.lit_item_percentage)
		var itemPanel: LinearLayout = view.findViewById(R.id.item_panel)
	}

	private inner class UniversalViewHolder(var view: View) : RecyclerView.ViewHolder(view)

	interface ItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}