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

package com.dimowner.elections.app.votes

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dimowner.elections.R
import com.dimowner.elections.EApplication
import com.dimowner.elections.util.AndroidUtils
import com.dimowner.elections.util.AnimationUtil
import kotlinx.android.synthetic.main.fragment_votes_list.*
import javax.inject.Inject

class VotesListFragment: Fragment(), VotesListContract.View {

	companion object {
		fun newInstance(): VotesListFragment {
			return VotesListFragment()
		}
	}

	@Inject
	lateinit var presenter: VotesListContract.UserActionsListener

	lateinit var layoutManager: LinearLayoutManager

	val adapter: VotesListAdapter by lazy { VotesListAdapter() }

	var onMoveToResultsListener: View.OnClickListener? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_votes_list, container, false)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			view.findViewById<FrameLayout>(R.id.pnlToolbar).setPadding(0, AndroidUtils.getStatusBarHeight(context), 0, 0)
			val navBarHeight = AndroidUtils.getNavigationBarHeight(context).toFloat()
			if (navBarHeight > 0) {
				view.findViewById<TextView>(R.id.btnVotes).translationY = -navBarHeight
			}
		}
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		recyclerView.setHasFixedSize(true)
		layoutManager = LinearLayoutManager(activity?.applicationContext)
		recyclerView.layoutManager = layoutManager
		recyclerView.adapter = adapter
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(rv, dx, dy)
				handleToolbarScroll(dy)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					if (isListOnTop()) {
						AnimationUtil.viewElevationAnimation(pnlToolbar, 0f, object : Animator.AnimatorListener {
							override fun onAnimationStart(animation: Animator) {}
							override fun onAnimationEnd(animation: Animator) {
								pnlToolbar.setBackgroundResource(android.R.color.transparent)
							}

							override fun onAnimationCancel(animation: Animator) {}
							override fun onAnimationRepeat(animation: Animator) {}
						})
					}
				}
			}
		})

		btnVotes.setOnClickListener { onMoveToResultsListener?.onClick(btnVotes) }

		EApplication.get(view.context).applicationComponent().inject(this)
		presenter.bindView(this)
		presenter.loadVotes()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		presenter.unbindView()
	}

	override fun showCandidatesList(list: List<VoteListItem>) {
		adapter.setData(list)
	}

	fun isListOnTop(): Boolean {
		return layoutManager.findFirstCompletelyVisibleItemPosition() == 0
	}

	private fun handleToolbarScroll(dy: Int) {
		var inset = pnlToolbar.translationY - dy
		val height: Int
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			height = pnlToolbar.height + AndroidUtils.getStatusBarHeight(context)
		} else {
			height = pnlToolbar.height
		}

		if (inset < -height) {
			inset = (-height).toFloat()
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				pnlToolbar.translationZ = resources.getDimension(R.dimen.toolbar_elevation)
				pnlToolbar.setBackgroundResource(R.color.main_yellow_dark)
			}
		}

		if (pnlToolbar.translationY <= 0 && inset > 0) {
			pnlToolbar.translationY = 0f
		} else {
			pnlToolbar.translationY = inset
		}
	}

	override fun showProgress() {
		loadingProgress.visibility = View.VISIBLE
	}

	override fun hideProgress() {
		loadingProgress.visibility = View.GONE
	}

	override fun hideSmallProgress() {
		loadingProgressSmall.visibility = View.GONE
	}

	override fun showSmallProgress() {
		loadingProgressSmall.visibility = View.VISIBLE
	}

	override fun showError(message: String) {
//		Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
		Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
	}

	override fun showError(resId: Int) {
		Toast.makeText(activity?.applicationContext, resId, Toast.LENGTH_LONG).show()
//		Snackbar.make(container, resId, Snackbar.LENGTH_LONG).show()
	}
}