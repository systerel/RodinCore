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

import static org.eventb.core.seqprover.eventbExtensions.Lib.eqLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.eqRight;
import static org.eventb.core.seqprover.eventbExtensions.Lib.getSet;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isEmptySet;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isEq;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isInclusion;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNotEq;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNotInclusion;
import static org.eventb.core.seqprover.eventbExtensions.Lib.negPred;
import static org.eventb.core.seqprover.eventbExtensions.Lib.notEqLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.notEqRight;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * Deprecated implementation for trivial rewrites.
 * <p>
 * This class must not be used in new code but is kept to ensure backward
 * compatibility of proof trees.
 * </p>
 * 
 * @author fmehta
 * @deprecated use {@link AutoRewrites} instead
 */
@Deprecated
public class TrivialRewriter implements Rewriter{

	public String getRewriterID() {
		return "trivialRewriter";
	}
	
	public String getName() {
		return "trivial";
	}
	
	public boolean isApplicable(Predicate p) {
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  true;
		
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return true;
		
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return true;
		
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return true;
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return true;
		
		if (isNotEq(p) && (isEmptySet(notEqRight(p)) || isEmptySet(notEqLeft(p))))
			return true;
		
		return false;
	}

	public Predicate apply(Predicate p, FormulaFactory ff) {
		// not not P == P
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  negPred(negPred(p));
		// a=a  == T
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return DLib.True(ff);
		// a/=a == F
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return DLib.False(ff);
		// a : {} == F
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return DLib.False(ff);
		// a /: {} == T
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return DLib.True(ff);
		// A /= {} <OR> {} /= A   ==   (#e. e:A)
		if (isNotEq(p) && (isEmptySet(notEqRight(p)) || isEmptySet(notEqLeft(p))))
		{
			Expression nonEmptySet = isEmptySet(notEqRight(p)) ?
					notEqLeft(p) : notEqRight(p);
			
			Type type = nonEmptySet.getType().getBaseType();
			assert type != null;
			// TODO : find a nice way to generate a name for this variable
			// Although it is bound freeing it may clutter the name space and
			// maybe force refactoring.
			String varName = "e";
			BoundIdentDecl[] boundIdentDecls = {ff.makeBoundIdentDecl(varName,null,type)};
			BoundIdentifier boundIdent = ff.makeBoundIdentifier(0,null,type);
			Predicate pred = ff.makeRelationalPredicate(RelationalPredicate.IN,boundIdent,nonEmptySet,null);
			Predicate exPred = DLib.makeExQuant(boundIdentDecls,pred);
			// next lines commented out since type synthesis is now implemented
			// typeCheck(exPred);
			// assert exPred.isTypeChecked();
			return exPred;
		}
		return null;
	}

}
