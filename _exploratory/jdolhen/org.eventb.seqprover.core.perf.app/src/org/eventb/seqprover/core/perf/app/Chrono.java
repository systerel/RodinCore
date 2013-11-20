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
package org.eventb.seqprover.core.perf.app;

import java.util.Calendar;

public class Chrono {

	private long startTime;

	public void startMeasure() {
		startTime = Calendar.getInstance().getTimeInMillis();
	}

	public long endMeasure() {
		final long endTime = Calendar.getInstance().getTimeInMillis();

		final long duration = endTime - startTime;
		return duration;
	}
}