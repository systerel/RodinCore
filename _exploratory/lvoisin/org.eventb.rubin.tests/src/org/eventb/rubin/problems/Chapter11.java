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
public enum Chapter11 implements IProblem {

	Rules_Ia(VALID, "Rubin Rules Ia, p230",//
			"!x x = x"),

	Rules_Ic(VALID, "Rubin Rules Ic, p230",//
			"!x !y (x = y -> y = x)"),

	Rules_Id(VALID, "Rubin Rules Id, p230",//
			"!x !y !z (x = y & y = z -> x = z)"),

	Example_11_1(VALID, "Rubin Example 11.1, p231",//
			"!x (Px & Qx -> x = a | x = b)",//
			"~Ra",//
			"~ #x (Qx & Rx & ~Px)",//
			"!x (Qx & Rx -> x = b)"),

	Example_11_2(VALID, "Rubin Example 11.2, p232",//
			"Ja",//
			"Kb",//
			"!x (Jx -> Lx)",//
			"!x (Kx -> ~Lx)",//
			"a = b",//
			"A & ~A"),

	Example_11_3(INVALID, "Rubin Example 11.3, p233",//
			"#x (Fx & Gx)",//
			"Fa",//
			"a = b",//
			"Gb"),

	/* fails because of missing equality between variables */
	Example_11_5(VALID, "Rubin Example 11.5, p232",//
			"#x Ax & !y !z (Ay & Az -> y = z) <-> #x (Ax & !y (Ay -> x = y))"),

	Example_11_9a(VALID, "Rubin Example 11.9(a), p239",//
			"Cp",//
			"Nj",//
			"!x (Nx -> ~Cx)",//
			"j /= p"),

	Example_11_9b(VALID, "Rubin Example 11.9(b), p240",//
			"Ta",//
			"!x !y (Tx & Ry & x /= a -> Faxy)",//
			"#x #y (Tx & Ry & Wxy)",//
			"!x !y !z (Rz & Fxyz -> ~Wyz)",//
			"#x (Rx & Wax)"),

	Example_11_D1(INVALID, "Rubin Example 11.D(1), p243",//
			"#x (Ax & ~Bx)",//
			"Aa & Bb",//
			"a = b",//
			"E & ~E"),

	Example_11_D2(VALID, "Rubin Example 11.D(2), p243",//
			"~#x (Px & ~Rx)",//
			"~#x (Qx & Rx)",//
			"Pa",//
			"Qb",//
			"a = b",//
			"A & ~A"),

	Example_11_D3(VALID, "Rubin Example 11.D(3), p243",//
			"#x (Jx & !y (Jy -> x = y))",//
			"Ja",//
			"Jb",//
			"a /= b",//
			"A & ~A"),

	Example_11_D4(INVALID, "Rubin Example 11.D(4), p243",//
			"!x !y (Px & Qx & Py & Qy -> x = y)",//
			"Pa",//
			"Qb",//
			"a /= b",//
			"A & ~A"),

	Example_11_D5(VALID, "Rubin Example 11.D(5), p243",//
			"Pa | !x Qx",//
			"~Pb",//
			"a = b & ~Qc",//
			"A & ~A"),

	Example_11_D6(INVALID, "Rubin Example 11.D(6), p243",//
			"#x #y (x = y -> Px)",//
			"~#x Px",//
			"A & ~A"),

	Example_11_D7(INVALID, "Rubin Example 11.D(7), p243",//
			"!x #y (x /= y)",//
			"#x #y !z (z = x | z = y)",//
			"A & ~A"),

	/* fails because of singleton sets */
	Example_11_D8(VALID, "Rubin Example 11.D(8), p243",//
			"#x !y x = y",//
			"#x #y (x /= y)",//
			"A & ~A"),

	Example_11_D9(INVALID, "Rubin Example 11.D(9), p243",//
			"!x #y (Rxy -> Ryx)",//
			"#x (Px & ~Rxx)",//
			"#x !z (x /= z -> ~Px & Rzx & Rxz)",//
			"A & ~A"),

	Example_11_D13(VALID, "Rubin Example 11.D(13), p244",//
			"Pa",//
			"!x (Px -> #y Rxy)",//
			"!x (Rax -> x = b)",//
			"~Rab",//
			"A & ~A"),

	Example_11_D14(VALIDPPFAILS, "Rubin Example 11.D(14), p244",//
			"!x #y Rxy",//
			"!x !y !z !w (Rxy & Rzw -> y = w)",//
			"!y #x ~Rxy",//
			"A & ~A"),

	Example_11_D17(INVALID, "Rubin Example 11.D(17), p244",//
			"#x Px",//
			"!x (Px -> Qx)",//
			"#x #y x /= y",//
			"!x !y (Px & Qy -> Rxy)",//
			"~#x #y (Rxy & x /= y)",//
			"A & ~A"),

	Example_11_E1(INVALID, "Rubin Example 11.E(1), p244",//
			"#x (Jx & Kx)",//
			"!x (Jx -> x = c)",//
			"Ka & a = c"),

	/* fails because of missing equality between variables */
	Example_11_E2(VALID, "Rubin Example 11.E(2), p244",//
			"!x (Px | x = b)",//
			"~!x x = b",//
			"#x Px"),

	Example_11_E3(VALID, "Rubin Example 11.E(3), p244",//
			"#y !x (x = y -> Qx)",//
			"#x Qx"),

	Example_11_E4(INVALID, "Rubin Example 11.E(4), p244",//
			"Fa & Gb",//
			"!x (Fx -> ~Hx)",//
			"!x (~Gx -> Hx)",//
			"a = b"),

	/* fails because of missing equality between variables */
	Example_11_E5(VALID, "Rubin Example 11.E(5), p244",//
			"!x (Rax -> a = x | a = b)",//
			"#x Rax",//
			"Sa & ~Sb",//
			"Raa"),

	/* fails because of missing equality between variables */
	Example_11_E6(VALID, "Rubin Example 11.E(6), p244",//
			"#y !x (Fx <-> x = y)",//
			"!x (Fx -> Gx) <-> #x (Fx & Gx)"),

	/* fails because of missing singleton set rule */
	Example_11_E7(VALID, "Rubin Example 11.E(7), p244",//
			"!x !y (Rxy -> x = y)",//
			"#x Px",//
			"#x ~Px",//
			"#x #y ~Rxy"),

	/* fails because of missing equality between variables */
	Example_11_E8(VALID, "Rubin Example 11.E(8), p244",//
			"#x (Px & !y (Py -> x = y))",//
			"#x !y (Py <-> x = y)"),

	Example_11_E9(INVALID, "Rubin Example 11.E(9), p244",//
			"#x #y x /= y",//
			"#x Ax",//
			"!x (Ax -> Bx)",//
			"!x !y (Ax & By -> Cxy)",//
			"#x #y (Cxy & x /= y)"),

	Example_11_E10(VALID, "Rubin Example 11.E(10), p244",//
			"!x (Ax -> #y Bxy)",//
			"#x (Ax & ~Bxa)",//
			"#x #y (Bxy & y /= a)"),

	Example_11_E11(VALID, "Rubin Example 11.E(11), p244",//
			"!x !y (Ax & By -> x = y)",//
			"#x (Ax & Cx)",//
			"#x (Bx & Dx)",//
			"#x (Cx & Dx)"),

	Example_11_E16(INVALID, "Rubin Example 11.E(16), p245",//
			"!x !y !z (Rxy & Ryz -> Rxz)",//
			"!x !y #z (Rxz & Ryz)",//
			"!x !y (Rxy | Ryx)"),

	/*
	 * fails because of singleton sets -> add an instantiation rule for
	 * !x.#y.x/=y
	 */
	Example_11_E17(VALID, "Rubin Example 11.E(17), p245",//
			"#x (!y Fxy | !y Fyx)",//
			"!x (~Fxx | Gxa)",//
			"!x (x /= a -> ~Gxa)",//
			"!x !y (Gxa -> Gya)",//
			"#y !x x = y"),

	Example_11_E18(INVALID, "Rubin Example 11.E(18), p245",//
			"#x Rxx",//
			"!x !y (Sxxx -> Rxy)",//
			"!x !y (Sxyz -> x = y)",//
			"!x !y (Rxy -> #z Sxzy)",//
			"#x !y Rxy"),

	/* fails because of missing equality between variables -> this loops ?? */
	Example_11_E24(VALIDPPFAILS, "Rubin Example 11.E(24), p245",//
			"!x !y (Rxy -> ~Ryx)",//
			"!x !y (Rxy -> x /= y)",//
			"!x !y !z (Rxy & Ryz -> Rxz)",//
			"!x !z (x /= z -> #y Bxyz)",//
			"!x !y !z (Bxyz -> (Rxy & Ryz) | (Rzy & Ryx))",//
			"!x !z (Rxz -> #y (Rxy & Ryz))"),

	Example_11_F1(VALID, "Rubin Example 11.F(1), p245",//
			"!x (Px <-> #y (Px & Py))"),

	Example_11_F2(INVALID, "Rubin Example 11.F(2), p245",//
			"#x (Ax & Bx) & #x (Ax & x = a) -> #x (Bx & x = a)"),

	/* fails because of missing equality between variables */
	Example_11_F3(VALID, "Rubin Example 11.F(3), p245",//
			"#y !x (Fx <-> x = y) & #x (Fx & Gx) -> !x (Fx -> Gx)"),

	Example_11_F6(INVALID, "Rubin Example 11.F(6), p245",//
			"#x #y x /= y & #x Px & #x ~Qx -> #x #y (Px & ~Qy & x /= y)"),

	/* fails because of missing equality between variables */
	Example_11_F7(VALID, "Rubin Example 11.F(7), p245",//
			"#x (Px & !y (Py <-> x = y)) <-> #x !y (Py <-> x = y)"),

	Example_11_F8(VALID, "Rubin Example 11.F(8), p245",//
			"!x (Ax -> (Bx <-> #y (x = y & By)))");

	private final IProblem problem;

	private Chapter11(ProblemStatus status, String name, String... predImages) {
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
