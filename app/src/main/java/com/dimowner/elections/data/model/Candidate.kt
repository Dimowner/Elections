package com.dimowner.elections.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "candidates")
data class Candidate(
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
		val votesCount: Int
)
{
	constructor() : this("Andriy", "Ivanovich", "Morozenko",
			"https://www.thecocktaildb.com/images/media/drink/qyyvtu1468878544.jpg",
			"avatar_4", 4, "Power Force", 853)

	@PrimaryKey(autoGenerate = true)
	@SerializedName("id")
	var id: Int = 0
}

//"id": 1,
//"firstName": "Andriy",
//"midName": "Ivanovich",
//"surName": "Morozenko",
//"iconUrl": "https://www.thecocktaildb.com/images/media/drink/qyyvtu1468878544.jpg",
//"iconId": "avatar_4",
//"number": 4,
//"party": "Power Force",
//"votesCount": 853