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
package fr.systerel.tactics.perfs.utils;

import java.util.Calendar;

/**
 * Class used to print the time between two points of measures. Should not be
 * used as follow :
 * <p>
 * Chrono c = new Chrono();<br>
 * c.startTime();<br>
 * c.getTime();<br>
 * c.getTime();<br>
 * <p>
 * But rather :
 * <p>
 * Chrono c = new Chrono();<br>
 * c.startTime();<br>
 * c.getTime();<br>
 * c.startTime();<br>
 * c.getTime();<br>
 * <p>
 * Indeed, in getTime method, a string is printed in the console, and creating
 * and printing a string can be greedy. Since that class is use to measure
 * execution time of methods or reasoner or tactic, we should avoid interfering
 * with the measurement.
 * 
 * @author Emmanuel Billaud
 */
public class Chrono {
	private long startTime;

	/**
	 * Save in a parameter the time when this method has been called.
	 */
	public void startTime() {
		startTime = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Compute the elapsed time between the call of <code>startTime()</code> and
	 * the call of this method.<br>
	 * Print in the console that duration (in milliseconds).
	 */
	public void getTime() {
		final long loopTime = Calendar.getInstance().getTimeInMillis();
		final long duration = loopTime - startTime;
		System.out.println(duration);
	}

}
