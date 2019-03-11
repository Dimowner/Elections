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
		val votesCount: Int,
		@SerializedName("votesCountUa")
		val votesCountUa: Int,
		@SerializedName("votesCountPaid")
		val votesCountPaid: Int
)
{
	constructor() : this(0, "Andriy", "Ivanovich", "Morozenko",
			"https://www.site.com/img.png",
			"avatar_4", 4, "Power Force", 853, 700, 153)
}
