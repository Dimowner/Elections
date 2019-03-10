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

package com.dimowner.elections.app.votes

import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dimowner.elections.R
import com.dimowner.elections.util.AndroidUtils
import com.dimowner.elections.util.TimeUtils
import java.util.*

class VotesListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var data: MutableList<VoteListItem> = ArrayList()

	override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RecyclerView.ViewHolder {
		when (type) {
			ITEM_TYPE_HEADER -> {
				val view = View(viewGroup.context)
				val height: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					AndroidUtils.getStatusBarHeight(viewGroup.context) + viewGroup.context.resources.getDimension(R.dimen.toolbar_height).toInt()
				} else {
					viewGroup.context.resources.getDimension(R.dimen.toolbar_height).toInt()
				}
				val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
				view.layoutParams = lp
				return UniversalViewHolder(view)
			}
			ITEM_TYPE_DATE -> {
				//Create date list item layout programmatically.
				val textView = TextView(viewGroup.context)
				val lp = ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT)
				textView.layoutParams = lp
				textView.setTypeface(textView.typeface, Typeface.BOLD)
				textView.setTextColor(viewGroup.context.resources.getColor(R.color.primary_transparent))
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewGroup.context.resources.getDimension(R.dimen.text_medium))

				val pad = viewGroup.context.resources.getDimension(R.dimen.spacing_small).toInt()
				textView.setPadding(pad, pad, pad, pad)
				textView.gravity = Gravity.CENTER

				return UniversalViewHolder(textView)
			}
			ITEM_TYPE_FOOTER -> {
				val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_footer, viewGroup, false)
				return UniversalViewHolder(v)
			}
			else -> {
				val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_activity, viewGroup, false)
				return ItemViewHolder(v)
			}
		}
	}

	override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
		val pos = viewHolder.adapterPosition
		if (viewHolder.itemViewType == ITEM_TYPE_NORMAL) {
			val holder = viewHolder as ItemViewHolder
			holder.name.text = data[pos].device + " (" + data[pos].country + ")\nЗа " + data[pos].candidateName
			holder.date.text = TimeUtils.formatTime(data[pos].time)
		} else if (viewHolder.itemViewType == ITEM_TYPE_DATE) {
			val holder = viewHolder as UniversalViewHolder
			(holder.view as TextView).text = TimeUtils.formatDateSmart(data[pos].time, holder.view.context)
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

	fun setData(list: List<VoteListItem>) {
		if (data.isEmpty()) {
			this.data.addAll(list)
			addDateHeaders()
			this.data.add(0, VoteListItem.createHeaderItem())
			this.data.add(VoteListItem.createFooterItem())
			notifyDataSetChanged()
		} else {
			val diff = VotesDiffUtilCallback(data, list)
			val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diff)
			this.data.clear()
			this.data.addAll(list)
			addDateHeaders()
			this.data.add(0, VoteListItem.createHeaderItem())
			this.data.add(VoteListItem.createFooterItem())
			diffResult.dispatchUpdatesTo(this)
		}
	}

	private fun addDateHeaders() {
		if (data.size > 0) {
			data.add(0, VoteListItem.createDateItem(data[0].time))
			val d1 = Calendar.getInstance()
			d1.timeInMillis = data[0].time
			val d2 = Calendar.getInstance()
			for (i in 1 until data.size) {
				d1.timeInMillis = data[i - 1].time
				d2.timeInMillis = data[i].time
				if (!TimeUtils.isSameDay(d1, d2)) {
					data.add(i, VoteListItem.createDateItem(data[i].time))
				}
			}
		}
	}

	private inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
		var name: TextView = view.findViewById(R.id.txtActivity)
		var date: TextView = view.findViewById(R.id.txtActivityDate)
	}

	private inner class UniversalViewHolder(var view: View) : RecyclerView.ViewHolder(view)
}
