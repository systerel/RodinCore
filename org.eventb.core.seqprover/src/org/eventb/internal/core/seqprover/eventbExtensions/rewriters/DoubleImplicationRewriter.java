/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class DoubleImplicationRewriter extends DefaultRewriter {

	public DoubleImplicationRewriter(boolean autoFlattening) {
		super(autoFlattening);
	}
	
	@ProverRule("DERIV_IMP_IMP") 
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		final FormulaFactory ff = predicate.getFactory();
		if (!Lib.isImp(predicate)) {
			return predicate;
		}
		Predicate P = predicate.getLeft();
		Predicate right = predicate.getRight();
		if (!Lib.isImp(right)) {
			return predicate;
		}
		BinaryPredicate bRight = (BinaryPredicate) right;
		Predicate Q = bRight.getLeft();
		Predicate R = bRight.getRight();
		Predicate pAndq = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { P, Q }, null);
		return ff.makeBinaryPredicate(Predicate.LIMP, pAndq, R, null);
	}

}