/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.fail;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.tests.Util;

/**
 * Common utility class for writing reasoner tests.
 * 
 * @author htson
 */
public class AbstractTests {
	
	public static void assertPositions(String message, String expected,
			List<IPosition> positions) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IPosition position : positions) {
			if (sep)
				builder.append('\n');
			builder.append(position);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

	public static void assertSequents(String message, String expected,
			IProverSequent... sequents) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IProverSequent sequent : sequents) {
			if (sep)
				builder.append('\n');
			builder.append(sequent);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
