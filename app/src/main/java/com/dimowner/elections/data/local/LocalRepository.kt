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

package com.dimowner.elections.data.local

import com.dimowner.elections.data.local.room.AppDatabase
import com.dimowner.elections.data.local.room.CandidateEntity
import com.dimowner.elections.data.repository.Repository
import io.reactivex.Flowable

class LocalRepository(private val appDatabase: AppDatabase) : Repository {

	override fun subscribeCandidates(): Flowable<List<CandidateEntity>> {
		return appDatabase.electionsDao().subscribeCandidates()
	}

	override fun cacheCandidates(entity: List<CandidateEntity>) {
		if (entity.isNotEmpty()) {
			appDatabase.electionsDao().delete()
			appDatabase.electionsDao().insertAll(*entity.toTypedArray())
		}
	}
}
