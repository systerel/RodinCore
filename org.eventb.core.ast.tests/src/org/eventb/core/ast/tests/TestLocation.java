/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;

/**
 * Tests about source locations stored by the parser in AST nodes. Most of the
 * tests are actually done in classes TestParser and TestUnparse. This class
 * contains only specific regression tests.
 * 
 * @author Laurent Voisin
 */
public class TestLocation extends TestCase {
	
	private static FormulaFactory ff = FormulaFactory.getDefault();

	/*
	 * First regression test for [ 1544477 ] AST source location does not take
	 * into account brackets.
	 */
	public void testPredicate1() {
		//                   01234     56789     012
		String predString = "(X=Y\u2228Z=T)\u2227U=V"; 
		IParseResult parseResult = ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		
		assertTrue("Associative Predicate", parsedPred instanceof AssociativePredicate);
		AssociativePredicate aPred1 = (AssociativePredicate) parsedPred;
		Predicate [] children1 = aPred1.getChildren();
		
		SourceLocation loc = children1[1].getSourceLocation();
		assertEquals("Source Loc Start 2 ", 10, loc.getStart());
		assertEquals("Source Loc End 2 ", 12, loc.getEnd());
		
		AssociativePredicate aPred2 = (AssociativePredicate) children1[0];
		Predicate [] children2 = aPred2.getChildren();
		
		loc = children2[0].getSourceLocation();
		assertEquals("Source Loc Start 3 ", 1, loc.getStart());
		assertEquals("Source Loc End 3 ", 3, loc.getEnd());
		
		loc = children2[1].getSourceLocation();
		assertEquals("Source Loc Start 4 ", 5, loc.getStart());
		assertEquals("Source Loc End 4 ", 7, loc.getEnd());
		
		RelationalPredicate rPred1 = (RelationalPredicate) children2[0];
		
		loc = rPred1.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 5 ", 1, loc.getStart());
		assertEquals("Source Loc End 5 ", 1, loc.getEnd());

		loc = rPred1.getRight().getSourceLocation();
		assertEquals("Source Loc Start 6 ", 3, loc.getStart());
		assertEquals("Source Loc End 6 ", 3, loc.getEnd());

		RelationalPredicate rPred2 = (RelationalPredicate) children2[1];
		
		loc = rPred2.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 7 ", 5, loc.getStart());
		assertEquals("Source Loc End 7 ", 5, loc.getEnd());

		loc = rPred2.getRight().getSourceLocation();
		assertEquals("Source Loc Start 8 ", 7, loc.getStart());
		assertEquals("Source Loc End 8 ", 7, loc.getEnd());
	}
	
	/*
	 * Second regression test for [ 1544477 ] AST source location does not take
	 * into account brackets.
	 */
	public void testPredicate2() {
		//                   01     23     4     5     6
		String predString = "X\u21a6Y\u2208\u2115\u21f8\u2115";
		IParseResult parseResult = ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;
		
		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 2, loc.getEnd());
		
		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 4, loc.getStart());
		assertEquals("Source Loc End 2 ", 6, loc.getEnd());
	}
	
	/*
	 * Third regression test for [ 1544477 ] AST source location does not take
	 * into account brackets.
	 */
	public void testPredicate3() {
		//                   0123     4
		String predString = "{1}\u2282\u2115";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;
		
		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 2, loc.getEnd());
		
		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 4, loc.getStart());
		assertEquals("Source Loc End 2 ", 4, loc.getEnd());
	}

}
