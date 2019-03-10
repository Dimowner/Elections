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

package com.dimowner.elections.app.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dimowner.elections.EApplication
import com.dimowner.elections.R
import com.dimowner.elections.app.main.MainActivity
import com.dimowner.elections.app.poll.PollActivity
import com.dimowner.elections.util.AndroidUtils
import com.dimowner.elections.util.AnimationUtil
import kotlinx.android.synthetic.main.activity_welcome.*
import timber.log.Timber
import javax.inject.Inject

private const val AUTO_ADVANCE_DELAY = 6_000L
private const val INITIAL_ADVANCE_DELAY = 3_000L
private const val REQ_CODE_LOCATION = 303

class WelcomeActivity : AppCompatActivity(), WelcomeContract.View, ViewPager.OnPageChangeListener  {

	companion object {
		fun getStartActivity(context: Context): Intent {
			return Intent(context, WelcomeActivity::class.java)
		}
	}

	val tridentOffset = AndroidUtils.dpToPx(-136)
	lateinit var adapter: OnboardingAdapter

	private lateinit var pagerPager: ViewPagerPager

	private val handler = Handler()

	// Auto-advance the view pager to give overview of app benefits
	private val advancePager: Runnable = object : Runnable {
		override fun run() {
			pagerPager.advance()
			handler.postDelayed(this, AUTO_ADVANCE_DELAY)
		}
	}

	@Inject
	lateinit var presenter: WelcomePresenter

	private var isAnimated = false

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)
		EApplication.get(applicationContext).applicationComponent().inject(this)

		// immersive mode so images can draw behind the status bar
//		val decor = window.decorView
//		val flags = decor.systemUiVisibility or
//				View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//		decor.systemUiVisibility = flags

//		val deviceSerial = android.os.Build.SERIAL
////		val deviceIMEI = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
//		val androidId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
//		Timber.v("deviceSerial = %s, isEmulator = %b, androidId = %s", deviceSerial, AndroidUtils.isEmulator(), androidId)
//
//		val locations = applicationContext.resources.configuration.locales
//		Timber.v("Locations= %s", locations[0])
//		val locale = applicationContext.resources.configuration.locale.country
//		val locale2 = applicationContext.resources.configuration.locale.displayCountry
//		val language = applicationContext.resources.configuration.locale.displayLanguage
//		Timber.v("Country = %s, displayCountry = %s, language = %s", locale, locale2, language)
//		Timber.v("IP countryCode = %s, Mac = %s", NetworkUtils.getIPAddress(true), NetworkUtils.getMACAddress("wlan0"))
//
//		Timber.v("timeZone = %s ", TimeZone.getDefault())
//		Timber.v("java timeZone = %s ", java.util.TimeZone.getDefault())
//		Timber.v("Device ID = " + AndroidUtils.getDeviceIdentifier(applicationContext))
//		Timber.v("Device: " + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
//				+ " prod " + android.os.Build.PRODUCT + " board = " + android.os.Build.BOARD
//				+ " device = " + android.os.Build.DEVICE + " brand = " + android.os.Build.BRAND)

		btnStart.doOnLayout {
			btnStart.translationY = btnStart.height.toFloat() + applicationContext.resources.getDimension(com.dimowner.elections.R.dimen.spacing_huge)
			btnStart.visibility = View.VISIBLE
			AnimationUtil.verticalSpringAnimation(btnStart, 0)
		}

		adapter = OnboardingAdapter(supportFragmentManager)
		pager.adapter = adapter
		pagerPager = ViewPagerPager(pager)
		pageIndicator.setViewPager(pager)

		presenter.bindView(this)
		isAnimated = false
	}

	override fun onStart() {
		super.onStart()
		handler.postDelayed(advancePager, INITIAL_ADVANCE_DELAY)
		btnStart.setOnClickListener {
			if (AndroidUtils.isEmulator()) {
				showWarningEmulator()
			} else {
				//TODO: Add check that remote database doesn't have this device serial number in database
				if (checkLocationPermission()) {
					presenter.locate(applicationContext)
				} else {
					if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						AndroidUtils.showDialog(this,
								R.string.warning,
								R.string.location_are_needed,
								{ requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_CODE_LOCATION) },
								{ Timber.v("negative btn click") })
					}
				}
			}
		}
	}

	private fun checkLocationPermission(): Boolean {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return false
			}
		}
		return true
	}

	@SuppressLint("MissingPermission")
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == REQ_CODE_LOCATION
				&& grantResults.isNotEmpty()
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) run {
			presenter.locate(applicationContext)
		}
	}

	override fun onResume() {
		super.onResume()
		if (!isAnimated) {
			welcomeIcon.postDelayed({
				AnimationUtil.verticalSpringAnimation(welcomeIcon, tridentOffset.toInt()) {
					animation, canceled, value, velocity ->
						run {
							adapter.showText()
							pager.addOnPageChangeListener(this)
							isAnimated = true
						}
				}
			}, 200)
		}
	}

	override fun startPollActivity() {
		startActivity(PollActivity.getStartIntent(applicationContext))
		finish()
	}

	private fun showWarningEmulator() {
		AndroidUtils.showDialog(this,
				R.string.emulator,
				R.string.vote_from_emulator_not_allowed,
				{ Timber.v("Ok")
					presenter.firstRunExecuted()
					startActivity(MainActivity.getStartIntent(applicationContext))
					finish()
				}, null)
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
	}

	override fun onStop() {
		handler.removeCallbacks(advancePager)
		super.onStop()
	}

	override fun onPageScrollStateChanged(state: Int) {}
	override fun onPageSelected(position: Int) {}
	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
		if (position == 0) {
			welcomeIcon.translationY = tridentOffset - positionOffsetPixels.toFloat()
		}
	}

	override fun showProgress() {
		progress.visibility = View.VISIBLE
	}

	override fun hideProgress() {
		progress.visibility = View.GONE
	}

	override fun showError(message: String) {
//		Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
		Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
	}

	override fun showError(resId: Int) {
		Toast.makeText(applicationContext, resId, Toast.LENGTH_LONG).show()
//		Snackbar.make(container, resId, Snackbar.LENGTH_LONG).show()
	}
}

class OnboardingAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

	private val fragments =
			arrayOf(
					WelcomeFragment(),
					Welcome2Fragment()
			)

	override fun getItem(position: Int) = fragments[position]

	override fun getCount() = fragments.size

	fun showText() {
		(fragments[0] as WelcomeFragment).showText()
	}
}
