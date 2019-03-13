package com.dimowner.elections

import com.dimowner.elections.app.candidates.CandidatesListItem
import com.dimowner.elections.app.poll.PollListItem
import com.dimowner.elections.app.votes.VoteListItem
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.data.model.Vote

fun Vote.toVoteListItem(): VoteListItem {
	return VoteListItem(
			this.deviceId,
			com.dimowner.elections.app.votes.ITEM_TYPE_NORMAL,
			this.candidateName,
			this.countryCode,
			this.language,
			this.time,
			this.device
	)
}

fun Candidate.toCandidatesListItem(votesPerPercent: Float): CandidatesListItem {
	return CandidatesListItem(
			this.id,
			this.firstName + " " + this.surName,
			com.dimowner.elections.app.candidates.ITEM_TYPE_NORMAL,
			this.iconUrl,
			this.iconId,
			this.party,
			this.votesCount,
			this.votesCountUa,
			this.votesCountPaid,
			if (votesPerPercent > 0) (this.votesCount/votesPerPercent).toInt() else 0
	)
}

fun Candidate.toPollListItem(): PollListItem {
	return PollListItem(
			this.id,
			this.firstName + " " + this.surName,
			com.dimowner.elections.app.poll.ITEM_TYPE_NORMAL,
			this.iconUrl,
			this.iconId,
			this.party
	)
}
