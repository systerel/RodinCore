/*******************************************************************************
 * Copyright (c) 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.AbstractTests.ff;
import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parsePredicate;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.AbstractTests.typeCheck;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.Specialization;

/**
 * Helper class for verifying the contents of a specialization instance.
 * 
 * @author Laurent Voisin
 */
public class SpecializationChecker extends AbstractSpecializationHelper {

	/**
	 * Verifies that the specialization contains exactly the expected
	 * substitutions.
	 * 
	 * @param spe
	 *            the specialization to check
	 * @param srcTypenvImage
	 *            the type environment of the source language
	 * @param specImage
	 *            the substitutions
	 * @param dstTypenvImage
	 *            the type environment of the destination language
	 */
	public static void verify(ISpecialization spe, String srcTypenvImage,
			String specImage, String dstTypenvImage) {
		final SpecializationChecker checker = new SpecializationChecker(ff,
				srcTypenvImage, ff, dstTypenvImage);
		checker.addSpecialization(specImage);
		checker.verify((Specialization) spe);
	}

	final ITypeEnvironmentBuilder dstTypenv;

	final Map<GivenType, Type> typeMap;
	final Map<FreeIdentifier, Expression> identMap;
	final Map<PredicateVariable, Predicate> predMap;

	private SpecializationChecker(FormulaFactory srcFac, String srcTypenvImage,
			FormulaFactory dstFac, String dstTypenvImage) {
		super(mTypeEnvironment(srcTypenvImage, srcFac), dstFac);
		dstTypenv = mTypeEnvironment(dstTypenvImage, dstFac);
		typeMap = new HashMap<GivenType, Type>();
		identMap = new HashMap<FreeIdentifier, Expression>();
		predMap = new HashMap<PredicateVariable, Predicate>();
	}

	@Override
	protected void addTypeSpecialization(String srcImage, String dstImage) {
		final GivenType src = srcFac.makeGivenType(srcImage);
		final Type dst = parseType(dstImage, dstFac);

		// Ensure that the right-hand side given types are compatible.
		for (final GivenType given : dst.getGivenTypes()) {
			dstTypenv.addGivenSet(given.getName());
		}
		addTypeSubstitution(src, dst);
	}

	@Override
	protected void addPredicateSpecialization(String srcImage,
			String dstImage) {
		final PredicateVariable predVar = srcFac.makePredicateVariable(srcImage,
				null);
		final Predicate pred = parsePredicate(dstImage, dstTypenv);
		predMap.put(predVar, pred);
	}

	@Override
	protected void addIdentSpecialization(String srcImage, String dstImage) {
		final FreeIdentifier src = srcFac.makeFreeIdentifier(srcImage, null);
		typeCheck(src, srcTypenv);
		final Expression dst = parseExpression(dstImage, dstTypenv);
		identMap.put(src, dst);

		// Also add the identity substitutions for the given types occurring in
		// the left-hand side.
		for (final GivenType given : src.getGivenTypes()) {
			if (typeMap.get(given) == null) {
				addTypeSubstitution(given, given.translate(dstFac));
			}
		}
	}

	private void addTypeSubstitution(GivenType src, Type dst) {
		typeMap.put(src, dst);
		identMap.put(src.toExpression(), dst.toExpression());
	}

	/**
	 * Verifies that the given specialization contains exactly what was
	 * specified when constructing this checker.
	 * 
	 * @param spe
	 *            the specialization to verify
	 */
	private void verify(Specialization spe) {
		assertSame(spe.getFactory(), dstTypenv.getFormulaFactory());

		verifySourceTypenv(spe);
		verifyDestinationTypenv(spe);
		verifyTypeSubstitutions(spe);
		verifyIdentSubstitutions(spe);
		verifyPredSubstitutions(spe);
	}

	private void verifySourceTypenv(Specialization spe) {
		final ITypeEnvironmentBuilder actual = spe.getSourceTypenv();
		if (actual == null) {
			assertTrue(srcTypenv.isEmpty());
		} else {
			assertEquals(srcTypenv.makeBuilder(), actual);
		}
	}

	private void verifyDestinationTypenv(Specialization spe) {
		final ITypeEnvironmentBuilder actual = spe.getDestinationTypenv();
			assertEquals(dstTypenv.makeBuilder(), actual);
	}

	private void verifyTypeSubstitutions(ISpecialization spe) {
		assertSet(typeMap, spe.getTypes());
		for (final Entry<GivenType, Type> entry : typeMap.entrySet()) {
			final GivenType key = entry.getKey();
			final Type expected = entry.getValue();
			assertEquals(expected, spe.get(key));
			assertTrue(spe.canPut(key, expected));
			assertEquals(expected, key.specialize(spe));
		}
	}

	private void verifyIdentSubstitutions(ISpecialization spe) {
		assertSet(identMap, spe.getFreeIdentifiers());
		for (final Entry<FreeIdentifier, Expression> entry : identMap
				.entrySet()) {
			final FreeIdentifier key = entry.getKey();
			final Expression expected = entry.getValue();
			assertEquals(expected, spe.get(key));
			assertTrue(spe.canPut(key, expected));
			assertEquals(expected, key.specialize(spe));
		}
	}

	private void verifyPredSubstitutions(ISpecialization spe) {
		assertSet(predMap, spe.getPredicateVariables());
		for (final Entry<PredicateVariable, Predicate> entry : predMap
				.entrySet()) {
			final PredicateVariable key = entry.getKey();
			final Predicate expected = entry.getValue();
			assertEquals(expected, spe.get(key));
			assertEquals(expected, key.specialize(spe));
			assertTrue(spe.put(key, expected));
		}
	}

	private <T, U> void assertSet(Map<T, U> expectedMap, T[] actuals) {
		final Set<T> actual = new HashSet<T>(Arrays.asList(actuals));
		final Set<T> expected = expectedMap.keySet();
		assertEquals(expected, actual);
	}

}
