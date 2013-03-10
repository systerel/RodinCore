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
import static org.eventb.rubin.tests.ProblemStatus.VALIDPPFAILS;

import org.eventb.rubin.Sequent;
import org.eventb.rubin.tests.IProblem;
import org.eventb.rubin.tests.ProblemStatus;

/**
 * All sequents of this file have been extracted from chapter 10 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter10 implements IProblem {

	Example_10_1a(VALID, "Rubin Example 10.1(a), p206",//
			"!x (Px -> Qx) -> (#x Px -> #x Qx)"),

	Example_10_1b(INVALID, "Rubin Example 10.1(b), p206",//
			"!x!y (Px | ~Py)"),

	Example_10_1c(INVALID, "Rubin Example 10.1(c), p206",//
			"#x Px & #x Qx -> #x (Px & Qx)"),

	Example_10_1d(INVALID, "Rubin Example 10.1(d), p206",//
			"!x (Px | Qx) -> !x Px | !x Qx"),

	Example_10_2a(VALID, "Rubin Example 10.2(a), p209",//
			"((A & ~B & C) | (~A & ~B & C) | (B & C)) <-> C"),

	Example_10_2b(VALID, "Rubin Example 10.2(b), p209",//
			"~#x (Px & !y (Qy -> Rxy))",//
			"!x (Px -> #y (Qy & ~Rxy))"),

	Example_10_3a(VALID, "Rubin Example 10.3(a), p218",//
			"#x (Px & !y Rxy)",//
			"!y #x (Px & Rxy)"),

	Example_10_3b(VALID, "Rubin Example 10.3(b), p218",//
			"(!x Qx -> ~ #y #z Ryz) <-> #x !y !z (Qx -> ~Ryz)"),

	Exercise_10_B1(VALID, "Rubin Exercise 10.B(1), p222",//
			"#x (Px & Qx) -> (#y Py) & #z Qz"),

	Exercise_10_B2(VALID, "Rubin Exercise 10.B(2), p222",//
			"(!x Px | !y Qy) -> !z (Pz | Qz)"),

	Exercise_10_B3(INVALID, "Rubin Exercise 10.B(3), p222",//
			"(!x Px -> !x Qx) -> !x (Px -> Qx)"),

	/* fails */
	Exercise_10_B4(VALID, "Rubin Exercise 10.B(4), p222",//
			"!x (Px <-> Qx) -> (!x Px <-> !x Qx)"),

	Exercise_10_B5(INVALID, "Rubin Exercise 10.B(5), p222",//
			"(#x (Px & Qx) & #x (~Px & Rx)) -> #x (Qx & Rx)"),

	Exercise_10_B6(VALID, "Rubin Exercise 10.B(6), p222",//
			"#x (#y Py -> Qx) -> !x (Px -> #y Qy)"),

	Exercise_10_B7(INVALID, "Rubin Exercise 10.B(7), p222",//
			"(!x Px -> #y Qy) -> !x #y (Px -> Qy)"),

	/* failed because of missing arbitrary instantiations */
	Exercise_10_B8(VALID, "Rubin Exercise 10.B(8), p222",//
			"!x #y (Px & Qy) -> #y !x (Px & Qy)"),

	Exercise_10_B9(VALID, "Rubin Exercise 10.B(9), p222",//
			"#y !x (Px & Qy) -> !x #y (Px & Qy)"),

	Exercise_10_B10(INVALID, "Rubin Exercise 10.B(10), p222",//
			"!x !y !z (Rxy & Ryz -> Rxz)"),

	/* failed because of missing !x !y x \= y <=> FALSE - one-point rule */
	Exercise_10_B11(VALID, "Rubin Exercise 10.B(11), p222",//
			"!x (Px <-> ~Px) -> #x Qx"),

	Exercise_10_B12(VALID, "Rubin Exercise 10.B(12), p222",//
			"(!x Px -> #y ~Qy) -> (!x Qx -> #y ~Py)"),

	Exercise_10_B13(INVALID, "Rubin Exercise 10.B(13), p222",//
			"!x (Px -> Qx) & #x (Qx -> Pa) -> (#x Px -> Pa)"),

	Exercise_10_B14(VALID, "Rubin Exercise 10.B(14), p222",//
			"!x (Px -> !y (Qy -> Rxy)) & Qa & ~Rba -> ~Pb"),

	Exercise_10_B15(INVALID, "Rubin Exercise 10.B(15), p222",//
			"!x #y Rxy | ~!x #y Ryx"),

	Exercise_10_B16(VALID, "Rubin Exercise 10.B(16), p222",//
			"!x #y (Rxy | ~Ryx)"),

	Exercise_10_B17(INVALID, "Rubin Exercise 10.B(17), p222",//
			"!x #y Rxy | !x #y ~Rxy"),

	Exercise_10_B18(VALID, "Rubin Exercise 10.B(18), p222",//
			"!x #y Rxy | #y !x ~Ryx"),

	Exercise_10_B19(VALID, "Rubin Exercise 10.B(19), p223",//
			"#x (Px & !y (Qy -> Rxy))" + //
					" &  !x !y (Px & Rxy -> Sy)" + //
					" &  !x (Sx -> Qx)" + //
					" -> !x (Qx <-> Sx)"),

	Exercise_10_B20(INVALID, "Rubin Exercise 10.B(20), p223",//
			"!x !y (Px & Qy -> Rxy) & !x !y (Px & Rxy -> Sy) -> !x (Qx -> Sx)"),

	Exercise_10_B21(VALID, "Rubin Exercise 10.B(21), p223",//
			"!x (Px -> #y Qxy) & #x (Px & !y (Qxy -> Rxy)) -> #x #y Rxy"),

	Exercise_10_B22(INVALID, "Rubin Exercise 10.B(22), p223",//
			"(!x Px -> ~!x Qx) <-> #x !y (Px -> ~Qy)"),

	Exercise_10_B23(INVALID, "Rubin Exercise 10.B(23), p223",//
			"(~#x (Px & Qx) | #x Rx) <-> !x !y (~Px | ~Qx | Ry)"),

	/* fails */
	Exercise_10_B24(VALID, "Rubin Exercise 10.B(24), p223",//
			"!x (Fx -> ~#y Gxy) <-> !x !y (Fx -> ~Gxy)"),

	/* fails */
	Exercise_10_B25(VALID, "Rubin Exercise 10.B(25), p223",//
			"~#x (#y Py & Qx) <-> !x !y (Py -> ~Qx)"),

	Exercise_10_D1(VALID, "Rubin Exercise 10.D(1), p224",//
			"!x (Ax -> Bx)",//
			"~#x (Ax & Bx)",//
			"!x ~Ax"),

	Exercise_10_D2(VALID, "Rubin Exercise 10.D(2), p224",//
			"!x (Ax -> Bx | Cx)",//
			"~#x (Bx & Cx)",//
			"#x (Ax & Cx)",//
			"#x (Ax & ~Bx)"),

	Exercise_10_D3(VALID, "Rubin Exercise 10.D(3), p224",//
			"Pa",//
			"~#x (Px & Qx)",//
			"!x (~Rxa -> Qx)",//
			"#x Rxx"),

	Exercise_10_D4(VALID, "Rubin Exercise 10.D(4), p224",//
			"~#x !y (Py & ~Qx)",//
			"!x Px -> !x Qx"),

	Exercise_10_D5(VALID, "Rubin Exercise 10.D(5), p224",//
			"~#x (Px | Qx) | #x Rx",//
			"!x #y (Px | Qx -> Ry)"),

	Exercise_10_D6(VALID, "Rubin Exercise 10.D(6), p225",//
			"!x (Ax -> Bx)",//
			"#x (Cx & ~#y (By & Dxy))",//
			"#x (Cx & ~#y (Ay & Dxy))"),

	Exercise_10_D7(VALID, "Rubin Exercise 10.D(7), p225",//
			"!x (~Qx -> #y (Rxy | Qx))",//
			"~!x (Px -> Qx)",//
			"#x #y (Px & Rxy)"),

	Exercise_10_D8(VALID, "Rubin Exercise 10.D(8), p225",//
			"#x Px",//
			"Qa",//
			"~#x (Px & Rxa)",//
			"!x (Px -> !y (Sy & Qy -> Rxy))",//
			"~Sa"),

	Exercise_10_D9(VALID, "Rubin Exercise 10.D(9), p225",//
			"#x (Ax & Bx & Cx)",//
			"~#x (Ax & Bx & Dx)",//
			"#x (Bx & Cx & ~Dx)"),

	Exercise_10_D10(VALID, "Rubin Exercise 10.D(10), p225",//
			"#x (Fx & !y (Gy -> Hxy))",//
			"~#x #y (Fx & Hxy & Iy)",//
			"~#x (Gx & Ix)"),

	Exercise_10_D11(VALID, "Rubin Exercise 10.D(11), p225",//
			"!x (Fx -> Gx) -> #x (Hx & ~Ix)",//
			"!x (Hx -> Ix)",//
			"#x (Fx & ~Gx)"),

	Exercise_10_D12(VALID, "Rubin Exercise 10.D(12), p225",//
			"!x (Jx -> Kx) -> #x Lx | #x Mx",//
			"~#x Mx",//
			"#x (Lx | #x (Jx & ~Kx))"),

	Exercise_10_D13(VALID, "Rubin Exercise 10.D(13), p225",//
			"!x !y (Px & #z (Qz & Ryz) -> Sxy)",//
			"!x (#y (Py & Syx) -> Tx)",//
			"~Ta",//
			"#x Px -> !y (Qy -> ~Ray)"),

	/* fails because of missing seed generation */
	Exercise_10_D14(VALID, "Rubin Exercise 10.D(14), p225",//
			"!x (Ax -> Bx)",//
			"!x (~Cxx & Bx -> Ax)",//
			"#x (Dx & ~#y (By & Cxy))",//
			"!x (Ax -> !y (Ay -> Cxy))",//
			"!x (Dx -> Ax) <-> ~!x (Bx -> Cxx)"),

	Exercise_10_D15(VALID, "Rubin Exercise 10.D(15), p225",//
			"!x (Jx & Kx -> (Lx <-> ~#y (Myx & ~Ny)))",//
			"~!x (Kx -> Jx)",//
			"!x (Jx & Kx & Lx -> Na)",//
			"!x (Jx & Kx & !y (Myx -> Ny) -> Na)"),

	Exercise_10_D16(VALIDPPFAILS, "Rubin Exercise 10.D(16), p225",//
			"!x (Px & #y (Qy & Rxy) -> #z (Sz & Txz))",//
			"!x !y (Px -> Rxy)",//
			"#x Px",//
			"!x !y (Sy -> ~Txy)",//
			"#x !y (Qy -> ~Rxy)"),

	/* fails */
	Exercise_10_D17(VALID, "Rubin Exercise 10.D(17), p225",//
			"#x (Px & Rxa)",//
			"Sa",//
			"!x (Px & ~#y (Qy & Rxy) -> ~#z (Sz & Rxz))",//
			"#x #y (Px & Qy & Rxy)");

	private final IProblem problem;

	private Chapter10(ProblemStatus status, String name, String... predImages) {
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
