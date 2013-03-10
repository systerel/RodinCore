/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors,//
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.rubin.problems;

import static org.eventb.rubin.tests.Problem.mProblem;
import static org.eventb.rubin.tests.ProblemStatus.INVALID;
import static org.eventb.rubin.tests.ProblemStatus.VALID;

import org.eventb.rubin.Sequent;
import org.eventb.rubin.tests.IProblem;
import org.eventb.rubin.tests.ProblemStatus;

/**
 * All sequents of this file have been extracted from chapter 3 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter03 implements IProblem {

	Example_3_1(VALID, "Rubin Example 3.1, p39",//
			"A -> (B -> C)",//
			"B & ~C",//
			"A",//
			"D & ~D"),

	Example_3_2a(VALID, "Rubin Example 3.2(a), p39",//
			"C -> A",//
			"C",//
			"B | C -> ~A",//
			"D & ~D"),

	Example_3_2b(INVALID, "Rubin Example 3.2(b), p39",//
			"A -> B | C",//
			"A",//
			"B -> ~A",//
			"C -> D",//
			"E & ~E"),

	Example_3_2c(VALID, "Rubin Example 3.2(c), p40",//
			"A -> B",//
			"C -> D",//
			"C -> A | ~D",//
			"C | ~D",//
			"D | ~B",//
			"(A & ~C) | (~A & C)",//
			"E & ~E"),

	Example_3_3_1(INVALID, "Rubin Example 3.3(1), p41",//
			"A -> B",//
			"~A",//
			"B",//
			"E & ~E"),

	Example_3_3_2(INVALID, "Rubin Example 3.3(2), p41",//
			"A -> B",//
			"B -> C",//
			"C",//
			"~A",//
			"E & ~E"),

	Example_3_3_3(INVALID, "Rubin Example 3.3(3), p41",//
			"Pxy",//
			"Pyz",//
			"~Pxz",//
			"E & ~E"),

	Example_3_5(INVALID, "Rubin Example 3.5, p43",//
			"B -> D",//
			"A & ~D",//
			"~C & (A | D)",//
			"~A -> C",//
			"E & ~E"),

	Exercise_3_A1(VALID, "Rubin Exercise 3.A(1), p46",//
			"A -> B",//
			"~A -> B",//
			"~B",//
			"E & ~E"),

	Exercise_3_A2(INVALID, "Rubin Exercise 3.A(2), p46",//
			"A -> B | C",//
			"~A",//
			"B | C",//
			"E & ~E"),

	Exercise_3_A3(INVALID, "Rubin Exercise 3.A(3), p46",//
			"A & ~B -> C & D",//
			"~B",//
			"~C",//
			"E & ~E"),

	Exercise_3_A4(INVALID, "Rubin Exercise 3.A(4), p46",//
			"A -> B | C",//
			"B -> D",//
			"C -> ~D",//
			"A",//
			"E & ~E"),

	Exercise_3_A5(VALID, "Rubin Exercise 3.A(5), p46",//
			"A -> B",//
			"B -> C & D",//
			"C -> ~E",//
			"D -> E",//
			"~A & B",//
			"E & ~E"),

	Exercise_3_A6(INVALID, "Rubin Exercise 3.A(6), p46",//
			"B -> ~A",//
			"C <-> B",//
			"C -> ~D",//
			"~D -> ~E",//
			"A <-> E",//
			"E & ~E"),

	Exercise_3_A7(VALID, "Rubin Exercise 3.A(7), p46",//
			"A -> ~B",//
			"~A -> ~C",//
			"~C -> ~D",//
			"~B -> ~E",//
			"D & E",//
			"F & ~F"),

	Exercise_3_A8(VALID, "Rubin Exercise 3.A(8), p46",//
			"A -> ~B",//
			"A | ~A",//
			"~A -> ~C",//
			"~(B -> ~C)",//
			"E & ~E"),

	Exercise_3_A9(INVALID, "Rubin Exercise 3.A(9), p46",//
			"A & B -> C | D",//
			"C -> ~D",//
			"B -> ~A",//
			"~A & ~B",//
			"C | D",//
			"E & ~E"),

	Exercise_3_A10(INVALID, "Rubin Exercise 3.A(10), p46",//
			"A -> B & C",//
			"~A -> B & D",//
			"B -> (D -> E)",//
			"C -> D",//
			"E -> B",//
			"F & ~F"),

	Exercise_3_A11(INVALID, "Rubin Exercise 3.A(11), p47",//
			"A & B -> C",//
			"A & ~B -> ~C",//
			"~A & B -> C",//
			"C <-> (A & B) | ~A",//
			"E & ~E"),

	Exercise_3_A12(INVALID, "Rubin Exercise 3.A(12), p47",//
			"A -> (C -> B)",//
			"A & C -> B | ~B",//
			"B -> ~A",//
			"~C -> ~B",//
			"E & ~E"),

	Exercise_3_A13(INVALID, "Rubin Exercise 3.A(13), p47",//
			"~(A -> B) -> A & ~B",//
			"A | ~A -> ~B",//
			"~(A -> B)",//
			"E & ~E"),

	Exercise_3_A14(INVALID, "Rubin Exercise 3.A(14), p47",//
			"(A -> B) <-> A & B",//
			"A -> ~B",//
			"(A -> B) -> A",//
			"~(A -> B)",//
			"E & ~E"),

	Exercise_3_A15(INVALID, "Rubin Exercise 3.A(15), p47",//
			"A -> (B -> C) & (~B -> D)",//
			"B & ~A -> C",//
			"~B & ~D",//
			"E & ~E"),

	/**
	 * 
	 * Translation table
	 * 
	 * <pre>
	 * 		   A: A is VALID
	 * 		   B: B is VALID
	 * 		   C: A -> B is of the form T -> T
	 * 		   D: A -> B is of the form T -> F
	 * 		   E: A -> B is VALID
	 * </pre>
	 */
	Exercise_3_A16(INVALID, "Rubin Exercise 3.A(16), p47",//
			"A -> (B -> C) & (~B -> D)",//
			"~B -> ~E",//
			"~C & E",//
			"F & ~F"),

	/**
	 * Translation table
	 * 
	 * <pre>
	 * 	   A: A is VALID
	 * 	   B: B is VALID
	 * 	   C: A -> B is VALID
	 * </pre>
	 */
	Exercise_3_A17(VALID, "Rubin Exercise 3.A(17), p47",//
			"(~A -> C) & (A -> (B -> C))",//
			"A & ~B -> C",//
			"~C",//
			"D & ~D"),

	/**
	 * Translation table
	 * 
	 * <pre>
	 * 		   A: f is continuous on (a,b)
	 * 		   B: f has a maximum on [a,b]
	 * 		   C: f has a minimum on [a,b]
	 * 		   D: f is continuous at a
	 * 		   E: f is continuous at b
	 * </pre>
	 */
	Exercise_3_A18(INVALID, "Rubin Exercise 3.A(18), p47",//
			"A -> (D & E -> B & C)",//
			"~B -> ~A",//
			"~C -> ~D",//
			"B -> E",//
			"C -> ~E",//
			"C -> A",//
			"F & ~F");

	private final IProblem problem;

	private Chapter03(ProblemStatus status, String name, String... predImages) {
		problem = mProblem(status, name, predImages);
	}

	@Override
	public Sequent sequent() {
		return problem.sequent();
	}

	@Override
	public ProblemStatus status() {
		return problem.status();
	}

	@Override
	public IProblem invalidVariant() {
		return problem.invalidVariant();
	}

}
