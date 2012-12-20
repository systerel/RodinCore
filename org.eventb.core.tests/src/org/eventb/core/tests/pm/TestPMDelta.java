/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;

public abstract class TestPMDelta extends TestPM implements
		IUserSupportManagerChangedListener {

	List<IUserSupportManagerDelta> deltas;

	/**
	 * Starts recording delta from the user support manager.
	 */
	void startDeltas() {
		deltas = new ArrayList<IUserSupportManagerDelta>();
		manager.addChangeListener(this);
	}

	/**
	 * Stops recording delta from the user support manager.
	 */
	void stopDeltas() {
		manager.removeChangeListener(this);
		deltas = new ArrayList<IUserSupportManagerDelta>();
	}

	void clearDeltas() {
		deltas = new ArrayList<IUserSupportManagerDelta>();
	}

	void assertDeltas(String message, String expected) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IUserSupportManagerDelta delta : deltas) {
			if (sep)
				builder.append('\n');
			builder.append(delta);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

	@Override
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		assertTrue(deltas != null);
		deltas.add(delta);
	}

}
