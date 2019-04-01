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

import com.dimowner.elections.BuildConfig;
import com.dimowner.elections.EApplication;
import com.dimowner.elections.data.model.Candidate;
import com.dimowner.elections.data.model.Vote;
import com.dimowner.elections.data.model.VoteRequest;
import com.dimowner.elections.exceptions.NullAuthTokenException;
import com.dimowner.elections.exceptions.SignInException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FirebaseDatasource {

	private static final String CANDIDATES_TABLE = "candidates";
	private static final String USERS_TABLE = BuildConfig.SECRET_CODE;
	private static final String VOTES_TABLE = "votes";
	private static final String FIELD_VOTE_COUNTER = "voteCount";
	private static final String FIELD_TIME = "time";
	private static final int MAX_COUNT = 300;

	private FirebaseDatabase database;
	private DatabaseReference candidatesRef;
	private DatabaseReference votesRef;
	private DatabaseReference usersRef;
	private FirebaseAuth auth;

	public FirebaseDatasource() {
		this.database = FirebaseDatabase.getInstance();
		this.candidatesRef = database.getReference(CANDIDATES_TABLE);
		this.votesRef = database.getReference(VOTES_TABLE);
		this.usersRef = database.getReference(USERS_TABLE);
		this.auth = FirebaseAuth.getInstance();
	}

	private Single<String> getAuthUid() {
		FirebaseUser user = auth.getCurrentUser();
		if (user != null) {
			return Single.create(emitter -> user
					.getIdToken(false)
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							if (task.getResult() != null && task.getResult().getToken() != null) {
								emitter.onSuccess(user.getUid());
							} else {
								emitter.onError(new NullAuthTokenException());
							}
						} else {
							emitter.onError(task.getException());
						}
					}));
		} else {
			return signInAnonymously();
		}
	}

	private Single<String> signInAnonymously() {
		return Single.<AuthResult>create(emitter -> callTask(auth.signInAnonymously(), emitter))
				.flatMap(authResult -> {
					String uid = authResult.getUser().getUid();
					return Single.<String>create(emitter -> {
							usersRef.child(uid).setValue(true)
								.addOnSuccessListener(aVoid -> {
									emitter.onSuccess(uid);
								})
								.addOnFailureListener(command -> {
									emitter.onError(new SignInException());
								});
					});
				})
				.subscribeOn(Schedulers.io());
	}

	public Single<Boolean> checkDeviceVoted() {
		return getAuthUid()
				.flatMap(authResult -> {
					return Single.<Boolean>create(emitter -> {
						votesRef.child(EApplication.Companion.getDeviceId()).addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								if (dataSnapshot.exists()) {
									emitter.onSuccess(true);
								} else {
									emitter.onSuccess(false);
								}
							}

							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {
								emitter.onError(new Exception(databaseError.getMessage()));
							}
						});
					});
				})
				.subscribeOn(Schedulers.io());
	}

	public Completable vote(VoteRequest vote) {
		Timber.d("vote: %s", vote.toString());
		return getAuthUid().flatMapCompletable(uid ->
				Completable.create(emitter -> callTask(votesRef.child(vote.getDeviceId())
						.setValue(vote), emitter))
						.andThen(Completable.create(emitter ->
								candidatesRef.child(String.valueOf(vote.getCandidateId()))
										.runTransaction(new Transaction.Handler() {
												@NonNull
												@Override
												public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
													Candidate c = mutableData.getValue(Candidate.class);
													if (c == null) {
														return Transaction.success(mutableData);
													}
													c.setVotesCount(c.getVotesCount() + 1);
													if (vote.getCountryCode().trim().equalsIgnoreCase("UA")) {
														c.setVotesCountUa(c.getVotesCountUa() + 1);
													}
													// Set value and report transaction success
													mutableData.setValue(c);
													return Transaction.success(mutableData);
												}

												@Override
												public void onComplete(@Nullable DatabaseError databaseError,
																			  boolean succeed, @Nullable DataSnapshot dataSnapshot) {
													if (succeed) {
														emitter.onComplete();
													} else {
														if (databaseError != null) {
															emitter.onError(new Exception(databaseError.getMessage()));
														}
													}
												}
											}))).subscribeOn(Schedulers.io()));
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
			return getAuthUid().toFlowable().flatMap(uid -> {
					if (uid != null && !uid.isEmpty()) {
						return Flowable.create(new FlowableValueOnSubscribe(
								votesRef.orderByChild(FIELD_TIME).limitToLast(MAX_COUNT)), BackpressureStrategy.LATEST)
								.map(dataSnapshot -> {
									List<Vote> list = new LinkedList<>();
									for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
										Vote d = postSnapshot.getValue(Vote.class);
										if (d != null) {
											list.add(0, d);
										}
									}
									return list;
								});
					} else {
						return Flowable.error(new NullAuthTokenException());
					}
				});
		} catch (Exception e) {
			Timber.e(e);
			return Flowable.error(e);
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

	public static <Result> Task<Result> callTask(Task<Result> task, CompletableEmitter e) {
		return task
				.addOnSuccessListener(aVoid -> {
					if (!e.isDisposed()) e.onComplete();
				})
				.addOnFailureListener(command -> {
					if (!e.isDisposed()) e.onError(command);
				});
	}

	public static <Result> Task<Result> callTask(Task<Result> task, SingleEmitter<Result> e) {
		return task
				.addOnSuccessListener(result -> {
					if (!e.isDisposed()) e.onSuccess(result);
				})
				.addOnFailureListener(command -> {
					if (!e.isDisposed()) e.onError(getExceptionFromTask(task));
				});
	}

	public static Exception getExceptionFromTask(Task task) {
		Exception exception = task.getException();
		return exception == null ? new NullPointerException() : exception;
	}
}
