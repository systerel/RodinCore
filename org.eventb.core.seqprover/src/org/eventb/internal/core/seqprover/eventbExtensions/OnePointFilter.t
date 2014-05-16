/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.LAND;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
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
 
@SuppressWarnings({"unused", "cast"})
public class OnePointFilter {
 
 	static class QuantifiedFormUtil {
 	
 		private final Expression element;
 		private final BoundIdentDecl[] boundIdents;
 		private final Predicate guard;
 		private final Expression expression;
 		
 		private QuantifiedFormUtil(Expression element, BoundIdentDecl[] boundIdents,
 									Predicate guard, Expression expression) {
 			this.element = element;
 			this.boundIdents = boundIdents;
 			this.guard = guard;
 			this.expression = expression;
 		}
 		
 		public Expression getElement() {
 			return element;
 		}
 		
 		public BoundIdentDecl[] getBoundIdents() {
 			return boundIdents;
 		}
 		
 		public Predicate getGuard() {
 			return guard;
 		}
 		
 		public Expression getExpression() {
 			return expression;
 		}
 		
 	}
 	
 	static class ReplacementUtil {
 	
 		private BoundIdentifier biToReplace;
 		private Expression replacementExpression;
 		
 		private ReplacementUtil(BoundIdentifier biToReplace, Expression replacementExpression) {
 			this.biToReplace = biToReplace;
 			this.replacementExpression = replacementExpression;
 		}
 		
 		public BoundIdentifier getBiToReplace() {
 			return biToReplace;
 		}
 		
 		public Expression getReplacementExpression() {
 			return replacementExpression;
 		}
 		
 	}
 	
 	static class MapletUtil {
 	
 		private RelationalPredicate leftEquality;
 		private RelationalPredicate rightEquality;
 		
 		private MapletUtil(RelationalPredicate leftEquality, RelationalPredicate rightEquality) {
 			this.leftEquality = leftEquality;
 			this.rightEquality = rightEquality;
 		}
 		
 		public RelationalPredicate getLeftEquality() {
 			return leftEquality;
 		}
 		
 		public RelationalPredicate getRightEquality() {
 			return rightEquality;
 		}
 		
 	}
 	
 	%include {FormulaV2.tom}
 	
 	public static boolean match(Predicate predicate) {
 		%match (Predicate predicate) {
 			In(_, Cset(_, _, _)) -> {
 				return true;
 			}
 		}
 		return false;
 	}
 	
 	public static QuantifiedFormUtil matchAndDissociate(Predicate predicate) {
 		%match (Predicate predicate) {
 			In(E, Cset(idents, guard, expression)) -> {
 				return new QuantifiedFormUtil(`E, `idents, `guard, `expression);
 			}
 		}
 		return null;
 	}
 	
 	public static ReplacementUtil matchReplacement(Predicate predicate) {
 		%match (Predicate predicate) {
 			Equal(bi1@BoundIdentifier(_), bi2@BoundIdentifier(_)) -> {
 				if(((BoundIdentifier)`bi1).getBoundIndex() < ((BoundIdentifier)`bi2).getBoundIndex()) {
 					return new ReplacementUtil((BoundIdentifier) `bi1, `bi2);
 				} else {
 					return new ReplacementUtil((BoundIdentifier) `bi2, `bi1);
 				}
 			}
 			Equal(Expr, bi@BoundIdentifier(_)) -> {
 				return new ReplacementUtil((BoundIdentifier) `bi, `Expr);
 			}
 			Equal(bi@BoundIdentifier(_), Expr) -> {
 				return new ReplacementUtil((BoundIdentifier) `bi, `Expr);
 			}
 		}
 		return null;
 	}
 	
	public static boolean isMapletEquality(RelationalPredicate pred) {
 		%match (Predicate pred) {
 			Equal(Mapsto(_, _), Mapsto(_, _)) -> {
 				return true;
 			}
 		}
 		return false;
	}

 	public static List<Predicate> splitMapletEquality(Predicate pred, FormulaFactory ff) {
 		%match (Predicate pred) {
 			Equal(left@Mapsto(_, _), right@Mapsto(_, _)) -> {
				final List<Predicate> conjuncts = new ArrayList<Predicate>();
 				splitMapletEquality(`left, `right, conjuncts, ff);
 				return conjuncts;
 			}
 		}
 		return Collections.singletonList(pred);
 	}

 	private static void splitMapletEquality(Expression left, Expression right,
 			List<Predicate> conjuncts, FormulaFactory ff) {
 		%match (Expression left, Expression right) {
 			Mapsto(A, B), Mapsto(C, D) -> {
 				splitMapletEquality(`A, `C, conjuncts, ff);
 				splitMapletEquality(`B, `D, conjuncts, ff);
 				return;
 			}
 		}
		conjuncts.add(ff.makeRelationalPredicate(EQUAL, left, right, null));
 	}

}