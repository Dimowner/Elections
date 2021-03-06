/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this
 * file to you under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dimowner.elections.data

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates

class PrefsImpl constructor(context: Context) : Prefs {

	companion object {
		const val PREF_NAME = "com.dimowner.elections.data.Prefs"

		const val PREF_KEY_IS_FIRST_RUN = "is_first_run"
		const val PREF_KEY_COUNTRY_CODE = "pref_county_code"
		const val PREF_KEY_COUNTRY_NAME = "pref_county_name"
		const val PREF_KEY_CITY = "pref_city"
		const val PREF_KEY_DEVICE_VOTED = "pref_device_voted"
		const val PREF_KEY_IS_SHOW_IMAGE_PREVIEW_INSTRUCTIONS = "is_show_image_preview_instructions"

		const val DEVICE_VOTED_YES: Int = 200
		const val DEVICE_VOTED_NO: Int = 400
		const val DEVICE_VOTED_UNKNOWN: Int = 403
	}

	private var preferences: SharedPreferences by Delegates.notNull()

	init {
		this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
	}

	override fun isFirstRun(): Boolean {
		return !preferences.contains(PREF_KEY_IS_FIRST_RUN) || preferences.getBoolean(PREF_KEY_IS_FIRST_RUN, false)
	}

	override fun setFirstRunExecuted() {
		val editor = preferences.edit()
		editor.putBoolean(PREF_KEY_IS_FIRST_RUN, false)
		editor.apply()
	}

	override fun isDeviceVoted(): Int {
		if (!preferences.contains(PREF_KEY_DEVICE_VOTED)) {
			return DEVICE_VOTED_UNKNOWN
		} else if (preferences.contains(PREF_KEY_DEVICE_VOTED)) {
			return preferences.getInt(PREF_KEY_DEVICE_VOTED, DEVICE_VOTED_NO)
		}
		return DEVICE_VOTED_UNKNOWN
	}

	override fun setDeviceVoted(voted: Int) {
		val editor = preferences.edit()
		editor.putInt(PREF_KEY_DEVICE_VOTED, voted)
		editor.apply()
	}

	override fun setCountryCode(code: String) {
		val editor = preferences.edit()
		editor.putString(PREF_KEY_COUNTRY_CODE, code)
		editor.apply()
	}

	override fun getCountryCode(): String {
		return if (preferences.contains(PREF_KEY_COUNTRY_CODE)) {
			preferences.getString(PREF_KEY_COUNTRY_CODE, "") ?: ""
		} else {
			""
		}
	}

	override fun setCountryName(name: String) {
		val editor = preferences.edit()
		editor.putString(PREF_KEY_COUNTRY_NAME, name)
		editor.apply()
	}

	override fun getCountryName(): String {
		return if (preferences.contains(PREF_KEY_COUNTRY_NAME)) {
			preferences.getString(PREF_KEY_COUNTRY_NAME, "") ?: ""
		} else {
			""
		}
	}

	override fun setCity(city: String) {
		val editor = preferences.edit()
		editor.putString(PREF_KEY_CITY, city)
		editor.apply()
	}

	override fun getCity(): String {
		return if (preferences.contains(PREF_KEY_CITY)) {
			preferences.getString(PREF_KEY_CITY, "") ?: ""
		} else {
			""
		}
	}

	override fun isShowImagePreviewInstructions(): Boolean {
		return !preferences.contains(PREF_KEY_IS_SHOW_IMAGE_PREVIEW_INSTRUCTIONS)
				|| preferences.getBoolean(PREF_KEY_IS_SHOW_IMAGE_PREVIEW_INSTRUCTIONS, true)
	}

	override fun setShowImagePreviewInstructions(b: Boolean) {
		val editor = preferences.edit()
		editor.putBoolean(PREF_KEY_IS_SHOW_IMAGE_PREVIEW_INSTRUCTIONS, b)
		editor.apply()
	}
}
