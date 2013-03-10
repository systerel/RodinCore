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
import static org.eventb.rubin.tests.ProblemStatus.VALID;

import org.eventb.rubin.Sequent;
import org.eventb.rubin.tests.IProblem;
import org.eventb.rubin.tests.ProblemStatus;

/**
 * All sequents of this file have been extracted from chapter 1 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter01 implements IProblem {

	Exercise_1_E1(VALID, "Rubin Exercise 1.E(1), p11",//
			"A | (A & B) <-> A"),

	Exercise_1_E2(VALID, "Rubin Exercise 1.E(2), p11",//
			"A & (A | B) <-> A"),

	Exercise_1_E3(VALID, "Rubin Exercise 1.E(3), p11",//
			"(A <-> B) <-> (A & B) | (~A & ~B)"),

	Exercise_1_E4(VALID, "Rubin Exercise 1.E(4), p11",//
			"~(A & B) <-> ~A | ~B"),

	Exercise_1_E5(VALID, "Rubin Exercise 1.E(5), p11",//
			"(A -> B) <-> (~A | B)"),

	Exercise_1_E6(VALID, "Rubin Exercise 1.E(6), p11",//
			"~(A -> B) <-> (A & ~B)"),

	Exercise_1_E7(VALID, "Rubin Exercise 1.E(7), p11",//
			"(A -> (B -> C)) <-> ((A & B) -> C)"),

	Exercise_1_E8(VALID, "Rubin Exercise 1.E(8), p11",//
			"(A | B -> C) <-> (A -> C) & (B -> C)");

	private final IProblem problem;

	private Chapter01(ProblemStatus status, String name, String... predImages) {
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
