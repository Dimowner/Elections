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

package com.dimowner.elections.data.repository

import com.dimowner.elections.data.local.LocalRepository
import com.dimowner.elections.data.local.room.CandidateEntity
import com.dimowner.elections.data.remote.RemoteRepository
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RepositoryImpl(
		private val localRepository: LocalRepository,
		private val remoteRepository: RemoteRepository
) : Repository {

	override fun subscribeCandidates(): Flowable<List<CandidateEntity>> {
		remoteRepository.subscribeCandidates()
				.subscribeOn(Schedulers.io())
				.subscribe({ response ->
					localRepository.cacheCandidates(response)
				}, Timber::e)
		return localRepository.subscribeCandidates()
				.subscribeOn(Schedulers.io())
	}

	override fun cacheCandidates(entity: List<CandidateEntity>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
