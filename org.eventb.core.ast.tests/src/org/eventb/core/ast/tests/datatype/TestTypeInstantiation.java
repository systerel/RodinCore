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
package org.eventb.core.ast.tests.datatype;

import static org.eventb.core.ast.tests.DatatypeParser.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.ast.extension.datatype2.ITypeInstantiation;
import org.eventb.core.ast.tests.AbstractTests;
import org.junit.Test;

/**
 * Unit tests of interface {@link ITypeInstantiation}.
 * 
 * @author Laurent Voisin
 */
public class TestTypeInstantiation extends AbstractTests {

	private static final IDatatype2 OTHER_DT = parse(ff, "D ::= c");
	private static final FormulaFactory OTHER_FAC = OTHER_DT.getFactory();
	private static final Type OTHER_TYPE = parseType("D", OTHER_FAC);

	// Tests about type instantiation creation

	@Test(expected = NullPointerException.class)
	public void getTypeInstantiationNull() {
		LIST_DT.getTypeInstantiation(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getTypeInstantiationNotTypeConstructor() {
		LIST_DT.getTypeInstantiation(INT_TYPE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getTypeInstantiationOtherDatatype() {
		assertTrue(OTHER_TYPE instanceof ParametricType);
		LIST_DT.getTypeInstantiation(OTHER_TYPE);
	}

	// Tests about the interface properly

	private static final ITypeInstantiation LIST_INT_INST = LIST_DT
			.getTypeInstantiation(LIST_INT_TYPE);

	@Test
	public void getOrigin() {
		assertSame(LIST_DT, LIST_INT_INST.getOrigin());
	}

	@Test
	public void getInstanceType() {
		assertSame(LIST_INT_TYPE, LIST_INT_INST.getInstanceType());
	}

	// Tests about argument type instantiation

	private static final ITypeInstantiation OTHER_INST = OTHER_DT
			.getTypeInstantiation(OTHER_TYPE);

	@Test(expected = NullPointerException.class)
	public void argumentGetTypeNull() {
		EXT_HEAD.getType(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void argumentGetTypeOtherDatatype() {
		EXT_HEAD.getType(OTHER_INST);
	}

	@Test
	public void argumentGetType() {
		assertEquals(INT_TYPE, EXT_HEAD.getType(LIST_INT_INST));
		assertEquals(LIST_INT_TYPE, EXT_TAIL.getType(LIST_INT_INST));
	}

}
