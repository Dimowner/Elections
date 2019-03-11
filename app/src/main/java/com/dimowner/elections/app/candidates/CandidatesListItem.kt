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

const val ITEM_TYPE_NORMAL = 101
const val ITEM_TYPE_HEADER = 102
const val ITEM_TYPE_FOOTER = 103

data class CandidatesListItem(
		var id: Int = 100,
		val name: String,
		val type: Int,
		val iconUrl: String,
		val iconId: String,
		val party: String,
		val votesCount: Int,
		val votesCountUa: Int,
		val votesCountPaid: Int,
		val votesPercent: Int
)
{
	companion object {

		fun createHeaderItem(): CandidatesListItem {
			return CandidatesListItem(101, "HEADER", ITEM_TYPE_HEADER, "", "", "", 0, 0, 0, 0)
		}

		fun createFooterItem(): CandidatesListItem {
			return CandidatesListItem(102, "FOOTER", ITEM_TYPE_FOOTER, "", "", "", 0, 0, 0, 0)
		}
	}
}
