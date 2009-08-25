/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 
@SuppressWarnings("unused")
public class OnePointSimplifier {
  
    private static class Replacement {
        private final BoundIdentifier boundIdent;
        private final Expression replacement;

        public Replacement(BoundIdentifier boundIdent, Expression replacement) {
            this.boundIdent = boundIdent;
            this.replacement = replacement;
        }

        public BoundIdentifier getBoundIdent() {
            return boundIdent;
        }

        public Expression getReplacement() {
            return replacement;
        }

        // checks the validity of the replacement of boundIdent by expression
        // inside a quantified expression with identDecls
        public static boolean isValid(BoundIdentifier boundIdent,
                    Expression expression, BoundIdentDecl[] identDecls) {
            final BoundIdentifier[] boundIdents = expression.getBoundIdentifiers();
            
            return boundIdent.getBoundIndex() < identDecls.length
                    && !contains(boundIdents, boundIdent);
        }
            
        private static <T> boolean contains(T[] array, T element) {
            return Arrays.asList(array).contains(element);
        }
    
        public Predicate applyTo(QuantifiedPredicate predicate, FormulaFactory ff) {
            BoundIdentDecl[] identDecls = predicate.getBoundIdentDecls();
            final Expression[] replacements = new Expression[identDecls.length];
    
            final int boundIndex = boundIdent.getBoundIndex();
            final int replIndex = replacements.length - boundIndex - 1;
            if (replIndex >= 0) {
                replacements[replIndex] = replacement;
            }
            final Predicate rewritten = predicate.rewrite(new DefaultRewriter(true, ff) {
                @Override
                public Expression rewrite(BoundIdentifier identifier) {
                    if (identifier.getBoundIndex() == boundIdent.getBoundIndex()) {
                        return replacement;
                    }
                    return super.rewrite(identifier);
                }
            });
            return rewritten;
        }
    }
    
    private final Predicate predicate;
    private final Predicate replacementPredicate;
    private final FormulaFactory ff;
    private Replacement replacement;
    private Predicate simplifiedPredicate;
    private boolean matched = false;
    
    /**
	 * @param predicate
	 *            the predicate to apply one point rule to
	 * @param ff
	 *            a formula factory
	 */
    public OnePointSimplifier(Predicate predicate, FormulaFactory ff) {
       this(predicate, null, ff);
    }
    
    /**
	 * @param predicate
	 *            the predicate to apply one point rule to
	 * @param replacementPredicate
	 *            a replacement predicate to use when applying one point rule;
	 *            must be of the form "x = E" where x is a bound identifier and
	 *            must be found in the predicate
	 * @param ff
	 *            a formula factory
	 */
    public OnePointSimplifier(Predicate predicate, Predicate replacementPredicate,
                              FormulaFactory ff) {
       this.predicate = predicate;
       this.replacementPredicate = replacementPredicate;
       this.ff = ff;
    }
    
    public boolean isApplicable() {
    	return matched;
    }
    
    /**
	 * Returns the replacement expression found when matching.
	 * <p>
	 * The One Point Rule must be checked to be applicable before calling this
	 * method.
	 * </p>
	 * 
	 * @return the replacement expression
	 */
     public Expression getReplacement() {
        assert matched;
    	return replacement.getReplacement();
    }
    
    /**
	 * Applies the One Point Rule to simplify the predicate.
	 * <p>
	 * The One Point Rule must be checked to be applicable before calling this
	 * method.
	 * </p>
	 * 
	 * @return the replacement expression
	 */
    public Predicate getSimplifiedPredicate() {
        assert matched;
        return simplifiedPredicate;
	}

 	%include {FormulaV2.tom}
  
	public void match() {
     %match (Predicate predicate) {
	   
          QuantifiedPredicate(identDecls,
                 Limp(impLeft,
                      impRight)) -> {
                 processMatching(`identDecls, getConjuncts(`impLeft), `impRight);
          }
          
	      Exists(identDecls, Land(conjuncts)) -> {

             processMatching(`identDecls, `conjuncts, null);
	      }
	   }
	}

    private static Predicate[] getConjuncts(Predicate pred) {
        %match(Predicate pred) {
            Land(conjuncts) -> {
                return `conjuncts;
            }
        }
        return new Predicate[] {pred};
    }
	
	private void processMatching(BoundIdentDecl[] identDecls, Predicate[] conjuncts, Predicate impRight) {
        int conjIndex = 0;
        for (conjIndex = 0; conjIndex < conjuncts.length; conjIndex++) {
            replacement = getReplacement(conjuncts[conjIndex], identDecls);
            if (replacement != null) break;
        }
        if (replacement == null) {
            return;
        }
        final List<Predicate> conjunctsList = new ArrayList<Predicate>(Arrays.asList(conjuncts));
        conjunctsList.remove(conjIndex);
       
        final Predicate innerPred;
        if (impRight == null) { // simple conjunctive form
    	   innerPred = makeConjunction(conjunctsList);
        } else { // imply conjunctive form
            innerPred = makeInnerPred(conjunctsList, impRight);
        }
         
        simplifiedPredicate = makeSimplifiedPredicate(identDecls, innerPred);
        matched = true;
	}
	
	private Replacement getReplacement(Predicate pred, BoundIdentDecl[] identDecls) {

        %match(Predicate pred) {
		     Equal(boundIdent@BoundIdentifier(_), expression) -> {
		         final BoundIdentifier boundIdent = (BoundIdentifier) `boundIdent;
		         if (checkReplacement(pred, boundIdent, `expression, identDecls)) {
		             return new Replacement(boundIdent, `expression);
		         }
		     }
		   
		     Equal(expression, boundIdent@BoundIdentifier(_)) -> {
                 final BoundIdentifier boundIdent = (BoundIdentifier) `boundIdent;
                 if (checkReplacement(pred, boundIdent, `expression, identDecls)) {
                     return new Replacement(boundIdent, `expression);
                 }
		     }
		}
		return null;
	}

    private boolean checkReplacement(Predicate replPred, BoundIdentifier boundIdent,
                Expression expression, BoundIdentDecl[] identDecls) {
        return (Replacement.isValid(boundIdent, `expression, identDecls) 
                && (replacementPredicate == null
                    || replacementPredicate.equals(replPred)));
    }

    private Predicate makeSimplifiedPredicate(BoundIdentDecl[] identDecls,
			Predicate innerPred) {
        
		final QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(predicate
				.getTag(), identDecls, innerPred, null);

        return replacement.applyTo(qPred, ff);
	}

	private Predicate makeConjunction(List<Predicate> conjuncts) {
	   		final Predicate left;
		switch (conjuncts.size()) {
		case 0:
			return null;
		case 1:
			return conjuncts.iterator().next();
		default:
			return ff.makeAssociativePredicate(Predicate.LAND, conjuncts, null);
		}
	}	
	
	private Predicate makeInnerPred(List<Predicate> conjuncts,
			Predicate impRight) {
		final Predicate left = makeConjunction(conjuncts);

		final Predicate innerPred;
		if (left == null) {
			innerPred = impRight;
		} else {
			innerPred = ff.makeBinaryPredicate(Predicate.LIMP, left, impRight,
					null);
		}
		
		return innerPred;
	}

}