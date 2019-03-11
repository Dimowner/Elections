package com.dimowner.elections.data.local

import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.data.model.Vote
import io.reactivex.Flowable

interface LocalRepository {

	fun subscribeCandidates(): Flowable<List<Candidate>>

	fun subscribeResults(): Flowable<List<Candidate>>

	fun cacheCandidates(entity: List<Candidate>)

	fun subscribeVotes(): Flowable<List<Vote>>

	fun cacheVotes(entity: List<Vote>)
}