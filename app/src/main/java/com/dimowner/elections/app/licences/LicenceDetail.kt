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

package com.dimowner.elections.app.licences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.elections.R
import kotlinx.android.synthetic.main.activity_licence_detail.*

class LicenceDetail: AppCompatActivity() {

	companion object {
		private const val EXTRAS_KEY_LICENCE_ITEM_POS = "licence_item_pos"

		fun getStartActivity(context: Context, position: Int): Intent {
			val intent = Intent(context, LicenceDetail::class.java)
			intent.putExtra(LicenceDetail.EXTRAS_KEY_LICENCE_ITEM_POS, position)
			return intent
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_licence_detail)

		val licenceTitle: String
		val licenceLocation: String?
		if (intent.hasExtra(EXTRAS_KEY_LICENCE_ITEM_POS)) {
			val pos = intent.getIntExtra(EXTRAS_KEY_LICENCE_ITEM_POS, -1)
			if (pos > -1) {
				val licences = resources.getStringArray(R.array.licences_assets_locations)
				licenceLocation = licences[pos]
				val licenceNames = resources.getStringArray(R.array.licences_names)
				licenceTitle = licenceNames[pos]
			} else {
				licenceLocation = null
				licenceTitle = ""
			}
		} else {
			licenceLocation = null
			licenceTitle = ""
		}

		if (licenceLocation != null) {
			licenceHtmlText.loadUrl(licenceLocation)
		}

		btnBack.setOnClickListener {finish()}
		txtTitle.text = licenceTitle
	}
}