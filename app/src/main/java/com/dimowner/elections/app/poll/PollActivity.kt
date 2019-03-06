package com.dimowner.elections.app.poll

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimowner.elections.GWApplication
import com.dimowner.elections.R
import com.dimowner.elections.app.main.MainActivity
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.util.AnimationUtil
import kotlinx.android.synthetic.main.activity_poll.*
import javax.inject.Inject

class PollActivity: AppCompatActivity(), PollContract.View{

	companion object {
		fun getStartIntent(context: Context): Intent {
			return Intent(context, PollActivity::class.java)
		}
	}

	@Inject
	lateinit var presenter: PollContract.UserActionsListener

	val adapter: PollsAdapter by lazy { PollsAdapter() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_poll)

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
			btnVote.translationY = btnVote.height.toFloat() + applicationContext.resources.getDimension(R.dimen.spacing_normal)
			btnVote.visibility = View.VISIBLE
		}

		GWApplication.get(applicationContext).applicationComponent().inject(this)
		presenter.bindView(this)
		presenter.loadCandidates()
	}

	fun showVote() {
		AnimationUtil.verticalSpringAnimation(btnVote, 0)
	}

	fun hideVote() {
		val offset = btnVote.height + applicationContext.resources.getDimension(R.dimen.spacing_normal)
		AnimationUtil.verticalSpringAnimation(btnVote, offset.toInt())
	}

	override fun onStart() {
		super.onStart()
		btnVote.setOnClickListener {
			startActivity(MainActivity.getStartIntent(applicationContext))
			btnVote.setOnClickListener(null)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
	}

	override fun vote(id: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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