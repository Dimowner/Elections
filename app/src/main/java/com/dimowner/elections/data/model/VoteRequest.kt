package com.dimowner.elections.data.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class VoteRequest(
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
		var time: Map<String, String>,
		@SerializedName("device")
		var device: String,
		@SerializedName("android")
		var android: Int
)
