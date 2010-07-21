/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class CompImgRewriterImpl extends DefaultRewriter {

	private Expression subExp;

	public CompImgRewriterImpl(Expression subExp, FormulaFactory ff) {
		super(true, ff);
		this.subExp = subExp;
	}
		
	%include {FormulaV2.tom}
	
	@ProverRule("DERIV_RELIMAGE_FCOMP")
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {

			/**
	    	 * Set Theory : (p;...;q;r;...;s)[S] == (r;...;s)[(p;...;q)[S]]
	    	 */
			RelImage(Fcomp(children), S) -> {
				Collection<Expression> pToQ = new ArrayList<Expression>();
				Collection<Expression> rToS = new ArrayList<Expression>();
				boolean found = false;				
				for (Expression child : `children) {
					if (found)
						rToS.add(child);
					else if (child == subExp) {
						found = true;
						rToS.add(child);
					}
					else {
						pToQ.add(child);
					}
				}
				
				if (pToQ.size() == 0 || rToS.size() == 0)
					return expression;
				
				Expression pToQComp = makeCompIfNeccessary(pToQ);
				Expression rToSComp = makeCompIfNeccessary(rToS);
				
				Expression pToQCompImg = ff.makeBinaryExpression(
						Expression.RELIMAGE, pToQComp, `S, null);
						
				return ff.makeBinaryExpression(Expression.RELIMAGE,	rToSComp,
						pToQCompImg, null);
			}
			
	    }
	    return expression;
	}

	private Expression makeCompIfNeccessary(Collection<Expression> children) {
		if (children.size() == 1)
			return children.iterator().next();
		else
			return ff.makeAssociativeExpression(Expression.FCOMP, children, null);	
	}
}
