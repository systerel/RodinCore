/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adaptation to the Rodin database 
 *     Systerel - adaptation for the event-B core
 *******************************************************************************/
package org.eventb.core.tests;


import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IResourceDelta;
import org.eventb.core.tests.pm.Util;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;

/**
 * Delta listener for tests that want to inspect the changes made to the Rodin
 * Database.
 * <p>
 * Usage:
 * 
 * <pre>
 * final DeltaListener dl = new DeltaListener();
 * try {
 *     dl.start();
 *     // test code here
 *     dl.assertDeltas("...");
 * } finally {
 *     dl.stop();
 * }
 * </pre>
 * 
 * </p>
 */
public class DeltaListener implements IElementChangedListener {

	/**
	 * Deltas received from the Rodin database.
	 */
	public IRodinElementDelta[] deltas;

	public ByteArrayOutputStream stackTraces;

	public void assertDeltas(String message, String expected) {
		String actual = this.toString();
		if (!expected.equals(actual)) {
			System.out.println("Expected:\n" + expected);
			System.out.println("Got:\n" + Util.displayString(actual, 2));
			System.out.println(stackTraces.toString());
		}
		assertEquals(
				message,
				expected,
				actual);
	}

	/**
	 * Empties the current deltas.
	 */
	public void clear() {
		deltas = new IRodinElementDelta[0];
		stackTraces = new ByteArrayOutputStream();
	}

	public void elementChanged(ElementChangedEvent ev) {
		IRodinElementDelta[] copy = new IRodinElementDelta[deltas.length + 1];
		System.arraycopy(deltas, 0, copy, 0, deltas.length);
		copy[deltas.length] = ev.getDelta();
		deltas = copy;

		new Throwable("Caller of IElementChangedListener#elementChanged")
				.printStackTrace(new PrintStream(this.stackTraces));
	}

	private void sortDeltas(IRodinElementDelta[] elementDeltas) {
		Comparator<IRodinElementDelta> comparator = new Comparator<IRodinElementDelta>() {
			public int compare(IRodinElementDelta deltaA,
					IRodinElementDelta deltaB) {
				return deltaA.getElement().getElementName().compareTo(
						deltaB.getElement().getElementName());
			}
		};
		Arrays.sort(elementDeltas, comparator);
	}

	/**
	 * Starts listening to element deltas, and queues them in fgDeltas.
	 */
	public void start() {
		clear();
		RodinCore.addElementChangedListener(this);
	}

	/**
	 * Stops listening to element deltas, and clears the current deltas.
	 */
	public void stop() {
		RodinCore.removeElementChangedListener(this);
		clear();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = this.deltas.length; i < length; i++) {
			IRodinElementDelta[] projects = this.deltas[i]
					.getAffectedChildren();
			sortDeltas(projects);
			for (int j = 0, projectsLength = projects.length; j < projectsLength; j++) {
				buffer.append(projects[j]);
				if (j != projectsLength - 1) {
					buffer.append("\n");
				}
			}
			IResourceDelta[] nonRodinProjects = this.deltas[i]
					.getResourceDeltas();
			if (nonRodinProjects != null) {
				for (int j = 0, nonRodinProjectsLength = nonRodinProjects.length; j < nonRodinProjectsLength; j++) {
					if (j == 0 && buffer.length() != 0) {
						buffer.append("\n");
					}
					buffer.append(nonRodinProjects[j]);
					if (j != nonRodinProjectsLength - 1) {
						buffer.append("\n");
					}
				}
			}
			if (i != length - 1) {
				buffer.append("\n\n");
			}
		}
		return buffer.toString();
	}
}