package com.dimowner.elections.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "candidates")
data class Candidate(
		@PrimaryKey(autoGenerate = false)
		@SerializedName("id")
		var id: Int = 0,
		@SerializedName("firstName")
		val firstName: String,
		@SerializedName("midName")
		val midName: String,
		@SerializedName("surName")
		val surName: String,
		@SerializedName("iconUrl")
		val iconUrl: String,
		@SerializedName("iconId")
		val iconId: String,
		@SerializedName("number")
		val number: Int,
		@SerializedName("party")
		val party: String,
		@SerializedName("votesCount")
		var votesCount: Int,
		@SerializedName("votesCountUa")
		var votesCountUa: Int,
		@SerializedName("votesCountPaid")
		var votesCountPaid: Int
)
{
	constructor() : this(-1, "", "", "", "", "", 0, "", 0, 0, 0)
}
