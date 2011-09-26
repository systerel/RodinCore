/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG;

import static org.eventb.core.ast.Formula.PFUN;
import static org.eventb.core.ast.Formula.PINJ;
import static org.eventb.core.ast.Formula.PSUR;
import static org.eventb.core.ast.Formula.REL;
import static org.eventb.core.ast.Formula.SREL;
import static org.eventb.core.ast.Formula.STREL;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.TFUN;
import static org.eventb.core.ast.Formula.TINJ;
import static org.eventb.core.ast.Formula.TREL;
import static org.eventb.core.ast.Formula.TSUR;

import java.util.HashSet;
import java.util.Set;

/**
 * Class used to get an order of the existing relation. This order is defined as
 * follow :
 * <p>
 * if <code>f∈ A op1 B ⇒ f∈ A op2 B</code> then <code>op1 > op2</code>
 * </p>
 * We say that op2 is inferred by op1.
 * 
 * 
 * @author Emmanuel Billaud
 */
public enum Operator {
	BIJECTION(TBIJ), //
	T_SURJECTION(TSUR, BIJECTION), //
	P_SURJECTION(PSUR, T_SURJECTION), //
	T_INJECTION(TINJ, BIJECTION), //
	P_INJECTION(PINJ, T_INJECTION), //
	T_FUNCTION(TFUN, T_INJECTION, T_SURJECTION), //
	P_FUNCTION(PFUN, T_FUNCTION, P_INJECTION, P_SURJECTION), //
	T_SURJECTIVE_RELATION(STREL, T_SURJECTION), //
	SURJECTIVE_RELATION(SREL, T_SURJECTIVE_RELATION, P_SURJECTION), //
	T_RELATION(TREL, T_FUNCTION, T_SURJECTIVE_RELATION), //
	RELATION(REL, P_FUNCTION, SURJECTIVE_RELATION, T_RELATION);

	private final int tag;
	private final Operator[] isInferredBy;

	private Operator(int tag, Operator... isInferredBy) {
		this.tag = tag;
		this.isInferredBy = isInferredBy;
	}

	public int getTag() {
		return this.tag;
	}

	public Set<Operator> getHigherRel() {
		Set<Operator> set = new HashSet<Operator>();
		set.add(this);
		for (Operator far : this.isInferredBy) {
			set.addAll(far.getHigherRel());
		}
		return set;
	}

	public boolean isInferredBy(Operator tested) {
		for (Operator far : this.getHigherRel()) {
			if (tested.equals(far)) {
				return true;
			}
		}
		return false;
	}

	public static Operator integerToOp(int tag) {
		switch (tag) {
		case REL:
			return RELATION;
		case TREL:
			return T_RELATION;
		case SREL:
			return SURJECTIVE_RELATION;
		case STREL:
			return T_SURJECTIVE_RELATION;
		case PFUN:
			return P_FUNCTION;
		case TFUN:
			return T_FUNCTION;
		case PINJ:
			return P_INJECTION;
		case TINJ:
			return T_INJECTION;
		case PSUR:
			return P_SURJECTION;
		case TSUR:
			return T_SURJECTION;
		case TBIJ:
			return BIJECTION;
		default:
			return null;
		}
	}
}
