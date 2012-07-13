/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.InjectedDatatypeExtension.injectExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.internal.core.ast.extension.datatype.EnumDatatypeTranslator;

/**
 * Tests for the enumeration datatype translation.<br>
 * These tests check that the enumeration datatype contructors are translated
 * into fresh free identifiers of a new type which corresponds to the datatype
 * constructor, and that the partition predicate associated to the translation
 * is valid.
 * 
 * @author Thomas Muller
 */
public class TestEnumDatatypeTranslator extends AbstractTests {

	private static final Set<String> NO_USED_NAMES = Collections
			.<String> emptySet();

	/**
	 * Case where there is one enumerated element.
	 */
	public void testOneEnumTranslator() {
		assertValidTranslation("basicEnum ::= one",//
				NO_USED_NAMES,//
				"partition(basicEnum, {one})");//
	}

	/**
	 * Case where there is three enumerated elements.
	 */
	public void testEnumTranslator() {
		assertValidTranslation(
				"basicEnum ::= one || two || three",//
				NO_USED_NAMES,//
				"partition(basicEnum, {one}, {two}, {three})");
	}

	/**
	 * Case where there are unmatched reserved names.
	 */
	public void testEnumTranslatorNotUsedReservedNames() {
		assertValidTranslation(
				"basicEnum ::= one || two",//
				mSet("anotherEnum", "four", "five"),//
				"partition(basicEnum, {one}, {two})");
	}

	/**
	 * Case where there are reserved names match the symbols of the datatype
	 * constructors.
	 */
	public void testEnumTranslatorUsedReservedNames() {
		assertValidTranslation(
				"basicEnum ::= one || two || three",//
				mSet("basicEnum", "one", "two", "three"),//
				"partition(basicEnum0, {one0}, {two0}, {three0})");
	}

	private static Set<String> mSet(String... elements) {
		final Set<String> set = new HashSet<String>();
		for (String e : elements) {
			assertTrue(!set.contains(e));
			set.add(e);
		}
		return set;
	}

	private static void assertValidTranslation(String enumerationSpec,
			Set<String> usedNames, String expectedStr) {
		final IDatatypeExtension extension = injectExtension(enumerationSpec);
		final IDatatype datatype = mEnumDatatype(extension);
		final FormulaFactory factory = mFormulaFactory(datatype);
		final ParametricType parametricType = factory.makeParametricType(
				new Type[0], datatype.getTypeConstructor());
		final EnumDatatypeTranslator translator = getTranslator(factory,
				parametricType, usedNames);
		// check that newReservedNames correspond
		final Set<String> newReservedNames = translator.getAddedUsedNames();
		final String[] newNames = getNewNames(expectedStr);
		assertTrue(newReservedNames.size() == newNames.length);
		assertTrue(newReservedNames.containsAll(Arrays.asList(newNames)));
		// check the partition predicate associated to the translation
		final Predicate expected = ff.parsePredicate(expectedStr, LATEST, null)
				.getParsedPredicate();
		final ITypeEnvironment environment = getExpectedTypeEnv(newNames);
		expected.typeCheck(environment);
		final Predicate actual = translator.getTranslatedPredicate();
		assertPartitionSemanticallyEquals(expected, actual);
	}
	
	private static void assertPartitionSemanticallyEquals(Predicate expected,
			Predicate actual) {
		assertTrue(actual instanceof MultiplePredicate);
		assertTrue(actual.getTag() == Formula.KPARTITION);
		assertSame(expected.getChildCount(), actual.getChildCount());
		final List<Expression> actualChildren = Arrays
				.asList(((MultiplePredicate) actual).getChildren());
		final List<Expression> expectedChildren = Arrays
				.asList(((MultiplePredicate) expected).getChildren());
		assertTrue(expectedChildren.size() == actualChildren.size());
		assertTrue(actualChildren.containsAll(expectedChildren));
	}

	private static ITypeEnvironment getExpectedTypeEnv(String[] newNames) {
		final String[] typeEnvStrs = new String[newNames.length * 2];
		Arrays.fill(typeEnvStrs, newNames[0]);
		typeEnvStrs[1] = "ℙ(" + newNames[0] + ")";
		for (int i = 1; i < newNames.length; i++) {
			typeEnvStrs[i * 2] = newNames[i];
		}
		return mTypeEnvironment(typeEnvStrs);
	}

	private static String[] getNewNames(String expectedPredicate) {
		final String[] names = expectedPredicate.replaceAll(
				"partition|\\(|\\)|,|\\{|\\}", "").split("\\s+");
		return names;
	}

	public static EnumDatatypeTranslator getTranslator(FormulaFactory factory,
			ParametricType parametricType, Set<String> usedNames) {
		return new EnumDatatypeTranslator(factory, usedNames, parametricType);
	}

	private static FormulaFactory mFormulaFactory(IDatatype datatype) {
		return FormulaFactory.getInstance(datatype.getExtensions());
	}

	private static IDatatype mEnumDatatype(IDatatypeExtension extension) {
		return FormulaFactory.getDefault().makeDatatype(extension);
	}

}
