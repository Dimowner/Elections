package com.dimowner.elections.app.votes

const val ITEM_TYPE_NORMAL = 1001
const val ITEM_TYPE_HEADER = 1002
const val ITEM_TYPE_DATE = 1003
const val ITEM_TYPE_FOOTER = 1004

data class VoteListItem(
		val deviceId: String,
		val type: Int,
		val candidateName: String,
		val country: String,
		val language: String,
		val time: Long,
		val device: String
)
{
	companion object {

		fun createHeaderItem(): VoteListItem {
			return VoteListItem("HEADER", ITEM_TYPE_HEADER, "", "", "", 0, "")
		}

		fun createDateItem(date: Long): VoteListItem {
			return VoteListItem("DATE", ITEM_TYPE_DATE, "", "", "", date, "")
		}

		fun createFooterItem(): VoteListItem {
			return VoteListItem("FOOTER", ITEM_TYPE_FOOTER, "", "", "", 0, "")
		}
	}
}
