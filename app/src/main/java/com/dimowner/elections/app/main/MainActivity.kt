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

package com.dimowner.elections.app.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.dimowner.elections.R
import com.dimowner.elections.GWApplication
import com.dimowner.elections.app.candidates.CandidatesListFragment
import com.dimowner.elections.app.votes.VotesListFragment
import com.dimowner.elections.data.Prefs
import com.dimowner.elections.app.welcome.WelcomeActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

	companion object {
		fun getStartActivity(context: Context): Intent {
			return Intent(context, MainActivity::class.java)
		}
	}

//	companion object {
//		const val ITEM_SETTINGS = 0
//		const val ITEM_TODAY = 1
//		const val ITEM_TWO_WEEKS = 2
//	}

	@Inject
	lateinit var prefs: Prefs

//	private var prevMenuItem: MenuItem? = null
//
//	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//		item.isChecked = true
//		when (item.itemId) {
//			R.id.nav_settings -> {
//				pager.setCurrentItem(ITEM_SETTINGS, true)
//			}
//			R.id.nav_today -> {
//				pager.setCurrentItem(ITEM_TODAY, true)
//			}
//			R.id.nav_two_weeks -> {
//				pager.setCurrentItem(ITEM_TWO_WEEKS, true)
//			}
//		}
//		false
//	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		GWApplication.get(applicationContext).applicationComponent().inject(this)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			window.setFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
		}

		if (prefs.isFirstRun()) {
			startActivity(WelcomeActivity.getStartActivity(applicationContext))
			finish()
		} else {
			val fragments = ArrayList<Fragment>()
//			fragments.add(SettingsFragment.newInstance())
			val candidatesFragments = CandidatesListFragment.newInstance()
			val votesFragment = VotesListFragment.newInstance()
			candidatesFragments.onMoveToVotesListener = View.OnClickListener { pager.setCurrentItem(1, true) }
			votesFragment.onMoveToResultsListener = View.OnClickListener { pager.setCurrentItem(0, true) }
			fragments.add(candidatesFragments)
			fragments.add(votesFragment)
			val adapter = CustomStatePagerAdapter(supportFragmentManager, fragments)
			pager.adapter = adapter
//			pager.currentItem = 1
//			onPageSelected(1)
			pager.addOnPageChangeListener(this)

//			bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
		}
	}

	override fun onPageScrollStateChanged(state: Int) {}
	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

	override fun onPageSelected(position: Int) {
		Timber.v("onPageSelected pos = $position")
//		if (prevMenuItem != null) {
//			prevMenuItem?.isChecked = false
//		}
//		else {
//			bottomNavigation.menu.getItem(0).isChecked = false
//		}
//
//		bottomNavigation.menu.getItem(position).isChecked = true
//		prevMenuItem = bottomNavigation.menu.getItem(position)
	}
}
