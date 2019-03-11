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

package com.dimowner.elections.app.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dimowner.elections.R

/**
 * Second page of onboarding showing a welcome message & branding animation.
 */
class Welcome2Fragment : Fragment() {

	lateinit var fragmentView: View

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		fragmentView = inflater.inflate(R.layout.fragment_welcome2, container, false)
		fragmentView.findViewById<TextView>(R.id.txtTitle).apply {
			this.setText(R.string.only_here)
			this.visibility = View.VISIBLE
		}
		fragmentView.findViewById<TextView>(R.id.txtDetails).apply {
			this.setText(R.string.fair_elections)
			this.visibility = View.VISIBLE
		}
		return fragmentView
	}
}
