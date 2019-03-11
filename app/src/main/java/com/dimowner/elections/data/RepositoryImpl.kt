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

import com.dimowner.elections.data.local.LocalRepository
import com.dimowner.elections.data.remote.FirebaseDatasource
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.data.model.Vote
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RepositoryImpl(
		private val localRepository: LocalRepository,
		private val firebase: FirebaseDatasource
) : Repository {

	val compositeDisposable: CompositeDisposable = CompositeDisposable()

	override fun subscribeCandidates(): Flowable<List<Candidate>> {
		compositeDisposable.add(firebase.candidates
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe({ response ->
					localRepository.cacheCandidates(response)
				}, Timber::e))
		return localRepository.subscribeCandidates()
				.filter { !it.isEmpty() }
//				.timeout(AppConstants.LOADING_TIMEOUT, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
	}

	override fun subscribeResults(): Flowable<List<Candidate>> {
		compositeDisposable.add(firebase.candidates
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe({ response ->
					localRepository.cacheCandidates(response)
				}, Timber::e))
		return localRepository.subscribeResults()
				.filter { !it.isEmpty() }
//				.timeout(AppConstants.LOADING_TIMEOUT, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
	}

	override fun subscribeVotes(): Flowable<List<Vote>> {
		compositeDisposable.add(firebase.votes
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe({ response ->
					localRepository.cacheVotes(response)
				}, Timber::e))
		return localRepository.subscribeVotes()
				.filter { !it.isEmpty() }
//				.timeout(AppConstants.LOADING_TIMEOUT, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
	}

	override fun vote(vote: Vote): Completable {
		return firebase.vote(vote)
	}

	override fun clear() {
		compositeDisposable.clear()
	}
}
