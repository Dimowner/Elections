package com.dimowner.elections.app.poll

import androidx.recyclerview.widget.DiffUtil
import com.dimowner.elections.data.model.Candidate

/**
 * Created on 02.03.2019.
 * @author Dimowner
 */
class PollDiffUtilCallback(private val oldList: List<Candidate>, private val newList: List<Candidate>) : DiffUtil.Callback() {

	override fun getOldListSize(): Int {
		return oldList.size
	}

	override fun getNewListSize(): Int {
		return newList.size
	}

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition].id == newList[newItemPosition].id
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val oldProduct = oldList[oldItemPosition]
		val newProduct = newList[newItemPosition]

		return (oldProduct.id == newProduct.id
				&& oldProduct.firstName == newProduct.firstName
				&& oldProduct.midName == newProduct.midName
				&& oldProduct.surName == newProduct.surName
				&& oldProduct.number == newProduct.number
				&& oldProduct.party == newProduct.party
				&& oldProduct.votesCount == newProduct.votesCount)
	}
}