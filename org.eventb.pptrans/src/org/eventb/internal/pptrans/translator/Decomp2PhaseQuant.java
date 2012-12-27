/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;

/**
 * Specializes the decomposed quantification. Adding quantifiers and pushing 
 * expressions may now be intermixed. This comfort is paid by doing the whole work twice,
 * once in phase one and again in phase two.
 */
public abstract class Decomp2PhaseQuant extends DecomposedQuant {
	
	int count = 0;
	
	boolean recording = true;
	
	public Decomp2PhaseQuant(FormulaFactory ff) {
		super(ff);
	}
	
	/**
	 * In the recording phase (phase one) nothing is done, since exact offset is not
	 * yet nown. In the second phase the externally bound identifiers are shifted.
	 * @param expr the expression to be pushed
	 * @return a new expression with shifted bound identifiers (if in phase 2).
	 */
	@Override
	public Expression push(Expression expr) {
		if(recording) return expr;
		else return expr.shiftBoundIdentifiers(count, ff);
	}
	
	/**
	 * Starts the second phase of the quantification generation. In this phase, the
	 * total amount of quantifiers is known from the beginning. And externaly bound
	 * identifiers can be shifted properly. 
	 */
	public void startPhase2() {
		assert recording : "Recording was already finished";
		recording = false;
		count = offset();
		identDecls.clear();
	}
}
