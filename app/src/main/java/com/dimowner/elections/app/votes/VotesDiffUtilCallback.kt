package com.dimowner.elections.app.votes

import androidx.recyclerview.widget.DiffUtil
import com.dimowner.elections.data.model.Vote

/**
 * Created on 02.03.2019.
 * @author Dimowner
 */
class VotesDiffUtilCallback(private val oldList: List<Vote>, private val newList: List<Vote>) : DiffUtil.Callback() {

	override fun getOldListSize(): Int {
		return oldList.size
	}

	override fun getNewListSize(): Int {
		return newList.size
	}

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition].deviceId == newList[newItemPosition].deviceId
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val oldProduct = oldList[oldItemPosition]
		val newProduct = newList[newItemPosition]

		return (oldProduct.deviceId == newProduct.deviceId
				&& oldProduct.candidateId == newProduct.candidateId
				&& oldProduct.country == newProduct.country
				&& oldProduct.language == newProduct.language
				&& oldProduct.device == newProduct.device
				&& oldProduct.android == newProduct.android
				&& oldProduct.time == newProduct.time)
	}
}