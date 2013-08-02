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
import static org.junit.Assert.*;

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
	public void getName() {
		assertEquals("c1", c1.getName());
	}

	@Test
	public void getOrigin() {
		assertEquals(DT, c1.getOrigin());
	}

	@Test
	public void hasArgumentsFalse() {
		assertFalse(c1.hasArguments());
	}

	@Test
	public void hasArgumentsTrue() {
		assertTrue(c2.hasArguments());
	}

	@Test
	public void getArgumentsNone() {
		assertEquals(0, c1.getArguments().length);
	}

	@Test
	public void getArgumentsOne() {
		assertEquals(1, c2.getArguments().length);
	}

	@Test
	public void getArgumentsTwo() {
		assertEquals(2, c3.getArguments().length);
	}

	@Test
	public void getDestructorNone() {
		assertNull(c2.getDestructor("unknown"));
	}

	@Test
	public void getDestructorSuccess() {
		assertEquals(c3.getArguments()[1], c3.getDestructor("d3"));
	}

	@Test
	public void isDestructorFalse() {
		final IConstructorArgument arg = c3.getArguments()[0];
		assertFalse(arg.isDestructor());
		assertNull(arg.asDestructor());
	}

	@Test
	public void isDestructorTrue() {
		final IConstructorArgument d3 = c3.getArguments()[1];
		assertTrue(d3.isDestructor());
		assertSame(d3, d3.asDestructor());
	}

	@Test
	public void getArgumentConstructor() {
		for (IConstructorArgument arg : c3.getArguments()) {
			assertSame(c3, arg.getConstructor());
		}
	}

	@Test
	public void getArgumentOrigin() {
		for (IConstructorArgument arg : c3.getArguments()) {
			assertSame(DT, arg.getOrigin());
		}
	}

	@Test
	public void getDestructorName() {
		final IConstructorArgument d3 = c3.getArguments()[1];
		assertEquals("d3", d3.asDestructor().getName());
	}

	@Test
	public void getArgumentIndexUnknown() {
		assertEquals(-1, c2.getArgumentIndex(null));
		assertEquals(-1, c2.getArgumentIndex(EXT_HEAD));
		assertEquals(-1, c2.getArgumentIndex(c3.getArguments()[0]));
	}

	@Test
	public void getArgumentIndexKnown() {
		final IConstructorArgument[] args = c3.getArguments();
		for (int i = 0; i < args.length; i++) {
			assertEquals(i, c3.getArgumentIndex(args[i]));
		}
	}

}
