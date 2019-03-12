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

package com.dimowner.elections.app.poll

import android.content.Context
import com.dimowner.elections.EApplication
import com.dimowner.elections.data.Prefs
import com.dimowner.elections.data.Repository
import com.dimowner.elections.data.model.Vote
import com.dimowner.elections.toPollListItem
import com.dimowner.elections.util.AndroidUtils
import com.google.firebase.database.ServerValue
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*

class PollPresenter(
		private val repository: Repository,
		private val prefs: Prefs) : PollContract.UserActionsListener {

	private var view: PollContract.View? = null

	private val disposable: CompositeDisposable by lazy { CompositeDisposable() }

	override fun bindView(view: PollContract.View) {
		this.view = view
	}

	override fun unbindView() {
		this.view = null
		this.disposable.dispose()
	}

	override fun loadCandidates() {
		view?.showProgress()
		disposable.add(repository.subscribeCandidates()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ d ->
					val list: MutableList<PollListItem> = ArrayList(d.size)
					for (item in d) {
						list.add(item.toPollListItem())
					}
					view?.showCandidatesList(list)
					view?.hideProgress()
				}, {
					view?.hideProgress()
					Timber.e(it)
					view?.showError(it.message ?: "Error!")
				}))
	}

	override fun vote(context: Context, id: Int, name: String) {
		if (EApplication.isConnected()) {
			view?.showProgress()
			val vote = Vote(
					AndroidUtils.getDeviceIdentifier(context),
					id,
					name,
					prefs.getCountryCode(),
					prefs.getCountryName(),
					AndroidUtils.getDisplayLanguage(context),
					prefs.getCity(),
					//ServerValue.TIMESTAMP,
					Date().time, //TODO: use server time
					AndroidUtils.getBrandModel(),
					AndroidUtils.getAndroidVersion()
			)
			disposable.add(repository.vote(vote)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe({
						prefs.setFirstRunExecuted()
						view?.startMainScreen()
						view?.hideProgress()
					}, {
						view?.hideProgress()
						view?.showError(it.message ?: "Error!")
					}))
		} else {
			view?.showNoConnectionMessage()
		}
	}

}
