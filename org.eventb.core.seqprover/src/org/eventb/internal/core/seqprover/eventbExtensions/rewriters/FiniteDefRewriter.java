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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.UPTO;

import java.math.BigInteger;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class FiniteDefRewriter extends DefaultRewriter {

	public FiniteDefRewriter(boolean autoFlattening, FormulaFactory ff) {
		super(autoFlattening, ff);
	}

	@ProverRule("DEF_FINITE")
	@Override
	public Predicate rewrite(SimplePredicate predicate) {
		if (Lib.isFinite(predicate)) {
			final Expression set = predicate.getChild(0);

			final BoundIdentDecl[] decls = new BoundIdentDecl[] {
					ff.makeBoundIdentDecl("n", null),
					ff.makeBoundIdentDecl("f", null) };

			final BoundIdentifier n = ff.makeBoundIdentifier(1, null);
			final BoundIdentifier f = ff.makeBoundIdentifier(0, null);
			final IntegerLiteral one = ff.makeIntegerLiteral(BigInteger.ONE,
					null);

			final Expression upTo = ff.makeBinaryExpression(UPTO, one, n, null);
			final Expression bij = ff.makeBinaryExpression(TBIJ, upTo, set,
					null);
			final Predicate inRel = ff
					.makeRelationalPredicate(IN, f, bij, null);

			final Predicate exists = ff.makeQuantifiedPredicate(EXISTS, decls,
					inRel, null);

			final ITypeEnvironment env = ff.makeTypeEnvironment();
			exists.typeCheck(env);

			return exists;
		}
		return predicate;
	}
}
