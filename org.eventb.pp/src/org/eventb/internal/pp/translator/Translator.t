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
	    		return translateIn (`e, `rhs, loc, ff);
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
	
	protected Predicate translateIn(Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff) {
		Predicate result = translateIn_E(expr, rhs, loc, ff);
		if(result != null) return result;

		result = translateIn_EF(expr, rhs, loc, ff);
		if(result != null) return result;
		
		result = translateIn_EF_G(expr, rhs, loc, ff);
		if(result != null) return result;
		
		result = translateIn_E_FG(expr, rhs, loc, ff);
		if(result != null) return result;
		
		result = translateIn_EF_GH(expr, rhs, loc, ff);
		if(result != null) return result;
		
		throw new AssertionError("No Mapping for: " + expr + " ? " + rhs);		
	}
	
	protected Predicate translateIn_E(
		Expression e, Expression right, SourceLocation loc, FormulaFactory ff) {
	
		%match (Expression right) {
	        /**
	        * RULE IR1: 	e ∈ ℙ(t)
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
	        * RULE IR2: 	e ∈ ℕ 
	        *	  			e ≥ 0
	        */
			Natural() -> {
				return ff.makeRelationalPredicate(
					Formula.GE,
					translate(e, ff),
					ff.makeIntegerLiteral(BigInteger.ZERO, loc),
					loc);
			}
			/**
	        * RULE IR3: 	e ∈ ℕ1 
	        *	  			e > 0
	        */
			Natural1() -> {
				return ff.makeRelationalPredicate(
					Formula.GT,
					translate(e, ff),
					ff.makeIntegerLiteral(BigInteger.ZERO, loc),
					loc);
			}
			/**
	        * RULE IR4: 	e ∈ ℤ 
	        *	  			⊤ 
	        */
			INTEGER() -> {
				return  ff.makeLiteralPredicate(Formula.BTRUE, loc);	
			}
			/**
	        * RULE IR5: 	e ∈ {x·P∣f} 
	        *	  			∃x·P ∧ e'=f 
	        */
			Cset(is, P, f) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff, `is);

	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(`P, ff),
						translate(ff.makeRelationalPredicate(Formula.EQUAL, exists.push(e),`f, loc), ff),
						loc),
					loc);
			} 
			/**
	        * RULE IR6: 	e ∈ ⋂x·P∣f 
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
	        * RULE IR7: 	e ∈ ⋃x·P∣f 
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
	        * RULE IR8: 	e ∈ union(s) 
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
	        * RULE IR9: 	e ∈ inter(s) 
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
	        * RULE IR10: 	e ∈ ∅ 
	        *				e ∈ {}
	        *	  			⊥ 
	        */
			EmptySet() | SetExtension(()) -> {
				return ff.makeLiteralPredicate(Formula.BFALSE, loc);
			}
			/**
	        * RULE IR11: 	e ∈ r[w] 
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
	        * RULE IR12: 	e ∈ f(w) 
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
	        * RULE IR13: 	e ∈ ran(r) 
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
	        * RULE IR14: 	e ∈ dom(r) 
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
	        * RULE IR15: 	e ∈ {a} 
	        *	  			e = a
	        */
	        SetExtension((a)) -> {
				return translateEqual(
						ff.makeRelationalPredicate(Formula.EQUAL, e, `a, loc), ff);
						
			}
			/**
	        * RULE IR16: 	e ∈ f 
	        *	  			∃x·(x∈f' ∧ x=e')
	        */
	        _ -> {
				if(!GoalChecker.isMapletExpression(e)) {
					
					final Decomp2PhaseQuant exists = new Decomp2PhaseQuant(ff);
					final List<Predicate> bindings = new LinkedList<Predicate>();
					
					purifyMaplet(e, exists, new LinkedList<Predicate>(), ff);		
					exists.startPhase2();
					final Expression x = purifyMaplet(e, exists, bindings, ff);
					
					final List<Predicate> transformedBindings = new LinkedList<Predicate>();
					transformedBindings.add(
						translateIn(x, exists.push(right), loc,	ff));			
					for(Predicate pred : bindings) {
						transformedBindings.add(
							translateEqual(pred, ff));
					}
					
	    			return exists.makeQuantifiedPredicate(
						Formula.EXISTS,
						FormulaConstructor.makeLandPredicate(
							ff, transformedBindings, loc),
						loc);
				}
			}	
	        /**
	        * RULE IR17: 	e ∈{a1,...,an} 	
	        *	  			e=a1 ∨ ... ∨ e=an		whereas n > 1
	        */
			SetExtension(members) -> {
				final LinkedList<Predicate> predicates = new LinkedList<Predicate>();
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				e = condQuant.push(e);

				for(Expression member: `members){
					predicates.add(
						ff.makeRelationalPredicate(
							Formula.EQUAL, 
							e, 
							condQuant.push(member), 
							loc));
				}
				return condQuant.conditionalQuantify(
					translate(
						FormulaConstructor.makeLorPredicate(ff, predicates, loc), ff),
					this);
			}
	        /**
	        * RULE IR18:	e ∈ ℙ1(t)
	        *				e ∈ ℙ(t) ∧ (∃X_T·X ∈ e')	whereas ℙ(T) = type(e) 	  			
	        */
			Pow1(t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = exists.addQuantifier(e.getType().getBaseType(), loc);
				
				`t = condQuant.push(`t);
				e = condQuant.push(e);
				
				return condQuant.conditionalQuantify(				
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeUnaryExpression(Formula.POW, `t, loc), 
							loc, 
							ff),
						exists.makeQuantifiedPredicate(
							Formula.EXISTS,
							translateIn(x, exists.push(e), loc, ff),
							loc),
						loc), this);					
			}
	        /**
	        * RULE IR19:	e ∈ a‥b 	 
	        *	  			a ≤ e ∧ e ≤ b
	        */
			UpTo(a, b) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				e = condQuant.push(e);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						ff.makeRelationalPredicate(
							Formula.LE,
							translate(condQuant.push(`a), ff),
							translate(e, ff), 
							loc),
						ff.makeRelationalPredicate(
							Formula.LE,
							translate(e, ff), 
							translate(condQuant.push(`b), ff),
							loc),
						loc), this);
			}
	        /**
	        * RULE IR20:	e ∈ s ∖ t 	 
	        *	  			e∈s ∧ ¬(e∈t)
	        */
			SetMinus(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				e = condQuant.push(e);
				
				return condQuant.conditionalQuantify(				
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(e, condQuant.push(`s), loc, ff),
						ff.makeUnaryPredicate(
							Formula.NOT, 
							translateIn(e, condQuant.push(`t), loc, ff), 
							loc),
						loc), this);
			}
	        /**
	        * RULE IR21:	e ∈ s1 ∩ ... ∩ sn 	 
	        *	  			e∈s1 ∧ ... ∧ e∈sn
	        * RULE IR22:	e ∈ s1 ∪ ... ∪ sn
	        * 				e∈s1 ∨ ... ∨ e∈sn
	        */
			BInter(children) | BUnion(children) -> {
				final LinkedList<Predicate> preds = new LinkedList<Predicate>();
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				e = condQuant.push(e);
				
				int tag = right.getTag() == Formula.BINTER ? Formula.LAND : Formula.LOR;

				for(Expression child: `children) {
					preds.add(
						translateIn(e, condQuant.push(child), loc, ff));
				}
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeAssociativePredicate(ff, tag, preds, loc), this);
			}
	        /**
	        * RULE IR23:	e ∈ s↔t 	 
	        *	  			dom(e)⊆s ∧ ran(e)⊆t
	        */
			Rel(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				e = condQuant.push(e);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ, 
								ff.makeUnaryExpression(Formula.KDOM, e, loc),
								condQuant.push(`s),
								loc),
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ, 
								ff.makeUnaryExpression(Formula.KRAN, e, loc),
								condQuant.push(`t),
								loc),
							ff),
						loc), this);
			}
	        /**
	        * RULE IR24:	e ∈ st 	 
	        *	  			e∈s↔t ∧ s⊆dom(e)
	        */
			Trel(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				`s = condQuant.condSubstitute(`s);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
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
						loc), 
					this);
			}
	        /**
	        * RULE IR25:	e ∈ st	 
	        *	  			e∈s↔t ∧ t⊆ran(e)
	        */
			Srel(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				`t = condQuant.condSubstitute(`t);

				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
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
						loc),
					this);
			}
	        /**
	        * RULE IR26:	e ∈ st 	 
	        *	  			e∈st ∧ t⊆ran(e)
	        */
			Strel(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				`t = condQuant.condSubstitute(`t);

				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
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
						loc),
					this);
			}
	        /**
	        * RULE IR27:	e ∈ s⤖t 	 
	        *	  			e∈s↠t ∧ func(e^−1)
	        */
			Tbij(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.TSUR, `s, `t, loc), 
							loc, 
							ff),
					funcInv(e, ff),
					loc),
					this);
			}
	        /**
	        * RULE IR28:	e ∈ s↠t	 
	        *	  			e∈s→t ∧ t⊆ran(e)
	        */
			Tsur(s, t) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				`t = condQuant.condSubstitute(`t);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
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
						loc),
					this);			
			}
	        /**
	        * RULE IR29:	e ∈ s⤀t 	 
	        *	  			e∈s⇸t ∧ t⊆ran(e)
	        */
			Psur(s, t) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				`t = condQuant.condSubstitute(`t);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
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
						loc),
					this);			
			}
	        /**
	        * RULE IR30:	e ∈ s↣t 	 
	        *	  			e∈s→t ∧ func(e^−1)
	        */
			Tinj(s, t) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.TFUN, `s, `t, loc), 
							loc, 
							ff),
						funcInv(e, ff),
						loc),
					this);
			}			
	        /**
	        * RULE IR31:	e ∈ s⤔t 	 
	        *	  			e∈⇸t ∧ func(e^−1)
	        */
			Pinj(s, t) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.PFUN, `s, `t, loc), 
							loc, 
							ff),
						funcInv(e, ff),
						loc),
					this);		
			}	
	        /**
	        * RULE IR32:	e ∈ s→t 	 
	        *	  			e∈s⇸t ∧ s⊆dom(e)
	        */
			Tfun(s, t) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				`s = condQuant.condSubstitute(`s);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
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
						loc),
					this);			
			}
	        /**
	        * RULE IR33:	e ∈ s⇸t 	 
	        *	  			e∈s↔t ∧ func(e)
	        */
			Pfun(s, t) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				e = condQuant.condSubstitute(e);
				
				e = condQuant.push(e);
				`s = condQuant.push(`s);
				`t = condQuant.push(`t);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							e, 
							ff.makeBinaryExpression(Formula.REL, `s, `t, loc), 
							loc, 
							ff),
						func(e, ff),
						loc),
					this);	
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
		Expression E = null, F = null;
		%match(Expression expr) {
			Mapsto(left, right) -> {
				E = `left; F = `right;
			}
		}
		if(E == null) return null;
		
		%match(Expression rhs) {
			Cprod(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, `S, loc, ff),
					translateIn(F, `T, loc, ff),
					loc);			
			}
			Ovr(children) -> { //E?F?q?r becomes E?F?r ? E?>F?dom(r)?q
			
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				final ConditionalQuant condQuant = new ConditionalQuant(ff);

				Expression maplet = condQuant.condSubstitute(expr);
				for(int i = 1; i < `children.length; i++) {
					`children[i] = condQuant.condSubstitute(`children[i]);
				}
				
				maplet = condQuant.push(maplet);
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
								maplet, 
								ff.makeBinaryExpression(Formula.DOMSUB, sub, `children[i], loc),
								loc,
								ff));
					}
					else 
						preds.add(translateIn(maplet, `children[i], loc, ff));		
				}
				
				return  condQuant.conditionalQuantify(
					ff.makeAssociativePredicate(Formula.LOR, preds, loc),
					this);
			}
			RanSub(r, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				F = condQuant.condSubstitute(F);

				E = condQuant.push(E);
				F = condQuant.push(F);
				`r = condQuant.push(`r);
				`T = condQuant.push(`T);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
						ff.makeUnaryPredicate(Formula.NOT, translateIn(F, `T, loc, ff), loc),
						loc),
					this);
			}
			DomSub(S, r) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);

				E = condQuant.push(E);
				F = condQuant.push(F);
				`r = condQuant.push(`r);
				`S = condQuant.push(`S);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
						ff.makeUnaryPredicate(Formula.NOT, translateIn(E, `S, loc, ff), loc),
						loc),
					this);
			}
			RanRes(r, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				F = condQuant.condSubstitute(F);

				E = condQuant.push(E);
				F = condQuant.push(F);
				`r = condQuant.push(`r);
				`T = condQuant.push(`T);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
						translateIn(F, `T, loc, ff),
						loc),
					this);
			}
			DomRes(S, r) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);

				E = condQuant.push(E);
				F = condQuant.push(F);
				`r = condQuant.push(`r);
				`S = condQuant.push(`S);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
						translateIn(E, `S, loc, ff),
						loc),
					this);
			}
			Id(S) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);

				E = condQuant.push(E);
				F = condQuant.push(F);
				`S = condQuant.push(`S);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(E, `S, loc, ff),
						translate(ff.makeRelationalPredicate(Formula.EQUAL, E, F, loc), ff),
						loc),
					this);
			}
			Fcomp(children) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);

				Expression[] X = new Expression[`children.length + 1];
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				
				for(int i = 1; i < `children.length; i++) {
					Type type = ((ProductType)`children[i].getType().getBaseType()).getLeft();
					X[i] = exists.addQuantifier(type, loc);
				}		
					
				X[0] = exists.push(E);
				X[`children.length] = exists.push(F);
				
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
			Bcomp(children) -> {
				List<Expression> reversedChilds = Arrays.asList(`children);
				Collections.reverse(reversedChilds);
				
				return translateIn(
					expr,
					ff.makeAssociativeExpression(Formula.FCOMP, reversedChilds, loc),
					loc,
					ff);
			}
			Converse(r) -> {
				return translateIn(ff.makeBinaryExpression(Formula.MAPSTO, F, E, loc), `r, loc, ff);
			}
			_ -> {
				return null;
	    	}
		}
	}
	
	protected Predicate translateIn_EF_G(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff) {
		Expression E = null, F = null, G = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), three) -> {
				E = `one; F = `two; G = `three;
			}
		}
		if(E == null) return null;
		
		%match(Expression rhs) {
			Prj1(r) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);

				E = condQuant.push(E);
				F = condQuant.push(F);
				G = condQuant.push(G);
				`r = condQuant.push(`r);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r, loc, ff),
						translate(ff.makeRelationalPredicate(Formula.EQUAL, G, E, loc), ff),
						loc),
					this);
			}
			Prj2(r) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				F = condQuant.condSubstitute(F);

				E = condQuant.push(E);
				F = condQuant.push(F);
				G = condQuant.push(G);
				`r = condQuant.push(`r);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r, loc, ff),
						translate(ff.makeRelationalPredicate(Formula.EQUAL, G, F, loc), ff),
						loc),
					this);
			}
			_ -> {
				return null;
	    	}
		}
	}

	protected Predicate translateIn_E_FG(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff) {
		
		Expression E = null, F = null, G = null;
		%match(Expression expr){
			Mapsto(one, Mapsto(two, three)) -> {
				E = `one; F = `two; G = `three;
			}
		}
		if(E == null) return null;

		%match(Expression rhs){
			Dprod(p, q) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);

				E = condQuant.push(E);
				F = condQuant.push(F);
				G = condQuant.push(G);
				`p = condQuant.push(`p);	
				`q = condQuant.push(`q);
					
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `p, loc, ff),
						translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, G, loc), `q, loc, ff),
						loc),
					this);
			}
			_ -> {
				return null;
	    	}
		}
	}

	protected Predicate translateIn_EF_GH(
		Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff){
		
		Expression E = null, F = null, G = null, H = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), Mapsto(three, four)) -> {
				E = `one; F = `two; G = `three; H = `four;
			}
		}
		if(E == null) return null;

		%match(Expression rhs) {
			Pprod(p, q) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, G, loc), `p, loc, ff),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, F, H, loc), `q, loc, ff),
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

		final Expression A = forall.addQuantifier(dom, loc);
		final Expression B = forall.addQuantifier(ran, loc);
		final Expression C = forall.addQuantifier(ran, loc);
		
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
							inverse ? B : A, 
							inverse ? A : B, 
							loc),	
						f, 
						loc, 
						ff),
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							inverse ? C : A, 
							inverse ? A : C, 
							loc),	
						f, 
						loc, 
						ff),
					loc),
				translate(ff.makeRelationalPredicate(Formula.EQUAL, B, C, loc), ff),
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
			Equal(E, E) -> {
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
	        * RULE ER8: 	y = f(x)  
	        *	  			x↦y ∈ f
	        */
			Equal(FunImage(f, x), y) | Equal(y, FunImage(f, x)) -> {
				return translateIn(
					ff.makeBinaryExpression(Formula.MAPSTO, `x, `y, loc), `f, loc, ff);
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