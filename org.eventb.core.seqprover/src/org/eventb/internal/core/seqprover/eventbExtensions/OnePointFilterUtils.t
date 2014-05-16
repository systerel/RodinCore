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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.eventb.core.ast.IPosition;
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

/**
 * Implements with Tom algorithms used for the one point rule.
 * 
 * @author "Thomas Muller"
 */
@SuppressWarnings({"unused", "cast"})
public class OnePointFilterUtils {

	static class Replacement {
		private final BoundIdentifier boundIdent;
		private final Expression replacement;
		private final Predicate replacementPredicate;
		private final int conjIndex;

		public Replacement(BoundIdentifier boundIdent, Expression replacement,Predicate replacementPredicate,int conjIndex) {
			this.boundIdent = boundIdent;
			this.replacement = replacement;
			this.replacementPredicate = replacementPredicate;
			this.conjIndex = conjIndex;
		}

		public BoundIdentifier getBoundIdent() {
			return boundIdent;
		}

		public Expression getReplacement() {
			return replacement;
		}

		public Predicate getReplacementPredicate(){
			return replacementPredicate;
		}

		public int getReplacementIndex(){
			return conjIndex;
		}

		// checks the validity and ordering of the replacement of boundIdent by
		// expression inside a quantified expression with identDecls
		public static boolean isValid(BoundIdentifier boundIdent,
				Expression expression, BoundIdentDecl[] identDecls) {
			final int idx = boundIdent.getBoundIndex();
			if (idx >= identDecls.length) {
				// Bound outside this quantifier
				return false;
			}
			for (final BoundIdentifier bi : expression.getBoundIdentifiers()) {
				if (bi.getBoundIndex() <= idx) {
					// BoundIdentifier in replacement expression is inner bound
					return false;
				}
			}
			return true;
		}

		private static <T> boolean contains(T[] array, T element) {
			return Arrays.asList(array).contains(element);
		}
	}

	public static class ToProcessStruct{
		public final BoundIdentDecl[] identDecls;
		public final List<Predicate> conjuncts;
		public final Predicate impRight;

		public ToProcessStruct(BoundIdentDecl[] identDecls,List<Predicate> conjuncts, Predicate impRight){
			this.identDecls = identDecls;
			this.conjuncts = conjuncts;
			this.impRight = impRight;
		}
	}

	%include {FormulaV2.tom}

	public static ToProcessStruct match(Predicate predicate) {
		%match (Predicate predicate) {

			ForAll(identDecls,
					Limp(impLeft,
							impRight)) -> {
								return new ToProcessStruct(`identDecls, getConjuncts(`impLeft), `impRight);      
							}

			Exists(identDecls, pred) -> {
				return new ToProcessStruct(`identDecls, getConjuncts(`pred), null);
			}
		}
		return null;
	}

	private static List<Predicate> getConjuncts(Predicate pred) {
		%match(Predicate pred) {
			Land(conjuncts) -> {
				return new ArrayList<Predicate>(Arrays.asList(`conjuncts));
			}
		}
		return new ArrayList<Predicate>(Collections.singleton(pred));
	}

	public static Replacement getReplacement(Predicate pred, BoundIdentDecl[] identDecls,Predicate replacementPredicate, int conjIndex) {
		%match(Predicate pred) {

			Equal(boundIdent@BoundIdentifier(_), expression) -> {
				final BoundIdentifier boundIdent = (BoundIdentifier) `boundIdent;
				if (checkReplacement(pred, boundIdent, `expression, identDecls,replacementPredicate)){
					return new Replacement(boundIdent, `expression,pred,conjIndex);
				}
			}

			Equal(expression, boundIdent@BoundIdentifier(_)) -> {
				final BoundIdentifier boundIdent = (BoundIdentifier) `boundIdent;
				if (checkReplacement(pred, boundIdent, `expression, identDecls,replacementPredicate)){
					return new Replacement(boundIdent, `expression,pred,conjIndex);
				}
			}
		}
		return null;

	}

	private static boolean checkReplacement(Predicate replPred,BoundIdentifier boundIdent, Expression expression,BoundIdentDecl[] identDecls,Predicate replacementPredicate) {
		return (Replacement.isValid(boundIdent, expression, identDecls) && (replacementPredicate == null || replacementPredicate.equals(replPred)));
	}
}
