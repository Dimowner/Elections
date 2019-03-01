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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dimowner.elections.GWApplication
import com.dimowner.elections.R
import com.dimowner.elections.app.poll.PollActivity
import com.dimowner.elections.util.AndroidUtils
import com.dimowner.elections.util.AnimationUtil
import kotlinx.android.synthetic.main.activity_welcome.*
import javax.inject.Inject

private const val AUTO_ADVANCE_DELAY = 6_000L
private const val INITIAL_ADVANCE_DELAY = 3_000L

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
		GWApplication.get(applicationContext).applicationComponent().inject(this)

		// immersive mode so images can draw behind the status bar
//		val decor = window.decorView
//		val flags = decor.systemUiVisibility or
//				View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//		decor.systemUiVisibility = flags

		btnStart.setOnClickListener {
			presenter.firstRunExecuted()
//			startActivity(Intent(applicationContext, MainActivity::class.java))
//			finish()
			startActivity(PollActivity.getStartActivity(applicationContext))
		}

		btnStart.doOnLayout {
			btnStart.translationY = btnStart.height.toFloat() + applicationContext.resources.getDimension(R.dimen.spacing_huge)
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

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
	}

	override fun onStart() {
		super.onStart()
		handler.postDelayed(advancePager, INITIAL_ADVANCE_DELAY)
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

	override fun showProgress() {}
	override fun hideProgress() {}
	override fun showError(message: String) {}
	override fun showError(resId: Int) {}
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
