/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class DisjunctionToImplicationRewriter extends DefaultRewriter {

	public DisjunctionToImplicationRewriter(boolean autoFlattening) {
		super(autoFlattening);
	}

	@ProverRule("DEF_OR")
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		if (Lib.isDisj(predicate))
		{
			FormulaFactory ff = predicate.getFactory();
			Predicate[] disjuncts = Lib.disjuncts(predicate);
			assert disjuncts.length >= 2;
			Predicate firstDisjunct = disjuncts[0];
			Predicate[] restDisjuncts = new Predicate[disjuncts.length - 1];
			System.arraycopy(disjuncts,1,restDisjuncts,0,disjuncts.length - 1);
			return DLib.makeImp(DLib.makeNeg(firstDisjunct),
					DLib.makeDisj(ff, restDisjuncts)
					);
		}
		return predicate;
	}

}