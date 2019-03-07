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

class AppConstants {
	companion object {

		const val APPLICATION_NAME = "Elections"
		const val REQUESTS_RECEIVER = "dmitriy.ponomarenko.ua@gmail.com"
		const val LOCAL_DATABASE_NAME = "elections_db"

		const val TIME_FORMAT_24H = 11
		const val TIME_FORMAT_12H = 12

		const val SHOW_INSTRUCTIONS_DELAY_MILLS = 800
	}
}
