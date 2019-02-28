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

import com.dimowner.elections.GWApplication
import com.dimowner.elections.app.main.MainActivity
import com.dimowner.elections.app.main.CandidatesListFragment
import com.dimowner.elections.app.settings.SettingsActivity
import com.dimowner.elections.app.settings.SettingsFragment
import com.dimowner.elections.app.welcome.WelcomeActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(AppModule::class))
@Singleton
interface AppComponent {

	fun inject(app: GWApplication)

	fun inject(activity: MainActivity)

	fun inject(activity: WelcomeActivity)

	fun inject(activity: SettingsActivity)

	fun inject(activity: SettingsFragment)

	fun inject(fragment: CandidatesListFragment)

}
