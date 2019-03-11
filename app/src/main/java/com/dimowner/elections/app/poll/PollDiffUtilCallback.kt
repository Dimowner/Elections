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

import androidx.recyclerview.widget.DiffUtil

/**
 * Created on 02.03.2019.
 * @author Dimowner
 */
class PollDiffUtilCallback(private val oldList: List<PollListItem>, private val newList: List<PollListItem>) : DiffUtil.Callback() {

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
				&& oldProduct.name == newProduct.name
				&& oldProduct.type == newProduct.type
				&& oldProduct.iconUrl == newProduct.iconUrl
				&& oldProduct.iconId == newProduct.iconId
				&& oldProduct.party == newProduct.party)
	}
}