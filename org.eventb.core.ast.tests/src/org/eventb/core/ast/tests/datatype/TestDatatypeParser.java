/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.datatype;

import static org.eventb.core.ast.ProblemKind.DatatypeParsingError;
import static org.eventb.core.ast.ProblemKind.InvalidTypeExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.tests.AbstractTests;
import org.junit.Test;

/**
 * Acceptance tests for the specific type parser used when building datatype.
 * 
 * @author Vincent Monfort
 * @author Laurent Voisin
 */
public class TestDatatypeParser extends AbstractTests {

	private static final IDatatypeBuilder basic = makeDatatypeBuilder(ff,
			"Basic");
	private static final IDatatypeBuilder list3 = makeDatatypeBuilder(ff,
			"List3", "S", "T", "U");
	private static final IDatatypeBuilder foo = makeDatatypeBuilder(LIST_FAC,
			"Foo", "T");

	
	/**
	 * Nominal cases where the parser succeeds.
	 */
	@Test
	public void testParser() {
		// No parameter
		assertParsed(basic, "Basic");
		
		// Any parameter
		assertParsed(list3, "S");
		assertParsed(list3, "T");
		assertParsed(list3, "U");

		// Recursive call
		assertParsed(list3, "List3(S, T, U)", "List3");

		// Recursive call in Cartesian product
		assertParsed(list3, "ℤ × List3(S, T, U)", "ℤ × List3");
		assertParsed(list3, "List3(S, T, U) × ℤ", "List3 × ℤ");

		// Recursive call in other datatype
		assertParsed(foo, "List(Foo(T))", "List(Foo)");

		// Plenty of spaces
		assertParsed(foo, "Foo\n(\tT \f)", "Foo");
	}

	/**
	 * Error cases where the datatype is not exactly repeated.
	 */
	@Test
	public void testParserWrongType() {
		// Missing parameter(s)
		assertParserError(foo, "Foo");
		assertParserError(list3, "List3");
		assertParserError(list3, "List3(S, T)");
		assertParserError(list3, "List3(T, U)");
		assertParserError(list3, "List3(S, U)");

		// Too many parameters
		assertParserError(basic, "Basic(T)", InvalidTypeExpression);
		assertParserError(foo, "Foo(T, S)");
		assertParserError(list3, "List3(S, T, U, V)");

		// Wrong parameter name
		assertParserError(list3, "List3(X, T, U)");
		assertParserError(list3, "List3(S, X, U)");
		assertParserError(list3, "List3(S, T, X)");

		// Broken string
		assertParserError(list3, "List3(S");
	}

	@Test
	public void testNoParamProblemSourceLoc() throws Exception {
		final IParseResult result = foo.parseType("Foo");
		final ASTProblem problem = extractDatatypeParsingError(result);
		// the source location is 3:2 when the bug is present
		assertEquals(new SourceLocation(3, 3), problem.getSourceLocation());
	}

	/*
	 * Returns the first problem of kind DatatypeParsingError in the given parse
	 * result.
	 */
	private ASTProblem extractDatatypeParsingError(IParseResult result) {
		final List<ASTProblem> problems = result.getProblems();
		for (final ASTProblem pb : problems) {
			if (pb.getMessage() == DatatypeParsingError) {
				return pb;
			}
		}
		return null;
	}
	
	/**
	 * Checks that the given input is parsed without change, when using the
	 * parser of the given builder.
	 */
	private void assertParsed(IDatatypeBuilder builder, String input) {
		assertParsed(builder, input, input);
	}

	/**
	 * Checks that the given input is parsed the same as the reference string,
	 * when using the parser of the given builder.
	 */
	private void assertParsed(IDatatypeBuilder builder, String input,
			String reference) {
		final FormulaFactory factory = builder.getFactory();
		final Type expected = parseType(reference, factory);
		final IParseResult result = builder.parseType(input);
		assertFalse(result.hasProblem());
		final Type actual = result.getParsedType();
		assertEquals(expected, actual);
	}

	/**
	 * Checks that the type parser for the given datatype builder produces an
	 * error on the given input.
	 */
	private void assertParserError(IDatatypeBuilder builder, String input) {
		assertParserError(builder, input, DatatypeParsingError);
	}
	
	private void assertParserError(IDatatypeBuilder builder, String input,
			ProblemKind expectedProblem) {
		final IParseResult result = builder.parseType(input);
		assertTrue(result.hasProblem());
		assertNull(result.getParsedType());
		final List<ASTProblem> problems = result.getProblems();
		assertTrue(1 <= problems.size());
		final ASTProblem first = problems.get(0);
		assertTrue(first.isError());
		assertEquals(expectedProblem, first.getMessage());
	}

	private static IDatatypeBuilder makeDatatypeBuilder(FormulaFactory factory,
			String name, String... paramNames) {
		final GivenType[] typeParams = makeGivenTypes(factory, paramNames);
		return factory.makeDatatypeBuilder(name, typeParams);
	}

	private static GivenType[] makeGivenTypes(FormulaFactory factory,
			String[] names) {
		final int length = names.length;
		final GivenType[] result = new GivenType[length];
		for (int i = 0; i < length; i++) {
			result[i] = factory.makeGivenType(names[i]);
		}
		return result;
	}

}
