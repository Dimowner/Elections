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

package com.dimowner.elections.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.data.model.Vote
import io.reactivex.Flowable

@Dao
interface ElectionsDao {

	@Query("SELECT * FROM candidates ORDER BY id ASC")
	fun subscribeCandidates(): Flowable<List<Candidate>>

	@Query("SELECT * FROM candidates ORDER BY votesCount DESC")
	fun subscribeResults(): Flowable<List<Candidate>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertCandidate(item: Candidate)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(vararg items: Candidate)

	@Query("DELETE FROM candidates")
	fun deleteCandidates()

	@Query("SELECT * FROM votes")
	fun subscribeVotes(): Flowable<List<Vote>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertVotes(item: Vote)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(vararg items: Vote)

	@Query("DELETE FROM votes")
	fun deleteVotes()
}
