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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;

/**
 * This is the original implementation of free identifier decomposition. It is
 * now superseded by the new implementation that provides a sequent transformer.
 * 
 * @author Matthias Konrad
 */
@Deprecated
public abstract class FreeIdentifierDecomposition {
	
	public static Predicate decomposeIdentifiers(Predicate pred, FormulaFactory ff) {
		
		assert pred.isWellFormed();

		final FreeIdentifier[] freeIdentifiers = pred.getFreeIdentifiers();
		if (freeIdentifiers.length != 0) {
			DecomposedQuant forall = new DecomposedQuant(ff);
			Map<FreeIdentifier, Expression> identMap =
				new HashMap<FreeIdentifier, Expression>();
			LinkedList<Predicate> bindings = new LinkedList<Predicate>();
			for (int i = freeIdentifiers.length - 1; 0 <= i; --i) {
				FreeIdentifier ident = freeIdentifiers[i];
				if (ident.getType() instanceof ProductType) {
					final Expression substitute =
						forall.addQuantifier(
								ident.getType(), ident.getName(), ident.getSourceLocation());

					identMap.put(ident, substitute);
					bindings.add(0, 
						ff.makeRelationalPredicate(Formula.EQUAL, ident, substitute,null));	
				}
			}
			if (identMap.size() != 0) {
				pred = pred.substituteFreeIdents(identMap, ff);
				pred = forall.makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
							Formula.LIMP,
							bindings.size() > 1 ? 
								ff.makeAssociativePredicate(Formula.LAND, bindings,	null) :
								bindings.getFirst(),
							pred,
							null),
					null);
			}
		}
		return pred;
	}

}
