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

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.eventbExtensions.DLib;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.eventb.core.seqprover.eventbExtensions.Lib.*;

/**
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
		final DLib lib = mDLib(ff);
		// not not P == P
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  negPred(negPred(p));
		// a=a  == T
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return lib.True();
		// a/=a == F
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return lib.False();
		// a : {} == F
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return lib.False();
		// a /: {} == T
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return lib.True();
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
			Predicate exPred = lib.makeExQuant(boundIdentDecls,pred);
			// next lines commented out since type synthesis is now implemented
			// typeCheck(exPred);
			// assert exPred.isTypeChecked();
			return exPred;
		}
		return null;
	}

}
