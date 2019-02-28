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
import com.dimowner.elections.AppConstants
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlin.properties.Delegates

class PrefsImpl constructor(context: Context) : Prefs {

	companion object {
		const val PREF_NAME = "com.dimowner.elections.data.Prefs"

		const val PREF_KEY_IS_FIRST_RUN = "is_first_run"
	}

//	private var preferences: SharedPreferences by Delegates.notNull()
	private var preferences: Preferences by Delegates.notNull()
//	private var flowable: Flowable<String> by Delegates.notNull()

	init {
//		this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
		this.preferences = BinaryPreferencesBuilder(context)
				.name(PREF_NAME)
				.build()

//		flowable = Flowable.create({
//			preferences.registerOnSharedPreferenceChangeListener { sharedPrefs, key ->
//				it.onNext(key)
//			}
//		}, BackpressureStrategy.LATEST)
	}

	override fun isFirstRun(): Boolean {
		return !preferences.contains(PREF_KEY_IS_FIRST_RUN) || preferences.getBoolean(PREF_KEY_IS_FIRST_RUN, false)
	}

	override fun setFirstRunExecuted() {
		val editor = preferences.edit()
		editor.putBoolean(PREF_KEY_IS_FIRST_RUN, false)
		editor.apply()
	}
}
