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

package com.dimowner.elections.app.candidates

import android.animation.Animator
import android.app.ActivityOptions
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
import com.dimowner.elections.app.settings.SettingsActivity
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.util.AndroidUtils
import com.dimowner.elections.util.AnimationUtil
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class CandidatesListFragment : Fragment(), CandidatesListContract.View {

	companion object {
		fun newInstance(): CandidatesListFragment {
			return CandidatesListFragment()
		}
	}

	@Inject
	lateinit var presenter: CandidatesListContract.UserActionsListener

	lateinit var layoutManager: LinearLayoutManager

	var onMoveToVotesListener: View.OnClickListener? = null

	val adapter: CandidatesListAdapter by lazy { CandidatesListAdapter() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_list, container, false)
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
		adapter.setItemClickListener(object : CandidatesListAdapter.ItemClickListener{
			override fun onItemClick(view: View, position: Int) {
				val code = adapter.getIconCodeForPosition(position)
				if (code.isNotBlank()) {
					startImagePreviewActivity(AndroidUtils.candidateCodeToResourceBig(code))
				}
			}
		})

		btnSettings.setOnClickListener {
			if (activity != null) startActivity(SettingsActivity.getStartActivity(activity!!))
		}
		btnVotes.setOnClickListener { onMoveToVotesListener?.onClick(btnVotes) }

		EApplication.get(view.context).applicationComponent().inject(this)
		presenter.bindView(this)
		presenter.loadCandidates()
	}

	private fun startImagePreviewActivity(resId: Int) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			startActivity(ImagePreviewActivity.getStartIntent(context, resId),
					ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
		} else {
			startActivity(ImagePreviewActivity.getStartIntent(context, resId))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		presenter.unbindView()
	}

	override fun showCandidatesList(list: List<Candidate>) {
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
				pnlToolbar.setBackgroundResource(R.color.main_blue_dark)
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

	override fun showError(message: String) {
//		Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
		Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
	}

	override fun showError(resId: Int) {
		Toast.makeText(activity?.applicationContext, resId, Toast.LENGTH_LONG).show()
//		Snackbar.make(container, resId, Snackbar.LENGTH_LONG).show()
	}
}