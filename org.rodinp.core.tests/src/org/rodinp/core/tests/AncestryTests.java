/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * Unit tests for ancestor related methods.
 * 
 * @author Laurent Voisin
 */
public class AncestryTests extends AbstractRodinDBTests {

	private static final IRodinDB db = getRodinDB();
	private static final IRodinProject p1 = getRodinProject("P");
	private static final IRodinProject p2 = getRodinProject("Q");
	private static final IRodinFile f1 = p1.getRodinFile("X.test");

	private static void assertIsAncestorOf(boolean expected,
			IRodinElement left, IRodinElement right) {
		assertEquals(expected, left.isAncestorOf(right));
	}

	@Test
	public void testIsAncestorOf() throws Exception {
		assertIsAncestorOf(true, db, p1);
		assertIsAncestorOf(true, db, f1);

		assertIsAncestorOf(false, p1, db);
		assertIsAncestorOf(false, p1, p2);
		assertIsAncestorOf(false, p2, p1);
	}

	@Test
	public void testgetAncestor() throws Exception {
		assertEquals(db, p1.getAncestor(IRodinDB.ELEMENT_TYPE));
		assertEquals(db, f1.getAncestor(IRodinDB.ELEMENT_TYPE));
		assertNull(db.getAncestor(IRodinProject.ELEMENT_TYPE));
	}

}
