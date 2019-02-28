package com.dimowner.elections.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "votes")
data class Vote(
	@PrimaryKey(autoGenerate = false)
	@SerializedName("deviceId")
	val deviceId: String,
	@SerializedName("candidateId")
	val candidateId: Int,
	@SerializedName("country")
	val country: String,
	@SerializedName("language")
	val language: String,
	@SerializedName("locality")
	val locality: String,
	@SerializedName("time")
	val time: Long,
	@SerializedName("age")
	val age: Int,
	@SerializedName("device")
	val device: String,
	@SerializedName("android")
	val android: Int
)
{
	constructor() : this("SKJFKLSDF38K4", 1, "UA", "UA", "Kyiv", 155119520,
			24, "Samsung Galaxy S7", 27)

//	@PrimaryKey(autoGenerate = true)
//	@SerializedName("id")
//	var id: Int = 0
}

//"deviceId": "SKJFKLSDF38K4",
//"candidateId": 1,
//"country": "UA",
//"language": "UA",
//"locality": "Kyiv",
//"time": 155119520,
//"age": 24,
//"device": "Samsung Galaxy S7",
//"android": 27