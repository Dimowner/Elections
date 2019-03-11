package com.dimowner.elections.app.poll

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimowner.elections.EApplication
import com.dimowner.elections.R
import com.dimowner.elections.app.main.MainActivity
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.util.AndroidUtils
import com.dimowner.elections.util.AnimationUtil
import kotlinx.android.synthetic.main.activity_poll.*
import javax.inject.Inject

class PollActivity: AppCompatActivity(), PollContract.View {

	companion object {
		fun getStartIntent(context: Context): Intent {
			return Intent(context, PollActivity::class.java)
		}
	}

	@Inject
	lateinit var presenter: PollContract.UserActionsListener

	private var navBarHeight: Int = 0

	val adapter: PollsAdapter by lazy { PollsAdapter() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_poll)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			window.setFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
		}

		toolbar.setPadding(0, AndroidUtils.getStatusBarHeight(applicationContext), 0, 0)
		navBarHeight = AndroidUtils.getNavigationBarHeight(applicationContext)

		recyclerView.setHasFixedSize(true)
		recyclerView.layoutManager = LinearLayoutManager(applicationContext)
		recyclerView.adapter = adapter
		adapter.setItemClickListener(object : PollsAdapter.ItemClickListener{
			override fun onItemClick(view: View, position: Int, selected: Boolean) {
				if (selected) {
					showVote()
				} else {
					hideVote()
				}
			}
		})

		btnVote.doOnLayout {
			btnVote.translationY = btnVote.height.toFloat() + applicationContext.resources.getDimension(R.dimen.spacing_double)
			btnVote.visibility = View.VISIBLE
		}

		EApplication.get(applicationContext).applicationComponent().inject(this)
		presenter.bindView(this)
		presenter.loadCandidates()
	}

	fun showVote() {
		if (navBarHeight > 0) {
			AnimationUtil.verticalSpringAnimation(btnVote, -navBarHeight)
		} else {
			AnimationUtil.verticalSpringAnimation(btnVote, 0)
		}
	}

	fun hideVote() {
		val offset = btnVote.height + applicationContext.resources.getDimension(R.dimen.spacing_double)
		AnimationUtil.verticalSpringAnimation(btnVote, offset.toInt())
	}

	override fun onStart() {
		super.onStart()
		btnVote.setOnClickListener {
			if (EApplication.isConnected()) {
				showVoteConfirmationDialog()
				btnVote.setOnClickListener(null)
			} else {
				showNoConnectionMessage()
			}
		}
	}

	private fun showVoteConfirmationDialog() {
		val item = adapter.getSelectedItem()
		if (item != null) {
			val name = item.firstName + " " + item.surName
			AndroidUtils.showDialog(this,
					getString(R.string.vote),
					getString(R.string.confirm_vote, name),
					{//Positive btn
						presenter.vote(applicationContext, item.id, name)
					},
					{//Negative btn
						btnVote.setOnClickListener {
							if (EApplication.isConnected()) {
								showVoteConfirmationDialog()
								btnVote.setOnClickListener(null)
							} else {
								showNoConnectionMessage()
							}
						}
					})
		}
	}

	override fun showNoConnectionMessage() {
		AndroidUtils.showDialog(this,
				R.string.error,
				R.string.no_connection_to_internet,
				{
					btnVote.setOnClickListener {
						showVoteConfirmationDialog()
						btnVote.setOnClickListener(null)
					}
				},//Positive btn
				null)
	}

	override fun startMainScreen() {
		startActivity(MainActivity.getStartIntent(applicationContext))
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
	}

	override fun showCandidatesList(list: List<Candidate>) {
		adapter.setData(list)
	}

	override fun showProgress() {
		loadingProgress.visibility = View.VISIBLE
	}

	override fun hideProgress() {
		loadingProgress.visibility = View.GONE
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