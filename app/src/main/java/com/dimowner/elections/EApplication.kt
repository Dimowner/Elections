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

package com.dimowner.elections

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.dimowner.elections.dagger.application.AppComponent
import com.dimowner.elections.dagger.application.AppModule
import com.dimowner.elections.dagger.application.DaggerAppComponent
import com.dimowner.elections.util.AndroidUtils
import timber.log.Timber

const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
/**
 * Created on 16.01.2018.
 * @author Dimowner
 */
class EApplication : Application() {

	private var networkStateChangeReceiver: NetworkStateChangeReceiver? = null


	companion object {
		fun get(context: Context): EApplication {
			return context.applicationContext as EApplication
		}

		private var isConnectedToNetwork = false

		fun isConnected(): Boolean {
			return isConnectedToNetwork
		}
	}

	private val appComponent: AppComponent by lazy {
		DaggerAppComponent
				.builder()
				.appModule(AppModule(this))
				.build()
	}

	override fun onCreate() {
		super.onCreate()

		appComponent.inject(this)
//		appComponent = prepareAppComponent().build()

		if (BuildConfig.DEBUG) {
			//Timber initialization
			Timber.plant(object : Timber.DebugTree() {
				override fun createStackElementTag(element: StackTraceElement): String {
					return super.createStackElementTag(element) + ":" + element.lineNumber
				}
			})
		}

		val intentFilter = IntentFilter()
		intentFilter.addAction(CONNECTIVITY_ACTION)
		networkStateChangeReceiver = NetworkStateChangeReceiver()
		registerReceiver(networkStateChangeReceiver, intentFilter)
	}

//	operator fun get(context: Context): EApplication {
//		return context.applicationContext as EApplication
//	}

//	private fun prepareAppComponent(): DaggerAppComponent.Builder {
//		return DaggerAppComponent.builder()
//				.appModule(AppModule(this))
//	}

	override fun onTerminate() {
		super.onTerminate()
		unregisterReceiver(networkStateChangeReceiver)
	}

	override fun onLowMemory() {
		super.onLowMemory()
		Timber.d("onLowMemory")
	}

	override fun onTrimMemory(level: Int) {
		super.onTrimMemory(level)
		Timber.d("onTrimMemory level = %s", level)
	}

	fun applicationComponent(): AppComponent {
		return appComponent
	}


	private inner class NetworkStateChangeReceiver : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			val actionOfIntent = intent.action
			if (actionOfIntent == CONNECTIVITY_ACTION) {
				if (AndroidUtils.isConnectedToNetwork(context)) {
					Timber.d("network state changed - Connected")
					isConnectedToNetwork = true
				} else {
					Timber.d("network state changed - Disconnected")
					isConnectedToNetwork = false
				}
			}
		}
	}
}
