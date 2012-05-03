/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - added pred and succ (IR47, IR48)
 *     Systerel - move to tom-2.8
 *     Systerel - added option expandSetEquality
 *     Systerel - fixed ER10 in translateEqualsCard (see bug #3495675)
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.expanders.Expanders;
import org.eventb.pptrans.Translator.Option;

/**
 * Implements the predicate reduction rules: BR1-BR8, ER1-ER13, CR1-CR7
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings({"unused", "cast"})
public class Translator extends IdentityTranslator {

	private final boolean expandSetEquality;

	public static Predicate reduceToPredCalc(Predicate pred, FormulaFactory ff,
			Option[] options) {
		return new Translator(ff, options).translate(pred);
	}
	
	public Translator(FormulaFactory ff, Option[] options) {
		super(ff);
		final Set<Option> opts = new HashSet<Option>(Arrays.asList(options));
		expandSetEquality = opts.contains(Option.expandSetEquality);
	}

	private Predicate mNot(Predicate pred, SourceLocation loc) {
		return ff.makeUnaryPredicate(Formula.NOT, pred, loc);
	}

	private Predicate translateEqualsTRUE(Expression e, SourceLocation loc) {
		return translateEqual(
				ff.makeRelationalPredicate(
						Formula.EQUAL,
						e,
						ff.makeAtomicExpression(Formula.TRUE, loc),
						loc));
	}

	private Predicate translateInPow(Expression e, Expression t,
			SourceLocation loc) {
		final DecomposedQuant forall = new DecomposedQuant(ff);
		final Expression x = forall.addQuantifier(e.getType().getBaseType(), loc);

		return forall.makeQuantifiedPredicate(
			Formula.FORALL,
			ff.makeBinaryPredicate(
				Formula.LIMP,
				translateIn(x, forall.push(e), loc),
				translateIn(x, forall.push(t), loc), 
				loc),
		loc);
	}
	
	private Predicate translateSetEq(Expression s, Expression t,
			SourceLocation loc) {
		final DecomposedQuant forall = new DecomposedQuant(ff);
		final Expression x = forall.addQuantifier(s.getType().getBaseType(), loc);

		return forall.makeQuantifiedPredicate(
			Formula.FORALL,
			ff.makeBinaryPredicate(
				Formula.LEQV,
				translateIn(x, forall.push(s), loc),
				translateIn(x, forall.push(t), loc), 
				loc),
		loc);
	}
	
	private Predicate translateLessMin(int tag, Expression left,
			Expression rightSet, SourceLocation loc) {
		final DecomposedQuant forall = new DecomposedQuant(ff);
		final Expression x = forall.addQuantifier(left.getType(), loc);

		return forall.makeQuantifiedPredicate(
			Formula.FORALL,
			ff.makeBinaryPredicate(
				Formula.LIMP,
				translateIn(x, forall.push(rightSet),	loc),
				translate(
					ff.makeRelationalPredicate(tag, forall.push(left), x, loc)
				),
				loc),
			loc);
	}

	private Predicate translateLessMax(int tag, Expression left,
			Expression rightSet, SourceLocation loc) {
    	final DecomposedQuant exists = new DecomposedQuant(ff);
		final Expression x = exists.addQuantifier(left.getType(), loc);
    	
    	return exists.makeQuantifiedPredicate(
    		Formula.EXISTS,
    		FormulaConstructor.makeLandPredicate(
    			ff,
    			translateIn (x, exists.push(rightSet), loc),
    			translate(
    				ff.makeRelationalPredicate(tag, exists.push(left), x, loc)
    			),
    			loc),
    		loc);
	}

	private Predicate translateMinLess(int tag, Expression leftSet,
			Expression right, SourceLocation loc) {
    	final DecomposedQuant exists = new DecomposedQuant(ff);
		final Expression x = exists.addQuantifier(right.getType(), loc);
    	
    	return exists.makeQuantifiedPredicate(
    		Formula.EXISTS,
    		FormulaConstructor.makeLandPredicate(
    			ff,
    			translateIn (x, exists.push(leftSet), loc),
    			translate(
    				ff.makeRelationalPredicate(tag, x, exists.push(right), loc)
    			),
    			loc),
    		loc);
	}

	private Predicate translateMaxLess(int tag, Expression leftSet,
			Expression right, SourceLocation loc) {
    	final DecomposedQuant forall = new DecomposedQuant(ff);
		final Expression x = forall.addQuantifier(right.getType(), loc);

    	return forall.makeQuantifiedPredicate(
    		Formula.FORALL,
    		ff.makeBinaryPredicate(
    			Formula.LIMP,
    			translateIn(x, forall.push(leftSet),	loc),
    			translate(
	    			ff.makeRelationalPredicate(tag, x, forall.push(right), loc)
    			),
    			loc),
    		loc);	   
	}

	private Predicate translateEqualsCard(Expression n, Expression s,
			SourceLocation loc) {
		final DecomposedQuant exists = new DecomposedQuant(ff);
		final Expression zero = ff.makeIntegerLiteral(BigInteger.ZERO, loc);
		final Predicate positiveness = ff.makeRelationalPredicate(Formula.LE,
				zero, n, loc);
		final Expression bij = ff.makeBinaryExpression(
				Formula.TBIJ,
				s,
				ff.makeBinaryExpression(
				        Formula.UPTO,
						ff.makeIntegerLiteral(BigInteger.ONE, null),
						n,
						loc),
				loc);
		final Expression f = exists.addQuantifier(bij.getType().getBaseType(),
				"f", loc);
		final Predicate quantifiedPred = exists.makeQuantifiedPredicate(
				Formula.EXISTS, translateIn(f, exists.push(bij), loc), loc);
		final Predicate[] predicates = { positiveness,  //
				                          quantifiedPred //
										};
		return ff.makeAssociativePredicate(Formula.LAND, predicates, loc);
	}

	private Predicate translateEqualsMinMax(Expression n, Expression minMax,
			Expression s, SourceLocation loc) {
		final Predicate rel;
		if (minMax.getTag() == Formula.KMIN) {
			rel = ff.makeRelationalPredicate(Formula.LE, n, minMax, loc);
		} else {
			rel = ff.makeRelationalPredicate(Formula.LE, minMax, n, loc);
		}
		return FormulaConstructor.makeLandPredicate(
			ff,
			translateIn(n, s, loc),
			translate(rel),
			loc);
	}

	%include {FormulaV2.tom}

	@Override
	protected Predicate translate(Predicate pred) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
	    	/**
	    	 *	All ∈ rules are implemented in translateIn
	    	 */
	    	In(e, rhs) -> {
	    		Predicate result = translateIn(`e, `rhs, loc);
	    		return result != null ? result : super.translate(pred);
	    	}
	    	/**
	    	 *	All = rules are implemented in translateEqual
	    	 */
	    	Equal(_, _) -> {
	        	return translateEqual(pred);
	        }
	        /**
	 		*  RULE BR1: 	s ⊆ t
	 		* 				s ∈ ℙ(t)
	 		*/	   		      	
	    	SubsetEq(s, t) -> {
	    		return translateIn(`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc);   				
	    	}
	        /**
	 		*  RULE BR2: 	s ⊈ t
	 		* 				¬(s ∈ ℙ(t))
	 		*/	   		      	
	    	NotSubsetEq(s, t) -> {
	    		return ff.makeUnaryPredicate(
	    			Formula.NOT, 
	    			translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc),
	    			loc);
	    	}
	        /**
	 		*  RULE BR3:	s ⊂ t
	 		* 				s ∈ ℙ(t) ∧ ¬(t ∈ ℙ(s))
	 		*/	   		      	
	    	Subset(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
	    			translateIn(`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc),
	    			ff.makeUnaryPredicate(
	    				Formula.NOT,
		    			translateIn (`t, ff.makeUnaryExpression(Formula.POW, `s, loc), loc),
		    			loc),
			    	loc);	    				
	    	}
	        /**
	 		*  RULE BR4: 	s ⊄ t
	 		* 				¬(s ∈ ℙ(t)) ∨ t ∈ ℙ(s)
	 		*/	   		      	
	    	NotSubset(s, t) -> {
				return FormulaConstructor.makeLorPredicate(
					ff,
	    			ff.makeUnaryPredicate(
		    			Formula.NOT,
			    		translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc),
			    		loc),
		    		translateIn(`t, ff.makeUnaryExpression(Formula.POW, `s, loc), loc),
			    	loc);	    				
	    	}
	        /**
	 		*  RULE BR5: 	x ≠ y
	 		* 				¬(x = y)
	 		*/	   		      	
	    	NotEqual (x, y) -> {
	        	return ff.makeUnaryPredicate(
	        		Formula.NOT, 
	        		translateEqual(
	        			ff.makeRelationalPredicate(Formula.EQUAL, `x, `y, loc)),
	        		loc);
	        }	
	        /**
	        * RULE BR6: 	x ∉ s
	        *	   			¬(x ∈ s)
	        */
	    	NotIn(x, s) -> {
	    		return ff.makeUnaryPredicate(
	    			Formula.NOT, 
	    			translateIn (`x, `s, loc),
	    			loc);
	    	}
	        /**
	        * RULE BR7: 	finite(s)
	        *	  			∀a·∃b,f·f∈(s''↣a'‥b)
	        */
	    	Finite(s) -> {
	    		final DecomposedQuant forall = new DecomposedQuant(ff);
	    		final DecomposedQuant exists = new DecomposedQuant(ff);

	    		final Type setElementType = `s.getType().getBaseType();
	    		final Type intType = ff.makeIntegerType();

				final Expression f = 
					exists.addQuantifier(
						ff.makePowerSetType(ff.makeProductType(setElementType, intType)), 
						"f", loc);
	    		final Expression b = exists.addQuantifier(intType, "b", loc);

	    		final Expression a = forall.addQuantifier(intType, "a", loc);
	    	    			    		
	    		return forall.makeQuantifiedPredicate(
	    			Formula.FORALL,
	    			exists.makeQuantifiedPredicate(
	    				Formula.EXISTS,
			    		translateIn(
			    			f,
			    			ff.makeBinaryExpression(
			    				Formula.TINJ,
			    				DecomposedQuant.pushThroughAll(`s, ff, forall, exists),
			    				ff.makeBinaryExpression(
			    					Formula.UPTO, 
			    					exists.push(a), 
			    					b, 
			    					loc),
			    				loc),
			    			loc),
			    		loc),
			    	loc);
	    	}
	    	
	       /**
	        * RULE BR8: 	partition (s, sn , sn , . . . , sn )
	        *               s = s1 ∪ s2 ∪ · · · ∪ sn
            *               s1 ∩ s2 = ∅
            *               .
            *               .
            *               .
            *               s1 ∩ sn = ∅
            *               .
            *               .
            *               .
            *               sn−1 ∩ sn = ∅
            */
	    	Partition(_) -> {
	    		return translate(Expanders.expandPARTITION(pred, ff));
	    	}
	    	
	    	/**
	 		 *  RULE CR1: 	a <∣≤ min(s) 
	 		 * 				∀x·x∈s' ⇒ a' <∣≤ x
	 		 */
	 		(Le|Lt)(a, Min(s)) -> {
	        	return translateLessMin(pred.getTag(), `a, `s, loc);
	        }
	 		/**
	 		 *  RULE CR2:	max(s) <∣≤ a
			 *				∀x·x∈s' ⇒ x <∣≤ a'
	 		 */
	        (Le|Lt)(Max(s), a) -> {
	        	return translateMaxLess(pred.getTag(), `s, `a, loc);
	        }
	 		/**
	 		 *  RULE CR3:   min(s) <∣≤  a
			 *				∃x·x∈s' ∧ x <∣≤ a'
	 		 */	   		      	
	        (Le|Lt)(Min(s), a) -> {
	        	return translateMinLess(pred.getTag(), `s, `a, loc);
	        }
	        /**
 	 		 *  RULE CR4: 	a <∣≤ max(s) 
	 		 * 				∃x·x∈s' ∧ a' <∣≤ x
	 		 */
	 	    (Le|Lt)(a, Max(s)) -> {
	        	return translateLessMax(pred.getTag(), `a, `s, loc);
	        }
	        /**
	         * RULE CR5:	a >|≥ b
	         *				b <|≤ a
	         */
	        (Ge|Gt)(a, b) -> {
	        	return translate(
	        		ff.makeRelationalPredicate(
		        		pred.getTag() == Formula.GE ? Formula.LE : Formula.LT,
		        		`b,
		        		`a,
		        		loc)
		        );
	        }
	        /**
	         * RULE CR6:	a(bop(s)) <|≤ b
	         *				c∀ a(bop(s)∗) <|≤ b 
	         * RULE CR7:    a <|≤ b(bop(s))
	         *				c∀ a <|≤ b(bop(s)∗) 
	         */
	        
	    	(Le|Lt)(_, _) -> {
	    		Predicate newPred = Reorganizer.reorganize((RelationalPredicate)pred, ff);
				if (newPred != pred) {
					return translate(newPred);
				} else {
					return super.translate(pred);
				}
	    	}
	    }
	    return super.translate(pred);
	}
	
	protected Predicate translateIn(Expression e, Expression rhs, SourceLocation loc) {
		
		Predicate result = translateIn_E(e, rhs, loc);
		if(result != null) return result;

		result = translateIn_EF(e, rhs, loc);
		if(result != null) return result;
		
		result = translateIn_EF_G(e, rhs, loc);
		if(result != null) return result;
		
		result = translateIn_E_FG(e, rhs, loc);
		if(result != null) return result;
		
		result = translateIn_EF_GH(e, rhs, loc);
		return result;
	}
	
	protected Predicate translateIn_E(
		Expression e, Expression right, SourceLocation loc) {
	
		%match (Expression right) {
			/**
	         * RULE IR1: 	e ∈ s 
	         *	  			⊤ 		if type(e) = s
	         */
			s -> {
				if(`s.isATypeExpression()) {
					return  ff.makeLiteralPredicate(Formula.BTRUE, loc);	
				}
			}
	        /**
	         * RULE IR2: 	e ∈ ℙ(t)
	         *	  			∀X_T·X∈e' ⇒ X∈t'	where ℙ(T) = type(e)
	         */
			Pow(t) -> {
				return translateInPow(e, `t, loc);
			}
	        /**
	         * RULE IR2': 	e ∈ s↔t)
	         *	  			∀X_T·X∈e' ⇒ X∈s'×t' 	where ℙ(T) = type(e)
	         */
			Rel(s, t) -> {
				final Expression rhs =
						ff.makeBinaryExpression(Formula.CPROD, `s, `t, loc);
				return translateInPow(e, rhs, loc);
			}
	        /**
	         * RULE IR3: 	e ∈ f
	         *	  			c∃ (e*) ∈f
	         */
			Identifier() -> {
				if (!GoalChecker.isMapletExpression(e)) {
					final ConditionalQuant exists = new ConditionalQuant(ff);
					exists.condSubstitute(`e);					
					exists.startPhase2();
					final Expression x = exists.condSubstitute(`e);					
					return exists.conditionalQuantify(
						Formula.EXISTS,
						translateIn(x, exists.push(right), loc),
						this);
				} else {
					return ff.makeRelationalPredicate(Formula.IN, e, right, loc);
				}
			}
			/**
	         * RULE IR3': 	e ∈ f
	         *	  			c∀ (e*) ∈ f
	         */
			_ -> {
				final MapletDecomposer decomposer = new MapletDecomposer(ff);
				decomposer.decompose(e);
				if (decomposer.needsDecomposition()) {
					decomposer.startPhase2();
					final Expression lhs = decomposer.decompose(e);
					final Expression rhs = decomposer.push(right);
					final Predicate pred =
							ff.makeRelationalPredicate(Formula.IN, lhs, rhs, loc);
					return translate(decomposer.bind(pred));
				}
			}
			/**
	         * RULE IR4: 	e ∈ ℕ 
	         *	  			0 ≤ e
	         */
			Natural() -> {
				return translate(
					ff.makeRelationalPredicate(
						Formula.LE,
						ff.makeIntegerLiteral(BigInteger.ZERO, loc),
						e,
						loc));
			}
			/**
	         * RULE IR5: 	e ∈ ℕ1 
	         *	  			0 < e
	         */
			Natural1() -> {
				return translate(
					ff.makeRelationalPredicate(
						Formula.LT,
						ff.makeIntegerLiteral(BigInteger.ZERO, loc),
						e,
						loc));
			}
			/**
	         * RULE IR6: 	e ∈ {x·P∣f} 
	         *	  			∃x·P ∧ e'=f 
	         */
			Cset(is, P, f) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff, `is);

	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(`P),
						translateEqual(ff.makeRelationalPredicate(Formula.EQUAL, exists.push(e),`f, loc)),
						loc),
					loc);
			} 
			/**
	         * RULE IR7: 	e ∈ ⋂x·P∣f 
	         *	  			∀x·P ⇒ e'∈f 
	         */
			Qinter(is, P, f) -> {
				final DecomposedQuant forall = new DecomposedQuant(ff, `is);

	    		return forall.makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translate(`P),
						translateIn(forall.push(e), `f, loc),
						loc),
					loc);
			}
			/**
	         * RULE IR8: 	e ∈ ⋃x·P∣f 
	         *	  			∃x·P ∧ e'∈f 
	         */
			Qunion(is, P, f) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff, `is);

	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(`P),
						translateIn(exists.push(e), `f, loc),
						loc),
					loc);
			}
			/**
	         * RULE IR9: 	e ∈ union(s) 
	         *	  			∃x·x∈s' ∧ e'∈x 
	         */
			Union(s) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(`s.getType().getBaseType(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(x, exists.push(`s), loc),
						translateIn(exists.push(e), x, loc),
						loc),
					loc);
			}
			/**
	         * RULE IR10: 	e ∈ inter(s) 
	         *	  			∀x·x∈s' ⇒ e'∈x 
	         */
			Inter(s) -> {
				final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = 
					forall.addQuantifier(`s.getType().getBaseType(), loc);
	
	    		return forall.makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(x, forall.push(`s), loc),
						translateIn(forall.push(e), x, loc),
						loc),
					loc);
			}
			/**
	         * RULE IR11: 	e ∈ ∅ 
	         *				e ∈ {}
	         *	  			⊥ 
	         */
			EmptySet() -> {
				return ff.makeLiteralPredicate(Formula.BFALSE, loc);
			}
			SetExtension(eList()) -> {
				return ff.makeLiteralPredicate(Formula.BFALSE, loc);
			}
			/**
	         * RULE IR12: 	e ∈ r[w] 
	         *	  			∃X_T·X∈w' ∧ X↦e'∈r'	whereas ℙ(T) = type(dom(r))
	         */
			RelImage(r, w) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						((ProductType)`r.getType().getBaseType()).getLeft(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(x, exists.push(`w), loc),
						translateIn(
							ff.makeBinaryExpression(Formula.MAPSTO, x, exists.push(e), loc),
							exists.push(`r),
							loc),
						loc),
					loc);
			}
			/**
	         * RULE IR13: 	e ∈ f(w) 
	         *	  			∃X_T·w'↦X∈f' ∧ e'∈X	whereas T = ℙ(type(e))
	         */
			FunImage(f, w) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						ff.makePowerSetType(e.getType()), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							ff.makeBinaryExpression(
								Formula.MAPSTO, 
								exists.push(`w), 
								x,	
								loc),
							 exists.push(`f), 
							 loc
						),
						translateIn(exists.push(e), x, loc),
						loc),
					loc);
			}
			/**
	         * RULE IR14: 	e ∈ ran(r) 
	         *	  			∃X_T·X↦e' ∈ r' whereas ℙ(T) = type(dom(r)) 
	         */
			Ran(r) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						((ProductType)`r.getType().getBaseType()).getLeft(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, x, exists.push(e), loc), 
						exists.push(`r), 
						loc),
					loc);
			}
			/**
	         * RULE IR15: 	e ∈ dom(r) 
	         *	  			∃X_T·e'↦X ∈ r' whereas ℙ(T) = type(ran(r)) 
	         */
			Dom(r) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						((ProductType)`r.getType().getBaseType()).getRight(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, exists.push(e), x, loc), 
						exists.push(`r), 
						loc),
					loc);
			}
	        /**
	         * RULE IR16: 	e ∈{a1,...,an} 	
	         *	  			e=a1 ∨ ... ∨ e=an		
	         */
	        SetExtension(eList(a)) -> {
				return translateEqual(
						ff.makeRelationalPredicate(Formula.EQUAL, e, `a, loc));
						
			}
			SetExtension(members) -> {
				final LinkedList<Predicate> predicates = new LinkedList<Predicate>();

				for(Expression member: `members){
					predicates.add(
						translate(
							ff.makeRelationalPredicate(
								Formula.EQUAL, e, member, loc)));
				}
				return FormulaConstructor.makeLorPredicate(ff, predicates, loc);
			}
			
	        /**
	         * RULE IR17:	e ∈ ℙ1(s)
	         *				e ∈ ℙ(s) ∧ (∃X_T·X ∈ e')	whereas ℙ(T) = type(e) 	  			
	         */
			Pow1(s) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = exists.addQuantifier(e.getType().getBaseType(), loc);
				
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(
						e, 
						ff.makeUnaryExpression(Formula.POW, `s, loc), 
						loc),
					exists.makeQuantifiedPredicate(
						Formula.EXISTS,
						translateIn(x, exists.push(e), loc),
						loc),
					loc);					
			}
	        /**
	         * RULE IR18:	e ∈ a‥b 	 
	         *	  			a ≤ e ∧ e ≤ b
	         */
			UpTo(a, b) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translate(ff.makeRelationalPredicate(
						Formula.LE,
						translate(`a),
						translate(e), 
						loc)),
					translate(ff.makeRelationalPredicate(
						Formula.LE,
						translate(e), 
						translate(`b),
						loc)),
					loc);
			}
	        /**
	         * RULE IR19:	e ∈ s ∖ t 	 
	         *	  			e∈s ∧ ¬(e∈t)
	         */
			SetMinus(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(e, `s, loc),
					ff.makeUnaryPredicate(
						Formula.NOT, 
						translateIn(e, `t, loc), 
						loc),
					loc);
			}
	        /**
	         * RULE IR20:	e ∈ s1 ∩ ... ∩ sn 	 
	         *	  			e∈s1 ∧ ... ∧ e∈sn
	         * RULE IR21:	e ∈ s1 ∪ ... ∪ sn
	         * 				e∈s1 ∨ ... ∨ e∈sn
	         */
			(BInter|BUnion)(children) -> {
				final LinkedList<Predicate> preds = new LinkedList<Predicate>();
								
				int tag = right.getTag() == Formula.BINTER ? Formula.LAND : Formula.LOR;

				for(Expression child: `children) {
					preds.add(translateIn(e, child, loc));
				}
				return FormulaConstructor.makeAssociativePredicate(ff, tag, preds, loc);
			}
	        /**
	         * RULE IR23:	e ∈ st 	 
	         *	  			e∈s↔t ∧ s⊆dom(e)
	         */
			Trel(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.REL, `s, `t, loc), 
							loc),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`s,
								ff.makeUnaryExpression(Formula.KDOM, e, loc),
								loc)),
						loc);
			}
	        /**
	         * RULE IR24:	e ∈ st	 
	         *	  			e∈s↔t ∧ t⊆ran(e)
	         */
			Srel(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.REL, `s, `t, loc), 
							loc),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc)),
						loc);
			}
	        /**
	         * RULE IR25:	e ∈ st 	 
	         *	  			e∈st ∧ t⊆ran(e)
	         */
			Strel(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.TREL, `s, `t, loc), 
							loc),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc)),
						loc);
			}
	        /**
	         * RULE IR26:	e ∈ s⤖t 	 
	         *	  			e∈s↠t ∧ func(e^−1)
	         */
			Tbij(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.TSUR, `s, `t, loc), 
							loc),
					funcInv(e),
					loc);
			}
	        /**
	         * RULE IR27:	e ∈ s↠t	 
	         *	  			e∈s→t ∧ t⊆ran(e)
	         */
			Tsur(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.TFUN, `s, `t, loc), 
							loc),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc)),
						loc);			
			}
	        /**
	         * RULE IR28:	e ∈ s⤀t 	 
	         *	  			e∈s⇸t ∧ t⊆ran(e)
	         */
			Psur(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.PFUN, `s, `t, loc), 
							loc),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc)),
						loc);			
			}
	        /**
	         * RULE IR29:	e ∈ s↣t 	 
	         *	  			e∈s→t ∧ func(e^−1)
	         */
			Tinj(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.TFUN, `s, `t, loc), 
							loc),
						funcInv(e),
						loc);
			}			
	        /**
	         * RULE IR30:	e ∈ s⤔t 	 
	         *	  			e∈⇸t ∧ func(e^−1)
	         */
			Pinj(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.PFUN, `s, `t, loc), 
							loc),
						funcInv(e),
						loc);		
			}	
	        /**
	         * RULE IR31:	e ∈ s→t 	 
	         *	  			e∈s⇸t ∧ s⊆dom(e)
	         */
			Tfun(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.PFUN, `s, `t, loc), 
							loc),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`s,
								ff.makeUnaryExpression(Formula.KDOM, e, loc),
								loc)),
						loc);			
			}
	        /**
	         * RULE IR32:	e ∈ s⇸t 	 
	         *	  			e∈s↔t ∧ func(e)
	         */
			Pfun(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.REL, `s, `t, loc), 
							loc),
						func(e),
						loc);	
			}
		}					
		return null;
	}
	
	private Expression purifyMaplet(
		Expression expr, DecomposedQuant quant, List<Predicate> bindings) {
		SourceLocation loc = expr.getSourceLocation();
		
		if(GoalChecker.isMapletExpression(expr))
			return quant.push(expr);
		%match(Expression expr) {
			Mapsto(l, r) -> {
				Expression nr = purifyMaplet(`r, quant, bindings);
				Expression nl = purifyMaplet(`l, quant, bindings);

				if(nr == `r && nl == `l) return expr;
				else
					return ff.makeBinaryExpression(Formula.MAPSTO, nl, nr, loc);
			}
		}
		Expression substitute = quant.addQuantifier(expr.getType(), loc);
		bindings.add(0, 
				ff.makeRelationalPredicate(
						Formula.EQUAL, 
						substitute, 
						quant.push(expr), 
						loc));
		return substitute;
	}
	
	protected Predicate translateIn_EF(
		Expression expr, Expression rhs, SourceLocation loc){
		Expression e = null, f = null;
		%match(Expression expr) {
			Mapsto(left, right) -> {
				e = `left; f = `right;
			}
		}
		if(e == null) return null;
		
		%match(Expression rhs) {
	        /**
	         * RULE IR33:	e↦f ∈ s×t 	 
	         *	  			e∈s ∧ f∈t
	         */
			Cprod(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(e, `s, loc),
					translateIn(f, `t, loc),
					loc);			
			}
	        /**
	         * RULE IR34:	e↦f ∈ r1  ...  rn	
	         *	  			e ↦ f ∈ rn ∨ 
			 *				e ↦ f ∈ dom(rn) ⩤ rn−1 ∨ 
			 *				e ↦ f ∈ dom(rn) ∪ dom(rn−1) ⩤ rn−2 ∨	
			 *					 ...
			 *				e ↦ f ∈ dom(rn) ∪ ... ∪ dom(r2) ⩤ r1 
	         */
			Ovr(children) -> {
			
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				for (int i = 0; i < `children.length; i++) {
					LinkedList<Expression> exprs = new LinkedList<Expression>();
					for (int j = i + 1; j < `children.length; j++) {
						exprs.add(ff.makeUnaryExpression(Formula.KDOM, `children[j], loc));
					}	
					
					if (exprs.size() > 0) {
						final Expression sub;

						if (exprs.size() > 1) {
							sub = ff.makeAssociativeExpression(Formula.BUNION, exprs, loc);
						} else {
							sub = exprs.get(0);
						}
		
						preds.add(
							translateIn(
								expr, 
								ff.makeBinaryExpression(Formula.DOMSUB, sub, `children[i], loc),
								loc));
					}
					else {
						preds.add(translateIn(expr, `children[i], loc));
					}		
				}
				
				return FormulaConstructor.makeLorPredicate(ff, preds, loc);
			}
	        /**
	         * RULE IR35:	e↦f ∈ r ⩥ t	
	         *	  			e↦f ∈ r ∧ ¬(f∈t)
	         */
			RanSub(r, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc),
					ff.makeUnaryPredicate(
						Formula.NOT, translateIn(f, `T, loc), loc),
					loc);
			}
	        /**
	         * RULE IR36:	e↦f ∈ s ⩤ r
	         *	  			e↦f ∈ r ∧ ¬(e∈s)
	         */
			DomSub(S, r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc),
					ff.makeUnaryPredicate(Formula.NOT, translateIn(e, `S, loc), loc),
					loc);
			}
	        /**
	         * RULE IR37:	e↦f ∈ r ▷ t
	         *	  			e↦f ∈ r ∧ f ∈ t
	         */
			RanRes(r, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc),
					translateIn(f, `T, loc),
					loc);
			}
	        /**
	         * RULE IR38:	e↦f ∈ s ◁ r
	         *	  			e↦f ∈ r ∧ e ∈ s
	         */
			DomRes(S, r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc),
					translateIn(e, `S, loc),
					loc);
			}
	        /**
       		 * RULE IR39:	e↦f ∈ id
	         *	  			e=f
	         */
			IdGen() -> {
				return 
					translate(ff.makeRelationalPredicate(Formula.EQUAL, e, f, loc));
			}
	        /**
	         * RULE IR40:	e↦f ∈ r1; ;rn
	         *	  			∃X1_T1, ,Xn−1_Tn−1·	e↦X1 ∈ r1 ∧
			 *								   X1↦X2 ∈ r2 ∧
			 *										...
			 *								  Xn−1↦f ∈ rn
			 *		
			 *		whereas ℙ(T1)	= type(ran(r1))
			 *					   ...
			 *				ℙ(Tn−1)	= type(ran(rn−1))
	         */
			Fcomp(children) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);

				Expression[] X = new Expression[`children.length + 1];
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				
				for(int i = `children.length - 1; i > 0; i--) {
					Type type = ((ProductType)`children[i].getType().getBaseType()).getLeft();
					X[i] = exists.addQuantifier(type, loc);
				}		
					
				X[0] = exists.push(e);
				X[`children.length] = exists.push(f);
				
				for(int i = 0; i < `children.length; i++) {
					preds.add(
						translateIn(
							ff.makeBinaryExpression(Formula.MAPSTO, X[i], X[i+1], loc), 
							exists.push(`children[i]),
							loc));								
				}				
								
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					ff.makeAssociativePredicate(
						Formula.LAND,
						preds,
						loc),
					loc);						
			}
	        /**
	        * RULE IR41:	e ↦ f ∈ r1 ∘ ... ∘ rn
	        *	  			e↦f ∈ rn; ...; r1
	        */
			Bcomp(children) -> {
				List<Expression> reversedChilds = Arrays.asList(`children);
				Collections.reverse(reversedChilds);
				
				return translateIn(
					expr,
					ff.makeAssociativeExpression(Formula.FCOMP, reversedChilds, loc),
					loc);
			}
	        /**
	        * RULE IR42:	e ↦ f ∈ r^
	        *	  			f↦e ∈ r
	        */
			Converse(r) -> {
				return translateIn(ff.makeBinaryExpression(Formula.MAPSTO, f, e, loc), `r, loc);
			}
	        /**
	        * RULE IR47:	e ↦ f ∈ pred
	        *	  			e = f + 1
	        */
			PRED() -> {
				return translateEqual(equalsPlusOne(e, f, loc));
			}
	        /**
	        * RULE IR48:	e ↦ f ∈ succ
	        *	  			f = e + 1
	        */
			SUCC() -> {
				return translateEqual(equalsPlusOne(f, e, loc));
			}
		}
		return null;
	}
	
	private Predicate equalsPlusOne(Expression left, Expression right,
			SourceLocation loc) {
		final Expression one = ff.makeIntegerLiteral(BigInteger.ONE, null);
		final Expression plusOne = ff.makeAssociativeExpression(Formula.PLUS,
				Arrays.asList(right, one), loc);
		return ff.makeRelationalPredicate(Formula.EQUAL, left, plusOne, loc);
	}

	protected Predicate translateIn_EF_G(
		Expression expr, Expression rhs, SourceLocation loc) {
		Expression e = null, f = null, g = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), three) -> {
				e = `one; f = `two; g = `three;
			}
		}
		if(e == null) return null;
		
		%match(Expression rhs) {
	        /**
	        * RULE IR43:	(e↦f)↦g ∈ prj1
	        *	  			e=g
	        */
			Prj1Gen() -> {
				return translate(ff.makeRelationalPredicate(Formula.EQUAL, e, g, loc));
			}
	        /**
	        * RULE IR44:	(e↦f)↦g ∈ prj2
	        *	  			f=g
	        */
			Prj2Gen() -> {	
				return translate(ff.makeRelationalPredicate(Formula.EQUAL, f, g, loc));
			}
		}
		return null;
	}

	protected Predicate translateIn_E_FG(
		Expression expr, Expression rhs, SourceLocation loc) {
		
		Expression e = null, f = null, g = null;
		%match(Expression expr){
			Mapsto(one, Mapsto(two, three)) -> {
				e = `one; f = `two; g = `three;
			}
		}
		if(e == null) return null;

		%match(Expression rhs){
	        /**
	        * RULE IR45:	e↦(f↦g) ∈ p⊗q
	        *	  			e↦f∈p ∧ e↦g∈q
	        */
			Dprod(p, q) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `p, loc),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, g, loc), `q, loc),
					loc);
			}
		}
		return null;
	}

	protected Predicate translateIn_EF_GH(
		Expression expr, Expression rhs, SourceLocation loc){
		
		Expression e = null, f = null, g = null, h = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), Mapsto(three, four)) -> {
				e = `one; f = `two; g = `three; h = `four;
			}
		}
		if(e == null) return null;

		%match(Expression rhs) {
	        /**
	        * RULE IR46:	(e↦f)↦(g↦h) ∈ p∥q
	        *	  			e↦g∈p ∧ f↦h∈q
	        */
			Pprod(p, q) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, g, loc), `p, loc),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, f, h, loc), `q, loc),
					loc);
			}
		}
		return null;
	}
	
	protected Predicate func(Expression f) {
		return func(f, false);
	}
	
	protected Predicate funcInv(Expression f) {
		return func(f, true);
	}
	
	protected Predicate func(Expression f, boolean inverse) {
		SourceLocation loc = f.getSourceLocation();
		final DecomposedQuant forall = new DecomposedQuant(ff);

		Type dom = ((ProductType)f.getType().getBaseType()).getLeft();
		Type ran = ((ProductType)f.getType().getBaseType()).getRight();
		
		if(inverse) { Type t = dom; dom = ran; ran = t; }

		final Expression c = forall.addQuantifier(ran, loc);
		final Expression b = forall.addQuantifier(ran, loc);
		final Expression a = forall.addQuantifier(dom, loc);
		
		f = forall.push(f);
		
		return forall.makeQuantifiedPredicate(
			Formula.FORALL,
			ff.makeBinaryPredicate(
				Formula.LIMP,
				FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							inverse ? b : a, 
							inverse ? a : b, 
							loc),	
						f, 
						loc),
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							inverse ? c : a, 
							inverse ? a : c, 
							loc),	
						f, 
						loc),
					loc),
				translate(ff.makeRelationalPredicate(Formula.EQUAL, b, c, loc)),
				loc),
			loc);	
	}
	
	protected Predicate translateEqual(Predicate pred) {
		SourceLocation loc = pred.getSourceLocation();
		
		%match (Predicate pred) {
	        /**
	        * RULE ER1: 	e = e
	        *	  			⊤
	        */
			Equal(e, e) -> {
				return ff.makeLiteralPredicate(Formula.BTRUE, loc);
			}
	        /**
	        * RULE ER2: 	x↦y = a↦b  
	        *	  			x=a ∧ y=b
	        */
			Equal(Mapsto(x, y), Mapsto(a,b)) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateEqual(ff.makeRelationalPredicate(Formula.EQUAL, `x, `a, loc)),
					translateEqual(ff.makeRelationalPredicate(Formula.EQUAL, `y, `b, loc)),
					loc);						
			}
			/**
	        * RULE ER3: 	bool(P) = bool(Q)  
	        *	  			P ⇔ Q
	        */
			Equal(Bool(P), Bool(Q)) -> {
				
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					translate(`P),
					translate(`Q),
					loc);
			}
			/**
	        * RULE ER4: 	bool(P) = TRUE  
	        *	  			P
	        */
			Equal(TRUE(), Bool(P)) -> {
				return translate(`P);
			}
			Equal(Bool(P), TRUE()) -> {
				return translate(`P);
			}
			/**
	        * RULE ER5: 	bool(P) = FALSE  
	        *	  			¬P
	        */
			Equal(FALSE(), Bool(P)) -> {
				return ff.makeUnaryPredicate(Formula.NOT, translate(`P), loc);
			}
			Equal(Bool(P), FALSE()) -> {
				return ff.makeUnaryPredicate(Formula.NOT, translate(`P), loc);
			}
			/**
	        * RULE ER6: 	x = FALSE  
	        *	  			¬(x = TRUE)
	        */
			Equal(FALSE(), x) -> {
				return mNot(translateEqualsTRUE(`x, loc), loc);
			}
			Equal(x, FALSE()) -> {
				return mNot(translateEqualsTRUE(`x, loc), loc);
			}
			/**
	        * RULE ER7: 	x = bool(P)  
	        *	  			x = TRUE ⇔ P
	        */
	        Equal(x@Identifier(), Bool(P)) -> {
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					translateEqualsTRUE(`x, loc),
					translate(`P),
					loc);
			}
	        Equal(Bool(P), x@Identifier())-> {
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					translateEqualsTRUE(`x, loc),
					translate(`P),
					loc);
			}
			/**
	        * RULE ER8: 	x = f(y)  
	        *	  			y↦x ∈ f
	        */
			Equal(FunImage(f, y), x) -> {
				return translateIn(
					ff.makeBinaryExpression(Formula.MAPSTO, `y, `x, loc), `f, loc);
			}
			Equal(x, FunImage(f, y)) -> {
				return translateIn(
					ff.makeBinaryExpression(Formula.MAPSTO, `y, `x, loc), `f, loc);
			}
	        /**
	        * RULE ER9: 	s = t
	        *	  			∀x·x∈s ⇔ x∈t
	        */
			Equal(s, t) -> {
				final boolean isSetEquality = `s.getType() instanceof PowerSetType;
				if (expandSetEquality && isSetEquality) {
				    return translateSetEq(`s, `t, loc);
				}
				if (GoalChecker.isInGoal(pred)) {
					return pred;
				}
				if (isSetEquality) {
				    return translateSetEq(`s, `t, loc);
				}
			}		 
	        /**
	        * RULE ER10: 	n = card(s)  
	        *	  			0≤n ∧ ∃f·f ∈ s'⤖1‥n'
	        */
			Equal(n@Identifier(), Card(s)) -> {
				return translateEqualsCard(`n, `s, loc);
			}
			Equal(Card(s), n@Identifier()) -> {
				return translateEqualsCard(`n, `s, loc);
			}
	        /**
	        * RULE ER11: 	n = min(s)  
	        *	  			n∈s ∧ n≤min(s)
	        */
   			Equal(n@Identifier(), min@Min(s)) -> {
   				return translateEqualsMinMax(`n, `min, `s, loc);
   			}
   			Equal(min@Min(s), n@Identifier()) -> {
   				return translateEqualsMinMax(`n, `min, `s, loc);
			}
	        /**
	        * RULE ER12: 	n = max(s)  
	        *	  			n∈s ∧ max(s)≤n
	        */
			Equal(n@Identifier(), max@Max(s)) -> {
   				return translateEqualsMinMax(`n, `max, `s, loc);
			}
			Equal(max@Max(s), n@Identifier()) -> {
   				return translateEqualsMinMax(`n, `max, `s, loc);
			}
		}
		/**
		 * RULE ER13: 	el(bop(s)) = er  
		 *	  			c∀ el(bop(s)∗) = er
		 */
		Predicate newPred = Reorganizer.reorganize((RelationalPredicate)pred, ff);
		
		if (newPred != pred)
			return translate(newPred);
		else
			return super.translate(pred);
	}
}