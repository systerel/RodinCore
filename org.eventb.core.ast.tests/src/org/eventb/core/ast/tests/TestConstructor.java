/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.DatatypeParser.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.junit.Test;

/**
 * Unit tests of interface {@link IConstructorExtension}.
 * 
 * @author Laurent Voisin
 */
public class TestConstructor extends AbstractTests {

	private static final IDatatype2 DT = parse(ff, "DT[S] ::= c1 || c2[S]");
	private static final IConstructorExtension c1 = DT.getConstructor("c1");
	private static final IConstructorExtension c2 = DT.getConstructor("c2");

	@Test
	public void getName() throws Exception {
		assertEquals("c1", c1.getName());
	}

	@Test
	public void hasArgumentsFalse() throws Exception {
		assertFalse(c1.hasArguments());
	}

	@Test
	public void hasArgumentsTrue() throws Exception {
		assertTrue(c2.hasArguments());
	}

}
