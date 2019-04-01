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

package com.dimowner.elections.app.votes

import com.dimowner.elections.data.Callback
import com.dimowner.elections.data.Prefs
import com.dimowner.elections.data.Repository
import com.dimowner.elections.toVoteListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VotesListPresenter(
		private val repository: Repository,
		private val prefs: Prefs) : VotesListContract.UserActionsListener {

	private var view: VotesListContract.View? = null

	private val disposable: CompositeDisposable by lazy { CompositeDisposable() }

	override fun bindView(view: VotesListContract.View) {
		this.view = view
	}

	override fun unbindView() {
		this.view = null
		this.disposable.dispose()
	}

	override fun loadVotes() {
		view?.showProgress()
		view?.showSmallProgress()
		disposable.add(repository.subscribeVotes(object : Callback {
			override fun onRemote() {
				view?.hideSmallProgress()
			}
		})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ d ->
					val list: MutableList<VoteListItem> = ArrayList(d.size)
					for (item in d) {
						list.add(item.toVoteListItem())
					}
					view?.showCandidatesList(list)
					view?.hideProgress()
				}, {
					view?.hideProgress()
					Timber.e(it)
					view?.showError(it.message!!)
				}))
	}
}
