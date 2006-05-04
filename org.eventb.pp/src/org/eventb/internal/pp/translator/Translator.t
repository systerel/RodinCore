/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.math.BigInteger;
import java.util.*;

import org.eventb.core.ast.*;


/**
 * Implements the translator from set-theory to predicate calculus.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class Translator extends IdentityTranslator {
	
	public class TranslationException extends Exception{
		public TranslationException() {	super(); }
		public TranslationException(String message) { super(message); }
		public TranslationException(String message, Throwable cause) { super(message, cause); }
		public TranslationException(Throwable cause) { super(cause); }
	}
	
	public static Predicate reduceToPredCalc(Predicate pred, FormulaFactory ff) {

		Predicate newPred = pred;
		pred = null;
		
		while(newPred != pred) {
			pred = new Translator().translate(newPred, ff);
			newPred = new Reorganizer().translate(pred, ff);
		}
		return pred;		
	}
	
	%include {Formula.tom}
	@Override
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
	    	/**
	    	 *	All ∈ rules are implemented in translateEqual
	    	 */
	    	In(e, rhs) -> {
	    		Predicate result = translateIn (`e, `rhs, loc, ff);
	    		return result != null ? result : super.translate(pred, ff);
	    	}
	    	/**
	    	 *	All = rules are implemented in translateEqual
	    	 */
	    	Equal(_, _) -> {
	        	return translateEqual (pred,  ff);
	        }
	       	/**
	 		 *  RULE CR1: 	a <∣≤ min(s) 
	 		 * 				∀x·x∈s' ⇒ a' <∣≤ x
	 		 */
	 		Le(a, Min(s)) | Lt(a, Min(s)) -> {
	        	
	        	final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = forall.addQuantifier(`a.getType(), loc);

		    	return forall.makeQuantifiedPredicate(
		    		Formula.FORALL,
		    		ff.makeBinaryPredicate(
		    			Formula.LIMP,
		    			translateIn(x, forall.push(`s),	loc, ff),
		    			translate(
			    			ff.makeRelationalPredicate(pred.getTag(), forall.push(`a), x, loc),
		    				ff),
		    			loc),
		    		loc);	   
	        }
	 		/**
	 		 *  RULE CR2:	max(s) <∣≤ a
			 *				∀x·x∈s' ⇒ x <∣≤ a'
	 		 */	   		      	
	        Le(Max(s), a) | Lt(Max(s), a) -> {
	        	
	        	final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = forall.addQuantifier(`a.getType(), loc);

		    	return forall.makeQuantifiedPredicate(
		    		Formula.FORALL,
		    		ff.makeBinaryPredicate(
		    			Formula.LIMP,
		    			translateIn(x, forall.push(`s),	loc, ff),
		    			translate(
			    			ff.makeRelationalPredicate(pred.getTag(), x, forall.push(`a), loc),
		    				ff),
		    			loc),
		    		loc);	   
	        }
	 		/**
	 		 *  RULE CR3:   min(s) <∣≤  a
			 *				∃x·x∈s' ∧ x <∣≤ a'
	 		 */	   		      	
	        Le(Min(s), a) | Lt(Min(s), a) -> {
	        	
	        	final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = exists.addQuantifier(`a.getType(), loc);
		    	
		    	return exists.makeQuantifiedPredicate(
		    		Formula.EXISTS,
		    		FormulaConstructor.makeLandPredicate(
		    			ff,
		    			translateIn (x, exists.push(`s), loc, ff),
		    			translate(
		    				ff.makeRelationalPredicate(pred.getTag(), x, exists.push(`a), loc),
		    				ff),
		    			loc),
		    		loc);
	        }
	        /**
 	 		 *  RULE CR4: 	a <∣≤ max(s) 
	 		 * 				∃x·x∈s' ∧ a' <∣≤ x
	 		 */
	 	    Le(a, Max(s)) | Lt(a, Max(s)) -> {
	        	
	        	final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = exists.addQuantifier(`a.getType(), loc);
		    	
		    	return exists.makeQuantifiedPredicate(
		    		Formula.EXISTS,
		    		FormulaConstructor.makeLandPredicate(
		    			ff,
		    			translateIn (x, exists.push(`s), loc, ff),
		    			translate(
		    				ff.makeRelationalPredicate(pred.getTag(), exists.push(`a), x, loc),
		    				ff),
		    			loc),
		    		loc);
	        }
	        /**
	         * RULE CR5:	a >|≥ b
	         *				b <|≤ a
	         */
	        Ge(a, b) | Gt(a, b) -> {
	        	return translate(
	        		ff.makeRelationalPredicate(
		        		pred.getTag() == Formula.GE ? Formula.LE : Formula.LT,
		        		`b,
		        		`a,
		        		loc),
		        	ff);
	        }
	        /**
	 		*  RULE BR1: 	s ⊆ t
	 		* 				s ∈ ℙ(t)
	 		*/	   		      	
	    	SubsetEq(s, t) -> {
	    		return translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc, ff);   				
	    	}
	        /**
	 		*  RULE BR2: 	s ⊈ t
	 		* 				¬(s ∈ ℙ(t))
	 		*/	   		      	
	    	NotSubsetEq(s, t) -> {
	    		return ff.makeUnaryPredicate(
	    			Formula.NOT, 
	    			translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc, ff),
	    			loc);
	    	}
	        /**
	 		*  RULE BR3:	s ⊂ t
	 		* 				s ∈ ℙ(t) ∧ ¬�(t ∈ ℙ(s))
	 		*/	   		      	
	    	Subset(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
	    			translateIn(`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc, ff),
	    			ff.makeUnaryPredicate(
	    				Formula.NOT,
		    			translateIn (`t, ff.makeUnaryExpression(Formula.POW, `s, loc), loc, ff),
		    			loc),
			    	loc);	    				
	    	}
	        /**
	 		*  RULE BR4: 	s ⊄ t
	 		* 				�¬�(s ∈ ℙ(t)) ∨ t ∈ ℙ(s)
	 		*/	   		      	
	    	NotSubset(s, t) -> {
				return FormulaConstructor.makeLorPredicate(
					ff,
	    			ff.makeUnaryPredicate(
		    			Formula.NOT,
			    		translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc, ff),
			    		loc),
		    		translateIn(`t, ff.makeUnaryExpression(Formula.POW, `s, loc), loc, ff),
			    	loc);	    				
	    	}
	        /**
	 		*  RULE BR4: 	x ≠ y
	 		* 				¬(x = y)
	 		*/	   		      	
	    	NotEqual (x, y) -> {
	        	return ff.makeUnaryPredicate(
	        		Formula.NOT, 
	        		translateEqual(
	        			ff.makeRelationalPredicate(Formula.EQUAL, `x, `y, loc), ff),
	        		loc);
	        }	
	        /**
	        * RULE BR6: 	x ∉ s
	        *	   			�¬(x ∈ s)
	        */
	    	NotIn(x, s) -> {
	    		return ff.makeUnaryPredicate(
	    			Formula.NOT, 
	    			translateIn (`x, `s, loc, ff),
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
			    			loc,
			    			ff),
			    		loc),
			    	loc);
	    	}
	    	_ -> {
	    		return super.translate(pred, ff);
	    	}
	    }
	}
	
	protected Predicate translateIn(Expression e, Expression rhs, SourceLocation loc, FormulaFactory ff) {
		
		Predicate result = translateIn_E(e, rhs, loc, ff);
		if(result != null) return result;

		result = translateIn_EF(e, rhs, loc, ff);
		if(result != null) return result;
		
		result = translateIn_EF_G(e, rhs, loc, ff);
		if(result != null) return result;
		
		result = translateIn_E_FG(e, rhs, loc, ff);
		if(result != null) return result;
		
		result = translateIn_EF_GH(e, rhs, loc, ff);
		return result;
	}
	
	protected Predicate translateIn_E(
		Expression e, Expression right, SourceLocation loc, FormulaFactory ff) {
	
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
	         *	  			∀X_T·X∈e' ⇒ X∈t'	whereas ℙ(T) = type(e)
	         */
			Pow(t) -> {
				final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = forall.addQuantifier(e.getType().getBaseType(), loc);
	
	    		return forall.makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(x, forall.push(e), loc, ff),
						translateIn(x, forall.push(`t), loc, ff), 
						loc),
				loc);
			}
	        /**
	         * RULE IR3: 	e ∈ f
	         *	  			∃x·x=e ∧ x∈f
	         */
			_ -> {

				if(!GoalChecker.isMapletExpression(e)) {
					
					final ConditionalQuant exists = new ConditionalQuant(ff);
					exists.condSubstitute(`e);					
					exists.startPhase2();
					final Expression x = exists.condSubstitute(`e);					

					return exists.conditionalQuantify(
						Formula.EXISTS,
						translateIn(x, exists.push(right), loc,	ff),
						this);
				}
			}
						 
			Identifier() -> {
				return ff.makeRelationalPredicate(Formula.IN, e, right, loc);
			}
			
			/**
	         * RULE IR4: 	e ∈ ℕ 
	         *	  			0 ≤ e
	         */
			Natural() -> {
				return ff.makeRelationalPredicate(
					Formula.LE,
					ff.makeIntegerLiteral(BigInteger.ZERO, loc),
					translate(e, ff),
					loc);
			}
			/**
	         * RULE IR5: 	e ∈ ℕ1 
	         *	  			0 < e
	         */
			Natural1() -> {
				return ff.makeRelationalPredicate(
					Formula.LT,
					ff.makeIntegerLiteral(BigInteger.ZERO, loc),
					translate(e, ff),
					loc);
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
						translate(`P, ff),
						translateEqual(ff.makeRelationalPredicate(Formula.EQUAL, exists.push(e),`f, loc), ff),
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
						translate(`P, ff),
						translateIn(forall.push(e), `f, loc, ff),
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
						translate(`P, ff),
						translateIn(exists.push(e), `f, loc, ff),
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
						translateIn(x, exists.push(`s), loc, ff),
						translateIn(exists.push(e), x, loc, ff),
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
						translateIn(x, forall.push(`s), loc, ff),
						translateIn(forall.push(e), x, loc, ff),
						loc),
					loc);
			}
			/**
	         * RULE IR11: 	e ∈ ∅ 
	         *				e ∈ {}
	         *	  			⊥ 
	         */
			EmptySet() | SetExtension(()) -> {
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
						translateIn(x, exists.push(`w), loc, ff),
						translateIn(
							ff.makeBinaryExpression(Formula.MAPSTO, x, exists.push(e), loc),
							exists.push(`r),
							loc,
							ff),
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
							 loc, 
							 ff),
						translateIn(exists.push(e), x, loc,	ff),
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
						loc, 
						ff),
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
						loc, 
						ff),
					loc);
			}
	        /**
	         * RULE IR16: 	e ∈{a1,...,an} 	
	         *	  			e=a1 ∨ ... ∨ e=an		
	         */
	        SetExtension((a)) -> {
				return translateEqual(
						ff.makeRelationalPredicate(Formula.EQUAL, e, `a, loc), ff);
						
			}
			SetExtension(members) -> {
				final LinkedList<Predicate> predicates = new LinkedList<Predicate>();

				for(Expression member: `members){
					predicates.add(
						ff.makeRelationalPredicate(
							Formula.EQUAL, e, member, loc));
				}
				return translate(
					FormulaConstructor.makeLorPredicate(ff, predicates, loc), ff);
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
						loc, 
						ff),
					exists.makeQuantifiedPredicate(
						Formula.EXISTS,
						translateIn(x, exists.push(e), loc, ff),
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
					ff.makeRelationalPredicate(
						Formula.LE,
						translate(`a, ff),
						translate(e, ff), 
						loc),
					ff.makeRelationalPredicate(
						Formula.LE,
						translate(e, ff), 
						translate(`b, ff),
						loc),
					loc);
			}
	        /**
	         * RULE IR19:	e ∈ s ∖ t 	 
	         *	  			e∈s ∧ ¬(e∈t)
	         */
			SetMinus(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(e, `s, loc, ff),
					ff.makeUnaryPredicate(
						Formula.NOT, 
						translateIn(e, `t, loc, ff), 
						loc),
					loc);
			}
	        /**
	         * RULE IR20:	e ∈ s1 ∩ ... ∩ sn 	 
	         *	  			e∈s1 ∧ ... ∧ e∈sn
	         * RULE IR21:	e ∈ s1 ∪ ... ∪ sn
	         * 				e∈s1 ∨ ... ∨ e∈sn
	         */
			BInter(children) | BUnion(children) -> {
				final LinkedList<Predicate> preds = new LinkedList<Predicate>();
								
				int tag = right.getTag() == Formula.BINTER ? Formula.LAND : Formula.LOR;

				for(Expression child: `children) {
					preds.add(
						translateIn(e, child, loc, ff));
				}
				return FormulaConstructor.makeAssociativePredicate(ff, tag, preds, loc);
			}
	        /**
	         * RULE IR22:	e ∈ s↔t 	 
	         *	  			dom(e)⊆s ∧ ran(e)⊆t
	         */
			Rel(s, t) -> {
				return FormulaConstructor.makeLandPredicate(
						ff,
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ, 
								ff.makeUnaryExpression(Formula.KDOM, e, loc),
								`s,
								loc),
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ, 
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								`t,
								loc),
							ff),
						loc);
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
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`s,
								ff.makeUnaryExpression(Formula.KDOM, e, loc),
								loc),
							ff),
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
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc),
							ff),
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
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc),
							ff),
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
							loc, 
							ff),
					funcInv(e, ff),
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
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc),
							ff),
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
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								loc),
							ff),
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
							loc, 
							ff),
						funcInv(e, ff),
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
							loc, 
							ff),
						funcInv(e, ff),
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
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`s,
								ff.makeUnaryExpression(Formula.KDOM, e, loc),
								loc),
							ff),
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
							loc, 
							ff),
						func(e, ff),
						loc);	
			}	
			_ -> {
				return null;
			}
		}					
	}
	
	private static Expression purifyMaplet(
		Expression expr, DecomposedQuant quant, List<Predicate> bindings, 
		FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		if(GoalChecker.isMapletExpression(expr))
			return quant.push(expr);
		%match(Expression expr) {
			Mapsto(l, r) -> {
				Expression nr = purifyMaplet(`r, quant, bindings, ff);
				Expression nl = purifyMaplet(`l, quant, bindings, ff);

				if(nr == `r && nl == `l) return expr;
				else
					return ff.makeBinaryExpression(Formula.MAPSTO, nl, nr, loc);
			}
			_ -> {
				Expression substitute = quant.addQuantifier(expr.getType(), loc);
				bindings.add(0, 
					ff.makeRelationalPredicate(
						Formula.EQUAL, 
						substitute, 
						quant.push(expr), 
						loc));
				return substitute;
			}
		}
	}
	
	protected Predicate translateIn_EF(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff){
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
					translateIn(e, `s, loc, ff),
					translateIn(f, `t, loc, ff),
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
				final ConditionalQuant condQuant = new ConditionalQuant(ff);

				for(int i = 1; i < `children.length; i++) {
					condQuant.condSubstitute(`children[i]);
				}
				condQuant.startPhase2();
				for(int i = 1; i < `children.length; i++) {
					`children[i] = condQuant.condSubstitute(`children[i]);
				}
				
				expr = condQuant.push(expr);
				for(int i = 0; i < `children.length; i++) {
					`children[i] = condQuant.push(`children[i]);
				}				
								
				for(int i = 0; i < `children.length; i++) {
					LinkedList<Expression> exprs = new LinkedList<Expression>();
					
					for(int j = i + 1; j < `children.length; j++) {
						exprs.add(ff.makeUnaryExpression(Formula.KDOM, `children[j], loc));
					}	
					
					if(exprs.size() > 0) {
						Expression sub;

						if(exprs.size() > 1) sub = 
							ff.makeAssociativeExpression(Formula.BUNION, exprs, loc);
						else sub = exprs.get(0);
		
						preds.add(
							translateIn(
								expr, 
								ff.makeBinaryExpression(Formula.DOMSUB, sub, `children[i], loc),
								loc,
								ff));
					}
					else 
						preds.add(translateIn(expr, `children[i], loc, ff));		
				}
				
				return  condQuant.conditionalQuantify(
					Formula.FORALL,
					ff.makeAssociativePredicate(Formula.LOR, preds, loc),
					this);
			}
	        /**
	         * RULE IR35:	e↦f ∈ r ⩥ t	
	         *	  			e↦f ∈ r ∧ ¬(f∈t)
	         */
			RanSub(r, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc, ff),
					ff.makeUnaryPredicate(
						Formula.NOT, translateIn(f, `T, loc, ff), loc),
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
						ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc, ff),
					ff.makeUnaryPredicate(Formula.NOT, translateIn(e, `S, loc, ff), loc),
					loc);
			}
	        /**
	         * RULE IR37:	e↦f ∈ r ▷ t
	         *	  			e↦f ∈ r ∧ f ∈ t
	         */
			RanRes(r, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc, ff),
					translateIn(f, `T, loc, ff),
					loc);
			}
	        /**
	         * RULE IR38:	e↦f ∈ s ◁ r
	         *	  			e↦f ∈ r ∧ e ∈ s
	         */
			DomRes(S, r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r,	loc, ff),
					translateIn(e, `S, loc, ff),
					loc);
			}
	        /**
	         * RULE IR39:	e↦f ∈ id(s)
	         *	  			e∈s ∧ e=f
	         */
			Id(S) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(e, `S, loc, ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, e, f, loc), ff),
					loc);
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
							loc,
							ff));								
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
					loc,
					ff);
			}
	        /**
	        * RULE IR42:	e ↦ f ∈ r^
	        *	  			f↦e ∈ r
	        */
			Converse(r) -> {
				return translateIn(ff.makeBinaryExpression(Formula.MAPSTO, f, e, loc), `r, loc, ff);
			}
			_ -> {
				return null;
	    	}
		}
	}
	
	protected Predicate translateIn_EF_G(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff) {
		Expression e = null, f = null, g = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), three) -> {
				e = `one; f = `two; g = `three;
			}
		}
		if(e == null) return null;
		
		%match(Expression rhs) {
	        /**
	        * RULE IR43:	(e↦f)↦g ∈ prj1(r)
	        *	  			e↦f∈r ∧ g=e
	        */
			Prj1(r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r, loc, ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, g, e, loc), ff),
					loc);
			}
	        /**
	        * RULE IR44:	(e↦f)↦g ∈ prj2(r)
	        *	  			e↦f∈r ∧ g=f
	        */
			Prj2(r) -> {	
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `r, loc, ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, g, f, loc), ff),
					loc);
			}
			_ -> {
				return null;
	    	}
		}
	}

	protected Predicate translateIn_E_FG(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff) {
		
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
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, f, loc), `p, loc, ff),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, g, loc), `q, loc, ff),
					loc);
			}
			_ -> {
				return null;
	    	}
		}
	}

	protected Predicate translateIn_EF_GH(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff){
		
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
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, e, g, loc), `p, loc, ff),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, f, h, loc), `q, loc, ff),
					loc);
			}
			_ -> {
				return null;
	    	}
		}
	}
	
	protected Predicate func(Expression f, FormulaFactory ff) {
		return func(f, false, ff);
	}
	
	protected Predicate funcInv(Expression f, FormulaFactory ff) {
		return func(f, true, ff);
	}
	
	protected Predicate func(Expression f, boolean inverse, FormulaFactory ff) {
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
						loc, 
						ff),
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							inverse ? c : a, 
							inverse ? a : c, 
							loc),	
						f, 
						loc, 
						ff),
					loc),
				translate(ff.makeRelationalPredicate(Formula.EQUAL, b, c, loc), ff),
				loc),
			loc);	
	}
	
	protected Predicate translateEqual (Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
		%match(Predicate pred) {
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
					translateEqual(ff.makeRelationalPredicate(Formula.EQUAL, `x, `a, loc), ff),
					translateEqual(ff.makeRelationalPredicate(Formula.EQUAL, `y, `b, loc), ff),
					loc);						
			}
			/**
	        * RULE ER3: 	bool(P) = bool(Q)  
	        *	  			P ⇔ Q
	        */
			Equal(Bool(P), Bool(Q)) -> {
				
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					translate(`P, ff),
					translate(`Q, ff),
					loc);
			}
			/**
	        * RULE ER4: 	bool(P) = TRUE  
	        *	  			P
	        */
			Equal(TRUE(), Bool(P)) | Equal(Bool(P), TRUE()) -> {
				return translate(P, ff);
			}
			/**
	        * RULE ER5: 	bool(P) = FALSE  
	        *	  			¬P
	        */
			Equal(FALSE(), Bool(P)) | Equal(Bool(P), FALSE()) -> {
				return ff.makeUnaryPredicate(Formula.NOT, translate(P, ff), loc);
			}
			/**
	        * RULE ER6: 	x = FALSE  
	        *	  			¬(x = TRUE)
	        */
			Equal(FALSE(), x) | Equal(x, FALSE()) -> {
				return ff.makeUnaryPredicate(
					Formula.NOT,
					translateEqual(
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							x,
							ff.makeAtomicExpression(Formula.TRUE, loc),
							loc),
						ff),
					loc);						
			}
			/**
	        * RULE ER7: 	x = bool(P)  
	        *	  			x = TRUE ⇔ P
	        */
	        Equal(x@Identifier(), Bool(P)) | Equal(Bool(P), x@Identifier())-> {
				
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					ff.makeRelationalPredicate(
						Formula.EQUAL,
						`x,
						ff.makeAtomicExpression(Formula.TRUE, loc),
						loc),
					translate(`P, ff),
					loc);
			}
			/**
	        * RULE ER8: 	x = f(y)  
	        *	  			y↦x ∈ f
	        */
			Equal(FunImage(f, y), x) | Equal(x, FunImage(f, y)) -> {
				return translateIn(
					ff.makeBinaryExpression(Formula.MAPSTO, `y, `x, loc), `f, loc, ff);
			}
	        /**
	        * RULE ER9: 	s = t
	        *	  			s ⊆ t ∧ t ⊆ s
	        */
			Equal(s, t) -> {
				if(GoalChecker.isInGoal(pred)) return pred;
				else if(`s.getType() instanceof PowerSetType) {
					return FormulaConstructor.makeLandPredicate(
						ff,
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`s,
								`t,
								loc),
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`t,
								`s,
								loc),
							ff),
						loc);						
				}
			}		 
	        /**
	        * RULE ER10: 	n = card(s)  
	        *	  			∃f·f ∈ s'⤖1‥n'
	        */
			Equal(n@Identifier(), Card(s)) | Equal(Card(s), n@Identifier())-> {
				
				final DecomposedQuant exists = new DecomposedQuant(ff);
	
				final Expression bij = ff.makeBinaryExpression(
						Formula.TBIJ,
						`s,
						ff.makeBinaryExpression(
								Formula.UPTO,
								ff.makeIntegerLiteral(BigInteger.ONE, null),
								`n,
								loc),
						loc);

				final Expression f = 
					exists.addQuantifier(bij.getType().getBaseType(), "f", loc);
				
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					translateIn(f, exists.push(bij), loc, ff),
					loc);
			}
	        /**
	        * RULE ER11: 	n = min(s)  
	        *	  			n∈s ∧ n≤min(s)
	        */
   			Equal(n@Identifier(), min@Min(s)) 
   			| Equal(min@Min(s), n@Identifier()) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(`n, `s, loc, ff),
					translate(
						ff.makeRelationalPredicate(Formula.LE, `n, `min, loc), ff),
					loc);
			}
	        /**
	        * RULE ER12: 	n = max(s)  
	        *	  			n∈s ∧ max(s)≤n
	        */
			Equal(n@Identifier(), max@Max(s)) 
			| Equal(max@Max(s), n@Identifier()) -> {
   				
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(`n, `s, loc, ff),
					translate(
						ff.makeRelationalPredicate(Formula.LE, `max, `n, loc), ff),
					loc);
			}
			_ -> {
				return super.translate(pred, ff);
			}
		}
	}
}