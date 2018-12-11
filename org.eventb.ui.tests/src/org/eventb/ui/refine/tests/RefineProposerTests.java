/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.refine.tests;

import static java.util.Arrays.asList;
import static org.eventb.internal.ui.refine.RefineProposer.getTentativeName;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.internal.ui.refine.RefineProposer;
import org.junit.Test;

/**
 * Unit tests for the {@link RefineProposer} class.
 * 
 * @author Laurent Voisin
 */
public class RefineProposerTests {

	@Test
	public void testNoNumber() {
		assertProposition("m1", "m");
		
		// First proposition already used
		assertProposition("m2", "m", "m1");

		// First and second propositions already used
		assertProposition("m3", "m", "m1", "m2");
	}

	@Test
	public void testSuffix() {
		assertProposition("m2", "m1");
		assertProposition("m3", "m1", "m2");
	}

	@Test
	public void testPaddedSuffix() {
		assertProposition("m02", "m01");
		
		// Proposition exists but with different padding
		assertProposition("m02", "m01", "m2");

		assertProposition("m003", "m001", "m002");		
	}

	@Test
	public void testPrefix() {
		assertProposition("2m", "1m");
		
		// Proposition exists but as suffix
		assertProposition("2m", "1m", "m2");

		assertProposition("3m", "1m", "2m");
	}

	@Test
	public void testPaddedPrefix() {
		assertProposition("02m", "01m");
		
		// Proposition exists but with different padding
		assertProposition("02m", "01m", "2m");

		assertProposition("003m", "001m", "002m");		
	}

	@Test
	public void testPrefixTakesPrecedence() {
		assertProposition("2m1", "1m1");
		assertProposition("2m1", "1m1", "1m2");
		assertProposition("3m1", "1m1", "2m1");
		assertProposition("003m1", "001m1", "002m1");
	}

	private static void assertProposition(String expected, String oldName, String... usedNames) {
		final Set<String> usedSet = new HashSet<>(asList(usedNames));
		final IInputValidator validator = new IInputValidator() {
			@Override
			public String isValid(String name) {
				return usedSet.contains(name) ? "used" : null;
			}
		};
		final String actual = getTentativeName(oldName, validator);
		assertEquals(expected, actual);
	}

}
