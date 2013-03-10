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
 * All sequents of this file have been extracted from chapter 7 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter07 implements IProblem {

	Exercise_7_A1(VALID, "Rubin Exercise 7.A(1), p151",//
			"!x (Ax -> Bx)",//
			"!x (Cx -> ~Bx)",//
			"!x (Ax -> ~Cx)"),

	Exercise_7_A2(VALID, "Rubin Exercise 7.A(2), p151",//
			"!x (Ax -> Bx | Cx)",//
			"!x (Bx -> Cx)",//
			"!x (Ax -> Cx)"),

	Exercise_7_A3(VALID, "Rubin Exercise 7.A(3), p152",//
			"!x (Ax -> ~Bx)",//
			"!x (Cx | ~Dx -> Bx)",//
			"Aa",//
			"~Ca & Da"),

	Exercise_7_A4(VALID, "Rubin Exercise 7.A(4), p152",//
			"!x (Ax | Bx -> ~Cx)",//
			"!x (Bx -> Dx)",//
			"!x (Dx -> Ax)",//
			"Ca",//
			"~Da"),

	Exercise_7_A5(VALID, "Rubin Exercise 7.A(5), p152",//
			"!x (Ax -> Bx | ~Cx)",//
			"!x (Ax & Bx -> Dx)",//
			"!x (Ax & ~Cx -> ~Ex)",//
			"!x (Ax & Ex -> Dx)"),

	Exercise_7_A6(VALID, "Rubin Exercise 7.A(6), p152",//
			"!x (Ax -> !y (By -> Cxy))",//
			"!x (Dx -> Bx)",//
			"!x (Ax -> !y (Dy -> Cxy))"),

	Exercise_7_A7(VALID, "Rubin Exercise 7.A(7), p152",//
			"!x (Ax -> !y (By -> Cxy))",//
			"!x (Ax -> Bx)",//
			"Ba & ~Cba",//
			"~Ab"),

	Exercise_7_A8(VALID, "Rubin Exercise 7.A(8), p152",//
			"!x (Ax -> Bx)",//
			"!y (By -> !x Ax)",//
			"!x Bx <-> Ba"),

	Exercise_7_A9(VALID, "Rubin Exercise 7.A(9), p152",//
			"!x!y!z (Rxy & Ryz -> Rxz)",//
			"!x!y (Rxy -> Ryx)",//
			"!x!y (Rxy -> Rxx)"),

	Exercise_7_A10(VALID, "Rubin Exercise 7.A(10), p152",//
			"!x!y!z (Rxy & Ryz -> Rxz)",//
			"!x!y (Rxy -> Ryx)",//
			"!x Rxa -> !x Rxx"),

	Exercise_7_A11(VALID, "Rubin Exercise 7.A(11), p152",//
			"!x!y (Ax & By -> ~Rxy)",//
			"Aa",//
			"!y (Cy -> Ray)",//
			"!z (Cz -> ~Bz)"),

	Exercise_7_A12(VALID, "Rubin Exercise 7.A(12), p152",//
			"!x (Ax -> ~Rxa)",//
			"Ab & Ba",//
			"!x (Cx -> Bx)",//
			"!x (Ax -> !y (By & Cy -> Rxy))",//
			"~Ca"),

	Exercise_7_A13(VALID, "Rubin Exercise 7.A(13), p152",//
			"!x!y (Rxy & Ryx -> Sxy)",//
			"!x!y(!z (Rxz & Ryz) -> Sxy)");

	private final IProblem problem;

	private Chapter07(ProblemStatus status, String name, String... predImages) {
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
