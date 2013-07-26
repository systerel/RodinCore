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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.extension.datatype2.IConstructorArgument;
import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.junit.Test;

/**
 * Unit tests of interfaces {@link IConstructorExtension} and
 * {@link IConstructorArgument}.
 * 
 * @author Laurent Voisin
 */
public class TestConstructor extends AbstractTests {

	private static final IDatatype2 DT = parse(ff,
			"DT[S] ::= c1 || c2[S] || c3[S; d3: S]");
	private static final IConstructorExtension c1 = DT.getConstructor("c1");
	private static final IConstructorExtension c2 = DT.getConstructor("c2");
	private static final IConstructorExtension c3 = DT.getConstructor("c3");

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

	@Test
	public void getArgumentsNone() throws Exception {
		assertEquals(0, c1.getArguments2().length);
	}

	@Test
	public void getArgumentsOne() throws Exception {
		assertEquals(1, c2.getArguments2().length);
	}

	@Test
	public void getArgumentsTwo() throws Exception {
		assertEquals(2, c3.getArguments2().length);
	}

	@Test
	public void isDestructorFalse() throws Exception {
		final IConstructorArgument arg = c3.getArguments2()[0];
		assertFalse(arg.isDestructor());
		assertNull(arg.asDestructor());
	}

	@Test
	public void isDestructorTrue() throws Exception {
		final IConstructorArgument d3 = c3.getArguments2()[1];
		assertTrue(d3.isDestructor());
		assertEquals("d3", d3.asDestructor().getName());
	}

}
