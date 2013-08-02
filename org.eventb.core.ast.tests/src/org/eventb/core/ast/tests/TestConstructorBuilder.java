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

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;
import org.junit.Test;

/**
 * Unit tests of interface {@link IConstructorBuilder}.
 * 
 * @author Laurent Voisin
 */
public class TestConstructorBuilder extends AbstractTests {

	private static final GivenType tyS = LIST_FAC.makeGivenType("S");
	private static final GivenType tyDT = LIST_FAC.makeGivenType("DT");

	private static final Type LIST(Type base) {
		return LIST_FAC.makeParametricType(singletonList(base), EXT_LIST);
	}

	private final IDatatypeBuilder builder = LIST_FAC.makeDatatypeBuilder("DT",
			tyS);
	private final IConstructorBuilder cons = builder.addConstructor("cns");

	@Test(expected = NullPointerException.class)
	public void nullUnnamedArgument() {
		cons.addArgument(null);
	}

	@Test(expected = NullPointerException.class)
	public void nullNamedArgument() {
		cons.addArgument("foo", null);
	}

	public void nullNamedArgumentName() {
		cons.addArgument(null, tyS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidNamedArgumentName() {
		cons.addArgument("123", tyS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void conflictingNamedArgumentWithDatatype() {
		cons.addArgument("DT", tyS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void conflictingNamedArgumentWithConstructor() {
		cons.addArgument("cons", tyS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void conflictingNamedArgumentWithDestructor() {
		cons.addArgument("foo", tyS);
		cons.addArgument("foo", tyS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void conflictingNamedArgumentWithOtherDestructor() {
		final IConstructorBuilder other = builder.addConstructor("other");
		other.addArgument("foo", tyS);
		cons.addArgument("foo", tyS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrongFactory() {
		cons.addArgument(ff.makeBooleanType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void recursiveCallInTopLevelPowerSet() {
		cons.addArgument(POW(tyDT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void recursiveCallInDoublePowerSet() {
		cons.addArgument(POW(POW(tyDT)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void recursiveCallInSecondLevelPowerSet() {
		cons.addArgument(LIST(POW(tyDT)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void recursiveCallInIndirectPowerSet() {
		cons.addArgument(POW(LIST(tyDT)));
	}

	public void validArgumentTypes() {
		cons.addArgument(tyS);
		cons.addArgument(tyDT);

		cons.addArgument(CPROD(tyS, tyDT));
		cons.addArgument(CPROD(POW(tyS), tyDT));

		cons.addArgument(POW(tyS));
		cons.addArgument(POW(LIST(tyS)));

		cons.addArgument(LIST(tyS));
		cons.addArgument(LIST(tyDT));
	}

	@Test(expected = IllegalStateException.class)
	public void finalizedUnnamedArgument() {
		builder.finalizeDatatype();
		cons.addArgument(tyS);
	}

	@Test(expected = IllegalStateException.class)
	public void finalizedNamedArgument() {
		builder.finalizeDatatype();
		cons.addArgument("foo", tyS);
	}

	@Test
	public void nullaryIsBasic() {
		assertTrue(cons.isBasic());
	}

	@Test
	public void paramIsBasic() {
		cons.addArgument(tyS);
		cons.addArgument(POW(tyS));
		cons.addArgument(LIST(tyS));
		cons.addArgument(CPROD(tyS, tyS));
		assertTrue(cons.isBasic());
	}

	@Test
	public void directRecursiveIsNotBasic() {
		cons.addArgument(tyDT);
		assertFalse(cons.isBasic());
	}

	@Test
	public void cprodRecursiveIsNotBasic() {
		cons.addArgument(CPROD(tyS, tyDT));
		assertFalse(cons.isBasic());
	}

	@Test
	public void datatypeRecursiveIsNotBasic() {
		cons.addArgument(LIST(tyDT));
		assertFalse(cons.isBasic());
	}

	@Test
	public void deepRecursiveIsNotBasic() {
		cons.addArgument(LIST(CPROD(POW(tyS), tyDT)));
		assertFalse(cons.isBasic());
	}

}
