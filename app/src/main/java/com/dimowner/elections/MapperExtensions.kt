package com.dimowner.elections

import com.dimowner.elections.app.votes.ITEM_TYPE_NORMAL
import com.dimowner.elections.app.votes.VoteListItem
import com.dimowner.elections.data.model.Vote

fun Vote.toVoteListItem(): VoteListItem {
	return VoteListItem(
			this.deviceId,
			ITEM_TYPE_NORMAL,
			this.candidateName,
			this.countryCode,
			this.language,
			this.time,
			this.device
	)
}
