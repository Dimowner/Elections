package com.dimowner.elections.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "votes")
data class Vote(
	@PrimaryKey(autoGenerate = false)
	@SerializedName("deviceId")
	var deviceId: String,
	@SerializedName("candidateId")
	var candidateId: Int,
	@SerializedName("candidateName")
	var candidateName: String,
	@SerializedName("countryCode")
	var countryCode: String,
	@SerializedName("countryName")
	var countryName: String,
	@SerializedName("language")
	var language: String,
	@SerializedName("locality")
	var locality: String,
	@SerializedName("time")
	var time: Long,
	@SerializedName("device")
	var device: String,
	@SerializedName("android")
	var android: Int
) {
	constructor() : this("", -1, "", "", "", "", "", 0, "", 1)
}