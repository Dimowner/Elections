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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.elections.R

class LicenceActivity: AppCompatActivity() {

	companion object {
		fun getStartActivity(context: Context): Intent {
			return Intent(context, LicenceActivity::class.java)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_licences)

		val btnBack = findViewById<ImageButton>(R.id.btn_back)
		btnBack.setOnClickListener { finish()}


		val licenceNames = resources.getStringArray(R.array.licences_names)
		val list = findViewById<ListView>(R.id.licence_list)
		val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, licenceNames)
		list.adapter = adapter

		list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			startActivity(LicenceDetail.getStartActivity(applicationContext, position))
		}
	}
}
