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

package com.dimowner.elections.dagger.application

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import android.content.Context
import com.dimowner.elections.AppConstants.Companion.LOCAL_DATABASE_NAME
import com.dimowner.elections.data.remote.FirebaseDatasource
import com.dimowner.elections.data.Prefs
import com.dimowner.elections.data.local.LocalRepositoryImpl
import com.dimowner.elections.data.local.room.AppDatabase
import com.dimowner.elections.data.Repository
import com.dimowner.elections.data.RepositoryImpl
import com.dimowner.elections.places.PlacesProvider
import com.dimowner.elections.app.candidates.CandidatesListContract
import com.dimowner.elections.app.candidates.CandidatesListPresenter
import com.dimowner.elections.app.poll.PollContract
import com.dimowner.elections.app.poll.PollPresenter
import com.dimowner.elections.app.settings.SettingsContract
import com.dimowner.elections.app.settings.SettingsPresenter
import com.dimowner.elections.app.votes.VotesListContract
import com.dimowner.elections.app.votes.VotesListPresenter
import com.dimowner.elections.app.welcome.WelcomePresenter
import com.dimowner.elections.data.PrefsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(
		var appContext: Context
) {

	@Provides
	@Singleton
	internal fun provideContext(): Context {
		return appContext
	}

	@Provides
	@Singleton
	internal fun providePrefs(context: Context): Prefs {
		return PrefsImpl(context)
	}

	@Provides
	internal fun provideWelcomePresenter(prefs: Prefs, context: Context, locationProvider: PlacesProvider): WelcomePresenter {
		return WelcomePresenter(prefs, context, locationProvider)
	}

	@Provides
	internal fun provideSettingsPresenter(prefs: Prefs, context: Context): SettingsContract.UserActionsListener {
		return SettingsPresenter(prefs, context)
	}

	@Provides
	internal fun provideCandidatesListPresenter(repository: Repository, prefs: Prefs): CandidatesListContract.UserActionsListener {
		return CandidatesListPresenter(repository, prefs)
	}

	@Provides
	internal fun provideVotesListPresenter(repository: Repository, prefs: Prefs): VotesListContract.UserActionsListener {
		return VotesListPresenter(repository, prefs)
	}

	@Provides
	internal fun providePollPresenter(repository: Repository, prefs: Prefs): PollContract.UserActionsListener {
		return PollPresenter(repository, prefs)
	}

	@Provides
	@Singleton
	internal fun provideLocalRepository(appDatabase: AppDatabase): LocalRepositoryImpl {
		return LocalRepositoryImpl(appDatabase)
	}

	@Provides
	@Singleton
	internal fun provideFirebaseHandler(): FirebaseDatasource {
		return FirebaseDatasource()
	}

	@Provides
	@Singleton
	internal fun provideRepository(localRepository: LocalRepositoryImpl,
											 firebaseHandler: FirebaseDatasource): Repository {
		return RepositoryImpl(localRepository, firebaseHandler)
	}

	@Provides
	@Singleton
	internal fun provideLocationProvider(context: Context): PlacesProvider {
		return PlacesProvider(context)
	}

	@Provides
	@Singleton
	internal fun provideAppDatabase(context: Context): AppDatabase {
		return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, LOCAL_DATABASE_NAME)
//				.fallbackToDestructiveMigration()
				.addMigrations(MIGRATION_1_2)
				.build()
	}

	/**
	 * Migrate from:
	 * version 1 - using the SQLiteDatabase API
	 * to
	 * version 2 - using Room
	 */
	private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
		override fun migrate(database: SupportSQLiteDatabase) {
			//Migration code here
		}
	}
}
