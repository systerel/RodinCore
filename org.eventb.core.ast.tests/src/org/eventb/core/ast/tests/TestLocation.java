/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.junit.Test;

/**
 * Tests about source locations stored by the parser in AST nodes. Most of the
 * tests are actually done in classes TestParser and TestUnparse. This class
 * contains only specific regression tests.
 * 
 * @author Laurent Voisin
 */
public class TestLocation extends AbstractTests {

	/*
	 * First regression test for [ 1544477 ] AST source location does not take
	 * into account brackets.
	 */
	@Test 
	public void testPredicate1() {
		//                   01234     56789     012
		String predString = "(X=Y\u2228Z=T)\u2227U=V"; 
		Predicate parsedPred = parsePredicate(predString);
		
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
	@Test 
	public void testPredicate2() {
		//                   01     23     4     5     6
		String predString = "X\u21a6Y\u2208\u2115\u21f8\u2115";
		Predicate parsedPred = parsePredicate(predString);
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
	@Test 
	public void testPredicate3() {
		//                   0123     4
		String predString = "{1}\u2282\u2115";
		Predicate parsedPred = parsePredicate(predString);
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;
		
		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 2, loc.getEnd());
		
		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 4, loc.getStart());
		assertEquals("Source Loc End 2 ", 4, loc.getEnd());
	}

	@Test 
	public void testPredicate4() {
		//                   0123 4
		String predString = "∅∈∅↔S";
		Predicate parsedPred = parsePredicate(predString);
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;
		
		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 0, loc.getEnd());
		
		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 2, loc.getStart());
		assertEquals("Source Loc End 2 ", 4, loc.getEnd());
	}
	
	@Test 
	public void testPredicate5() {
		//                   0123456789012345678901
		String predString = "a∈dom(f)∧f∼;({a}◁f)⊆id";
		Predicate parsedPred = parsePredicate(predString);
		AssociativePredicate aPred = (AssociativePredicate) parsedPred;
		
		Predicate [] children = aPred.getChildren();
		
		SourceLocation loc = children[0].getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 7, loc.getEnd());
		
		loc = children[1].getSourceLocation();
		assertEquals("Source Loc Start 2 ", 9, loc.getStart());
		assertEquals("Source Loc End 2 ", 21, loc.getEnd());
	}
	
	@Test 
	public void testPredicate6() {
		//                   0123456789012
		String predString = "f∼;({a}◁f)⊆id";
		Predicate parsedPred = parsePredicate(predString);
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;
		
		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 9, loc.getEnd());
		
		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 11, loc.getStart());
		assertEquals("Source Loc End 2 ", 12, loc.getEnd());
	}
	
	@Test 
	public void testPredicate7() {
		String predString = "f(a)⊆S";
		Predicate parsedPred = parsePredicate(predString);
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;
		
		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 3, loc.getEnd());
		
		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 5, loc.getStart());
		assertEquals("Source Loc End 2 ", 5, loc.getEnd());
	}

}
