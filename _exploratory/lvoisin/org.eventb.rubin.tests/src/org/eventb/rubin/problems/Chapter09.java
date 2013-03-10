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
 * All sequents of this file have been extracted from chapter 9 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter09 implements IProblem {

	Exercise_9_B1(INVALID, "Rubin Exercise 9.B(1), p199",//
			"!x (Ax -> Cx)",//
			"!x (Bx -> Dx)",//
			"#x Ax",//
			"#x ~Dx",//
			"#x (Cx & ~Bx)"),

	Exercise_9_B2(VALID, "Rubin Exercise 9.B(2), p199",//
			"!x (Px -> ~Qx)",//
			"!x (Qx | ~Rx)",//
			"#x ~Qx | #x Qx",//
			"#x (~Px | ~Rx)"),

	Exercise_9_B3(INVALID, "Rubin Exercise 9.B(3), p199",//
			"#x (Ax & Bx & ~Cx)",//
			"#x (Bx & ~Cx & Dx)",//
			"#x (Ax & Dx)"),

	Exercise_9_B4(INVALID, "Rubin Exercise 9.B(4), p200",//
			"!x (Px & Qx -> Rx)",//
			"!x (Px & ~Qx -> Sx)",//
			"!x (Rx | Sx)"),

	Exercise_9_B5(INVALID, "Rubin Exercise 9.B(5), p200",//
			"!x (Px & Qx -> Rx | Sx)",//
			"#x (Px & ~Rx)",//
			"#x Sx"),

	Exercise_9_B6(INVALID, "Rubin Exercise 9.B(6), p200",//
			"!x (Px -> !y (Qy -> Rxy))",//
			"!x (Px -> #y (~Rxy & Sy))",//
			"#x (Sx & ~Qx)"),

	Exercise_9_B7(VALID, "Rubin Exercise 9.B(7), p200",//
			"!x (Ax -> !y (~By -> Cxy))",//
			"#x (Ax & !y (Dy -> ~Cxy))",//
			"!x (Dx -> Bx)"),

	Exercise_9_B8(INVALID, "Rubin Exercise 9.B(8), p200",//
			"#x (Ex & !y (Fy -> Gxy))",//
			"!x!y (Ex -> (Gxy <-> Hy))",//
			"!x (Fx <-> Hx)"),

	Exercise_9_B9(VALID, "Rubin Exercise 9.B(9), p200",//
			"!x (Px -> !y (Qy <-> ~Rxy))",//
			"#x (Px & !y (~Sy -> Rxy))",//
			"!x!y (Px & Sy -> ~Rxy)",//
			"!x (Qx <-> Sx)"),

	Exercise_9_B10(INVALID, "Rubin Exercise 9.B(10), p200",//
			"!x (Ax -> (Bx -> Cx))",//
			"#x (Ax & Cx)",//
			"!x (Bx -> (Ax -> ~Cx))",//
			"#x (Ax & Bx)"),

	Exercise_9_B11(INVALID, "Rubin Exercise 9.B(11), p200",//
			"!x(!y (Axy -> Ry) -> Rx)",//
			"Aab & Ra",//
			"Rb"),

	Exercise_9_B12(INVALID, "Rubin Exercise 9.B(12), p200",//
			"!x!y!z (Rxy & Ryz -> Rxz)",//
			"!x#y Rxy",//
			"!x Rxx",//
			"!x!y (Rxy -> Ryx)"),

	Exercise_9_B13(VALID, "Rubin Exercise 9.B(13), p200",//
			"!x!y (Fx & Gy -> Hxy)",//
			"#x!y (Fx & (Jy -> ~Hxy))",//
			"!x (Gx -> ~Jx)"),

	Exercise_9_B14(INVALID, "Rubin Exercise 9.B(14), p200",//
			"!x!y!z (Rxy & Ryz -> Rxz)",//
			"!x Rxx",//
			"!x!y#z (Rxz & Ryz)",//
			"!x!y (Rxy | Ryx)"),

	Exercise_9_B15(VALID, "Rubin Exercise 9.B(15), p200",//
			"#x (Jx & Kx)",//
			"#z (Jz & !y (Ky -> ~Lzy))",//
			"#x (Jx & #z (Jz & ~Lzx))");

	private final IProblem problem;

	private Chapter09(ProblemStatus status, String name, String... predImages) {
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
