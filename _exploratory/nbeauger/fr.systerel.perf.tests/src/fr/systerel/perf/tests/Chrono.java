/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests;

import static fr.systerel.perf.tests.PerfUtils.logger;

import java.util.Calendar;

import org.junit.rules.TestName;

public class Chrono {

	private final String name;
	private long startTime;

	public Chrono(TestName testName) {
		this(testName.getMethodName());
	}

	public Chrono(String name) {
		this.name = name;
	}

	public void startMeasure() {
		startTime = Calendar.getInstance().getTimeInMillis();
	}

	public void endMeasure() {
		final long endTime = Calendar.getInstance().getTimeInMillis();

		final long duration = endTime - startTime;
		logger.info(name + " : " + duration);
	}
}