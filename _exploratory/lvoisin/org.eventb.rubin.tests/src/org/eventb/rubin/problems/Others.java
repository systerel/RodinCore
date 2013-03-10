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
import static org.eventb.rubin.tests.ProblemStatus.VALIDPPFAILS;

import org.eventb.rubin.Sequent;
import org.eventb.rubin.tests.IProblem;
import org.eventb.rubin.tests.ProblemStatus;

/**
 * Various sequents from theorem proving folklore.
 * 
 * @author Laurent Voisin
 */
public enum Others implements IProblem {

	Animals(VALIDPPFAILS,
			"Animals",//
			"#x Wx",//
			"#x Fx",//
			"#x Bx",//
			"#x Sx",//
			"#x Gx",//
			"!x (Wx -> Ax)",//
			"!x (Fx -> Ax)",//
			"!x (Bx -> Ax)",//
			"!x (Sx -> Ax)",//
			"!x (Gx -> Px)",//
			"!x!y (((Sx | Cx) & By) -> Mxy)",//
			"!x!y ((Bx & Fy) -> Mxy)",//
			"!x!y ((Fx & Wy) -> Mxy)",//
			"!x!y ((Wx & (Fy | Gy)) -> ~Exy)",//
			"!x!y ((Bx & Sy) -> ~Exy)",//
			"!x ((Cx | Sx) -> #y (Py & Exy))",//
			"!x (Ax -> (!y (Py -> Exy) | !y (Ay & Myx & #z (Pz & Eyz) -> Exy)))",//
			"#x#y#z (Ax & Ay & Gz & Eyz & Exy)"),

	AnimalsPlus(VALIDPPFAILS,
			"Animals plus",//
			"#x Wx",//
			"#x Fx",//
			"#x Bx",//
			"#x Cx",//
			"#x Sx",//
			"#x Gx",//
			"!x (Wx -> Ax)",//
			"!x (Fx -> Ax)",//
			"!x (Bx -> Ax)",//
			"!x (Cx -> Ax)",//
			"!x (Sx -> Ax)",//
			"!x (Gx -> Px)",//
			"!x!y (((Sx | Cx) & By) -> Mxy)",//
			"!x!y ((Bx & Fy) -> Mxy)",//
			"!x!y ((Fx & Wy) -> Mxy)",//
			"!x!y ((Wx & (Fy | Gy)) -> ~Exy)",//
			"!x!y ((Bx & Cy) -> Exy)",//
			"!x!y ((Bx & Sy) -> ~Exy)",//
			"!x ((Cx | Sx) -> #y (Py & Exy))",//
			"!x (Ax -> (!y (Py -> Exy) | !y (Ay & Myx & #z (Pz & Eyz) -> Exy)))",//
			"#x#y#z (Ax & Ay & Gz & Eyz & Exy)");

	private final IProblem problem;

	private Others(ProblemStatus status, String name, String... predImages) {
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
