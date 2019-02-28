/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dimowner.elections.data.remote;

import com.dimowner.elections.data.model.Candidate;
import com.dimowner.elections.data.model.Vote;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

public class FirebaseDatasource {

	private static final String CANDIDATES_TABLE = "candidates";
	private static final String VOTES_TABLE = "votes";
	private static final String FIELD_VOTE_COUNTER = "voteCount";
	private static final String FIELD_TIME = "time";
	private static final int MAX_COUNT = 100;

	private FirebaseDatabase database;
	private DatabaseReference candidatesRef;
	private DatabaseReference votesRef;

	public FirebaseDatasource() {
		this.database = FirebaseDatabase.getInstance();
		this.candidatesRef = database.getReference(CANDIDATES_TABLE);
		this.votesRef = database.getReference(VOTES_TABLE);
	}

	public boolean vote(int id) {
//		if (!BuildConfig.DEBUG) {
			Timber.d("vote id: " + id);
			try {
				final DatabaseReference ref = candidatesRef.child(String.valueOf(id)).child(FIELD_VOTE_COUNTER);
				ref.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						String d = dataSnapshot.getValue(String.class);
						if (d != null) {
//							ref.setValue(d + "n1");
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Timber.e(databaseError.toString());
					}
				});
			} catch (Exception e) {
				Timber.e(e);
			}
//		}
		return true;
	}

	public Single<List<Candidate>> getCandidates() {
		try {
			return Single.create(new SingleValueOnSubscribe(candidatesRef.orderByChild(FIELD_VOTE_COUNTER).limitToLast(MAX_COUNT)))
					.map(dataSnapshot -> {
						List<Candidate> list = new LinkedList<>();
						for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
							Candidate d = postSnapshot.getValue(Candidate.class);
							if (d != null) {
								Timber.v("Candidate: %s", d.toString());
								list.add(0, d);
							}
						}
						return list;
			});
		} catch (Exception e) {
			Timber.e(e);
			return Single.just(new ArrayList<>());
		}
	}

	public Flowable<List<Vote>> getVotes() {
		try {
			return Flowable.create(new FlowableValueOnSubscribe(
					votesRef.orderByChild(FIELD_TIME).limitToLast(MAX_COUNT)), BackpressureStrategy.LATEST)
					.map(dataSnapshot -> {
						List<Vote> list = new LinkedList<>();
						for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
							Vote d = postSnapshot.getValue(Vote.class);
							if (d != null) {
								list.add(0, d);
							}
						}
						return list;
					});
		} catch (Exception e) {
			Timber.e(e);
			return Flowable.just(new ArrayList<>());
		}
	}

	public static class SingleValueOnSubscribe implements SingleOnSubscribe<DataSnapshot> {

		private Query query;

		public SingleValueOnSubscribe(Query query) {
			this.query = query;
		}

		@Override
		public void subscribe(@io.reactivex.annotations.NonNull SingleEmitter<DataSnapshot> e) throws Exception {
			ValueEventListener listener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (!e.isDisposed()) e.onSuccess(dataSnapshot);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					if (!e.isDisposed()) e.onError(databaseError.toException());
				}
			};

			e.setDisposable(Disposables.fromRunnable(() -> {
				if (query != null) {
					query.removeEventListener(listener);
					query = null;
				}
			}));

			if (query != null) {
				query.addListenerForSingleValueEvent(listener);
			}
		}
	}

	public static class FlowableValueOnSubscribe implements FlowableOnSubscribe<DataSnapshot> {

		private Query query;

		public FlowableValueOnSubscribe(Query query) {
			this.query = query;
		}

		@Override
		public void subscribe(@NonNull FlowableEmitter<DataSnapshot> e) throws Exception {
			ValueEventListener listener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (!e.isCancelled()) e.onNext(dataSnapshot);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					if (!e.isCancelled()) {
						e.onError(databaseError.toException());
					}
				}
			};

			e.setDisposable(Disposables.fromRunnable(() -> {
				if (query != null) {
					query.removeEventListener(listener);
					query = null;
				}
			}));

			if (query != null) {
				query.addValueEventListener(listener);
			}
		}
	}
}
