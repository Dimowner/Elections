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

package com.dimowner.elections.app.candidates;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dimowner.elections.AppConstants;
import com.dimowner.elections.EApplication;
import com.dimowner.elections.R;
import com.dimowner.elections.app.candidates.photoview.MyPhotoAttacher;
import com.dimowner.elections.app.candidates.photoview.ThresholdListener;
import com.dimowner.elections.data.Prefs;
import com.dimowner.elections.util.AndroidUtils;
import com.dimowner.elections.util.AnimationUtil;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ImagePreviewActivity extends AppCompatActivity {

	public static final String EXTRAS_KEY_IMAGE_PATH = "image_path";

	private FrameLayout container;
	private TextView txtInstructions;
	private int navigationBarHeight = 0;

	@Inject
	Prefs prefs;

	private Disposable disposable = null;

	public static Intent getStartIntent(Context context, int resId) {
		Intent intent = new Intent(context, ImagePreviewActivity.class);
		intent.putExtra(ImagePreviewActivity.EXTRAS_KEY_IMAGE_PATH, resId);
		return intent;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_preview);

		EApplication.Companion.get(getApplicationContext()).applicationComponent().inject(this);

		container = findViewById(R.id.container);
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow);
//		ImageButton btnBack = findViewById(R.id.btnNavUp);
//		btnBack.setOnClickListener(v -> finish());

		PhotoView photoView = findViewById(R.id.photo_view);
		photoView.setImageResource(R.drawable.trident_new2);

		MyPhotoAttacher attacher = new MyPhotoAttacher(photoView);
		attacher.setOnThresholdListener(new ThresholdListener() {
			@Override
			public void onTopThreshold() {
				finishActivity();
				prefs.setShowImagePreviewInstructions(false);
			}
			@Override
			public void onBottomThreshold() {
				finishActivity();
				prefs.setShowImagePreviewInstructions(false);
			}
			@Override public void onTouchDown() { }
			@Override public void onTouchUp() { }
		});

		photoView.setImageResource(getIntent().getIntExtra(EXTRAS_KEY_IMAGE_PATH, 0));

		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("");
		}

		if (prefs.isShowImagePreviewInstructions()) {
			txtInstructions = findViewById(R.id.txtInstructions);
			disposable = Completable.complete().delay(AppConstants.SHOW_INSTRUCTIONS_DELAY_MILLS, TimeUnit.MILLISECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(
							() -> {
								txtInstructions.setVisibility(View.VISIBLE);
								txtInstructions.setTranslationY(500);// Here should be instructions panel height.
								AnimationUtil.verticalSpringAnimation(txtInstructions, -navigationBarHeight);
								txtInstructions.setOnClickListener(v ->
										AnimationUtil.verticalSpringAnimation(
												txtInstructions,
												txtInstructions.getHeight(),
												(animation, canceled, value, velocity) -> {
													txtInstructions.setVisibility(View.GONE);
													prefs.setShowImagePreviewInstructions(false);
												}));
							}
					);
		}

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Set the padding to match the Status Bar height
//			btnBack.setPadding(0, AndroidUtils.getStatusBarHeight(getApplicationContext()), 0, 0);
			toolbar.setPadding(0, AndroidUtils.getStatusBarHeight(getApplicationContext()), 0, 0);
			if (hasNavBar()) {
				int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
				if (resourceId > 0) {
					navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
				}
			}
		}

//		AnimationUtil.physBasedRevealAnimation(toolbar.getChildAt(0));
		AndroidUtils.transparentNavigationBar(this);
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setupWindowAnimations();
		}
	}

	@TargetApi(21)
	private void setupWindowAnimations() {
		Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide_from_bottom);
		getWindow().setEnterTransition(slide);

	}

	private void finishActivity() {
		container.setBackgroundResource(android.R.color.transparent);
		if (AndroidUtils.isAndroid5()) {
			finishAfterTransition();
		} else {
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finishActivity();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (disposable != null) {
			disposable.dispose();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		container.setBackgroundResource(android.R.color.transparent);
	}

	public boolean hasNavBar () {
		int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		return id > 0 && getResources().getBoolean(id);
	}
}
