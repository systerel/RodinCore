package org.eventb.internal.pp.translator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;

public abstract class FreeIdentifierDecomposition {
	public static Predicate decomposeIdentifiers(Predicate pred, FormulaFactory ff) {
		
		if(pred.getFreeIdentifiers().length > 0) {

			DecomposedQuant forall = new DecomposedQuant(ff);
			Map<FreeIdentifier, Expression> identMap =
				new HashMap<FreeIdentifier, Expression>();

			LinkedList<Predicate> bindings = new LinkedList<Predicate>();
			List<FreeIdentifier> freeIdentifiers = Arrays.asList(pred.getFreeIdentifiers());
			Collections.reverse(freeIdentifiers);
			
			for (FreeIdentifier ident: freeIdentifiers) {
				
				if(ident.getType() instanceof ProductType) {
					final Expression substitute =
						forall.addQuantifier(
								ident.getType(), ident.getName(), ident.getSourceLocation());

					identMap.put(ident, substitute);
					bindings.add(0, 
						ff.makeRelationalPredicate(Formula.EQUAL, ident, substitute,null));	
				}
			}
			if(identMap.size() > 0) {
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
