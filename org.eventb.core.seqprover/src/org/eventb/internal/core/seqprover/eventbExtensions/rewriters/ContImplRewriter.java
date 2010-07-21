/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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

public class ContImplRewriter extends DefaultRewriter {

	public ContImplRewriter(boolean autoFlattening,
			FormulaFactory ff) {
		super(autoFlattening, ff);
	}

	@ProverRule("DERIV_IMP")
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		if (!Lib.isImp(predicate)) {
			return predicate;
		}
		Predicate P = predicate.getLeft();
		Predicate Q = predicate.getRight();
		Predicate notP = ff.makeUnaryPredicate(Predicate.NOT, P, null);
		Predicate notQ = ff.makeUnaryPredicate(Predicate.NOT, Q, null);
		return ff.makeBinaryPredicate(Predicate.LIMP, notQ, notP, null);
	}

}