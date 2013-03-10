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
import static org.eventb.rubin.tests.ProblemStatus.VALIDPPFAILS;

import org.eventb.rubin.Sequent;
import org.eventb.rubin.tests.IProblem;
import org.eventb.rubin.tests.ProblemStatus;

/**
 * All sequents of this file have been extracted from chapter 8 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter08 implements IProblem {

	Exercise_8_A1(VALID, "Rubin Exercise 8.A(1), p173",//
			"!x (Ax & Bx -> Cx)",//
			"#x (Bx & ~Cx)",//
			"#x ~Ax"),

	Exercise_8_A2(VALID, "Rubin Exercise 8.A(2), p173",//
			"!x (Px -> ~Qx | ~Rx)",//
			"#x (Qx & Rx)",//
			"#x ~Px"),

	Exercise_8_A3(VALID, "Rubin Exercise 8.A(3), p173",//
			"!x (Ax -> Cx)",//
			"!x (Bx -> Dx)",//
			"#x (Ax & Bx)",//
			"#x (Cx & Dx)"),

	Exercise_8_A4(VALID, "Rubin Exercise 8.A(4), p173",//
			"!x (Px -> Qx)",//
			"#x (Rx & ~Qx)",//
			"!x (Rx -> Px | Sx)",//
			"#x (Rx & Sx)"),

	Exercise_8_A5(VALID, "Rubin Exercise 8.A(5), p173",//
			"!x (~Ax -> Bx)",//
			"!x (~Bx | Cx)",//
			"#x ~Cx & #x Cx",//
			"Ca | #x Ax"),

	Exercise_8_A6(VALID, "Rubin Exercise 8.A(6), p173",//
			"!x (Px | Qx | Rx)",//
			"!x (Px -> ~Sx)",//
			"~Ra & Sa",//
			"#x (~Px & Qx)"),

	Exercise_8_A7(VALID, "Rubin Exercise 8.A(7), p173",//
			"!x !y (Ax & By -> Cxy)",//
			"#x (Ax & Cax)",//
			"#x (Bx & ~Cax)",//
			"#x ~Ax"),

	Exercise_8_A8(VALID, "Rubin Exercise 8.A(8), p173",//
			"#y !x Rxy",//
			"!x #y Rxy"),

	Exercise_8_A9(VALID, "Rubin Exercise 8.A(9), p173",//
			"#x Ax",//
			"#x Bx",//
			"!x (Ax -> !y (By -> Cxy))",//
			"#x #y Cxy"),

	Exercise_8_A10(VALID, "Rubin Exercise 8.A(10), p173",//
			"#x (Px & !y (Qy -> Rxy))",//
			"!x !y (Px & Rxy -> Sy)",//
			"!x (Sx -> Qx)",//
			"!x (Qx <-> Sx)"),

	Exercise_8_A11(VALID, "Rubin Exercise 8.A(11), p173",//
			"!x !y (Dx & Ey -> Fxy)",//
			"!x !y (Dx & Fxy -> Gy)",//
			"#x Dx -> !x (Ex -> Gx)"),

	Exercise_8_A12(VALID, "Rubin Exercise 8.A(12), p173",//
			"!x !y (Jx & ~Ky -> Lxy)",//
			"#x (Jx & !y (My -> ~Lxy))",//
			"!x (Mx -> Kx)"),

	Exercise_8_A13(VALID, "Rubin Exercise 8.A(13), p173",//
			"#x (Qx & !y (Py -> Rxy))",//
			"!x (Px -> !y (Qy & Sy -> ~Ryx))",//
			"#x Px -> #x ~Sx"),

	Exercise_8_A14(VALID, "Rubin Exercise 8.A(14), p173",//
			"#x (Px & !y (Py & Rxy -> Qya))",//
			"#x (Px & ~Qxa)",//
			"#x (~Px & Qxa)",//
			"#x #y (Px & Py & ~Rxy)"),

	Exercise_8_A15(VALID, "Rubin Exercise 8.A(15), p174",//
			"#x !y (Fx -> Gy)",//
			"!x Fx -> !x Gx"),

	Exercise_8_A16(VALID, "Rubin Exercise 8.A(16), p174",//
			"!x (#y (~Ay & Bxy) -> Cx)",//
			"#x Bxa",//
			"Aa | #x #y (Cx & Bxy)"),

	Exercise_8_A17(VALID, "Rubin Exercise 8.A(17), p174",//
			"!x (Px -> #y Qxy)",//
			"#x (Px & !y (Qxy -> Rxy))",//
			"#x #y Rxy"),

	Exercise_8_A18(VALID, "Rubin Exercise 8.A(18), p174",//
			"!x !y !z (Rxy & Ryz -> Rxz)",//
			"!x !y (Rxy -> Ryx)",//
			"!x #y Rxy -> !x Rxx"),

	/* fails */
	Exercise_8_A19(VALID, "Rubin Exercise 8.A(19), p174",//
			"!x !y !z (Rxz & Ryz -> Rxy)",//
			"!x #y Rxy",//
			"Rab & Rbc -> Rac"),

	Exercise_8_A20(VALIDPPFAILS, "Rubin Exercise 8.A(20), p174",//
			"!x !y (Px & Qy -> #z (Rz & Sxyz))",//
			"!x (Qx | Rx)",//
			"#x (Px & !y (Ry -> #z (Qz & Sxyz)))",//
			"#x !y #z Sxyz"),

	Exercise_8_A20_simp(VALID,
			"Simplification of Rubin Exercise 8.A(20), p174",//
			"!y Ry",//
			"!y (~Ry | #z Syz)",//
			"!z ~Sbz",//
			"E & ~E"),

	Exercise_8_A21(VALID, "Rubin Exercise 8.A(21), p174",//
			"#x #y Txy",//
			"!x !y (Txy -> Qx & Ry)",//
			"!x !y !z (Sxyz -> Px)",//
			"!x (!y !z (Qy & Rz -> Sxyz) -> Px)"),

	Exercise_8_A22(VALID, "Rubin Exercise 8.A(22), p174",//
			"!x (Px -> Qx)",//
			"!x !y (Qx | Txy -> Py)",//
			"!x (Rx -> !y (Py -> #z (Qz & Sxyz)))",//
			"!x !y (Rx & Txy -> #z (Py & Sxyz))"),

	/* fails reprogram seed search */
	Exercise_8_A23(VALID, "Rubin Exercise 8.A(23), p174",//
			"!x !y (Px <-> Sxy)",//
			"!x !y (Rxy -> Qy)",//
			"!x (Qx -> Tx)",//
			"#x Tx -> !y !z Ryz",//
			"!x (#y (Px & Rxy) <-> #z (Qz & Sxz))");

	private final IProblem problem;

	private Chapter08(ProblemStatus status, String name, String... predImages) {
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
