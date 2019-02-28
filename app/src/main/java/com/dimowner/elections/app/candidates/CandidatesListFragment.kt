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

package com.dimowner.elections.app.candidates

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dimowner.elections.R
import com.dimowner.elections.GWApplication
import com.dimowner.elections.data.model.Candidate
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class CandidatesListFragment : Fragment(), CandidatesListContract.View {

	companion object {
		fun newInstance(): CandidatesListFragment {
			return CandidatesListFragment()
		}
	}

	@Inject
	lateinit var presenter: CandidatesListContract.UserActionsListener

	val adapter: CandidatesListAdapter by lazy { CandidatesListAdapter() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_list, container, false)
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		recyclerView.setHasFixedSize(true)
		recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
		recyclerView.adapter = adapter

		GWApplication.get(view.context).applicationComponent().inject(this)
		presenter.bindView(this)
		presenter.loadCandidates()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		presenter.unbindView()
	}

	override fun showCandidatesList(list: List<Candidate>) {
		adapter.setData(list)
	}

	override fun showProgress() {
	}

	override fun hideProgress() {
	}

	override fun showError(message: String) {
//		Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
		Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
	}

	override fun showError(resId: Int) {
		Toast.makeText(activity?.applicationContext, resId, Toast.LENGTH_LONG).show()
//		Snackbar.make(container, resId, Snackbar.LENGTH_LONG).show()
	}
}