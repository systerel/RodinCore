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
	    	In(E, rhs) -> {
	    		return translateIn (`E, `rhs, loc, ff);
	    	}
	    	Equal(_, _) -> {
	        	return translateEqual (pred,  ff);
	        }
	        Le(a, Min(S)) | Lt(a, Min(S)) | Gt(a, Max(S)) |  Ge(a, Max(S)) -> {
	        	return FormulaConstructor.makeGreaterThanExtremumPredicate(
	        		ff, 
	        		translate(`a, ff), 
	        		translate(`S, ff), 
	        		pred.getTag(), 
	        		loc);
	        }
	        
	        Le(a, Max(S)) | Lt(a, Max(S)) | Ge(a, Min(S)) | Gt(a, Min(S))-> {
	        	return FormulaConstructor.makeLessThanExtremumPredicate(
	        		ff, 
	        		translate(`a, ff), 
	        		translate(`S, ff), 
	        		pred.getTag(), 
	        		loc);
	        }
	        NotEqual (left, right) -> {
	        	return ff.makeUnaryPredicate(
	        		Formula.NOT, 
	        		translateEqual(
	        			ff.makeRelationalPredicate(Formula.EQUAL, `left, `right, loc), ff),
	        		loc);
	        }		   		      	
	    	SubsetEq(s, t) -> {
	    		return translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc, ff);   				
	    	}
	    	NotSubsetEq(s, t) -> {
	    		return ff.makeUnaryPredicate(
	    			Formula.NOT, 
	    			translateIn (`s, ff.makeUnaryExpression(Formula.POW, `t, loc), loc, ff),
	    			loc);
	    	}
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
	    	NotIn(s, t) -> {
	    		return ff.makeUnaryPredicate(
	    			Formula.NOT, 
	    			translateIn (`s, `t, loc, ff),
	    			loc);
	    	}
	    	Finite(S) -> {
	    		final DecomposedQuant forall = new DecomposedQuant(ff);
	    		final DecomposedQuant exists = new DecomposedQuant(ff);

	    		final Type setElementType = `S.getType().getBaseType();
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
			    				DecomposedQuant.pushThroughAll(`S, ff, forall, exists),
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
		Expression E, Expression right, SourceLocation loc, FormulaFactory ff) {
	
		%match (Expression right) {
			Pow(child) -> {
				final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = forall.addQuantifier(E.getType().getBaseType(), loc);
	
	    		return forall.makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(x, forall.push(E), loc, ff),
						translateIn(x, forall.push(`child), loc, ff), 
						loc),
				loc);
			}
			Natural() -> {
				return ff.makeRelationalPredicate(
					Formula.GE,
					translate(E, ff),
					ff.makeIntegerLiteral(BigInteger.ZERO, loc),
					loc);
			}
			Natural1() -> {
				return ff.makeRelationalPredicate(
					Formula.GT,
					translate(E, ff),
					ff.makeIntegerLiteral(BigInteger.ZERO, loc),
					loc);
			}
			INTEGER() -> {
				return  ff.makeLiteralPredicate(Formula.BTRUE, loc);	
			}
			Cset(is, P, F) -> {
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					`is,
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(`P, ff),
						translate(ff.makeRelationalPredicate(Formula.EQUAL, E,`F, loc), ff),
						loc),
					loc);
			} 
			Qinter(is, P, F) -> {
				return ff.makeQuantifiedPredicate(
					Formula.FORALL,
					`is,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translate(`P, ff),
						translateIn(E, `F, loc,	ff),
						loc),
					loc);
			}
			Qunion(is, P, F) -> {
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					`is,
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(`P, ff),
						translateIn(E, `F, loc, ff),
						loc),
					loc);
			}
			Union(S) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(`S.getType().getBaseType(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(x, exists.push(`S), loc, ff),
						translateIn(exists.push(E), x, loc, ff),
						loc),
					loc);
			}
			Inter(S) -> {
				final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = 
					forall.addQuantifier(`S.getType().getBaseType(), loc);
	
	    		return forall.makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(x, forall.push(`S), loc, ff),
						translateIn(forall.push(E), x, loc, ff),
						loc),
					loc);
			}
			Pow1(T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = exists.addQuantifier(E.getType().getBaseType(), loc);
				
				`T = condQuant.push(`T);
				E = condQuant.push(E);
				
				return condQuant.conditionalQuantify(				
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeUnaryExpression(Formula.POW, `T, loc), 
							loc, 
							ff),
						exists.makeQuantifiedPredicate(
							Formula.EXISTS,
							translateIn(x, exists.push(E), loc, ff),
							loc),
						loc), this);					
			}
			EmptySet() -> {
				return ff.makeLiteralPredicate(Formula.BFALSE, loc);
			}
			SetExtension(members) -> {
				if(`members.length == 0) {
					return ff.makeLiteralPredicate(Formula.BFALSE, loc);
				}
				else if(members.length == 1) {
					return translateEqual(
						ff.makeRelationalPredicate(Formula.EQUAL, E, `members[0], loc),
						ff);
				}
				else{
					final LinkedList<Predicate> predicates = new LinkedList<Predicate>();
					final ConditionalQuant condQuant = new ConditionalQuant(ff);
					E = condQuant.condSubstitute(E);
					E = condQuant.push(E);

					for(Expression member: `members){
						predicates.add(
							ff.makeRelationalPredicate(
								Formula.EQUAL, 
								E, 
								condQuant.push(member), 
								loc));
					}
					return condQuant.conditionalQuantify(
						translate(
							FormulaConstructor.makeLorPredicate(ff, predicates, loc), ff),
						this);
				}
			}
			UpTo(a, b) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				E = condQuant.push(E);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						ff.makeRelationalPredicate(
							Formula.GE,
							translate(E, ff), 
							translate(condQuant.push(`a), ff),
							loc),
						ff.makeRelationalPredicate(
							Formula.LE,
							translate(E, ff), 
							translate(condQuant.push(`b), ff),
							loc),
						loc), this);
			}
			SetMinus(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				E = condQuant.push(E);
				
				return condQuant.conditionalQuantify(				
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(E, condQuant.push(`S), loc, ff),
						ff.makeUnaryPredicate(
							Formula.NOT, 
							translateIn(E, condQuant.push(`T), loc, ff), 
							loc),
						loc), this);
			}
			BInter(children) | BUnion(children) -> {
				final LinkedList<Predicate> preds = new LinkedList<Predicate>();
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				E = condQuant.push(E);
				
				int tag = right.getTag() == Formula.BINTER ? Formula.LAND : Formula.LOR;

				for(Expression child: `children) {
					preds.add(
						translateIn(E, condQuant.push(child), loc, ff));
				}
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeAssociativePredicate(ff, tag, preds, loc), this);
			}
			Rel(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				E = condQuant.push(E);

				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ, 
								ff.makeUnaryExpression(Formula.KDOM, E, loc),
								condQuant.push(`S),
								loc),
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ, 
								ff.makeUnaryExpression(Formula.KRAN, E, loc),
								condQuant.push(`T),
								loc),
							ff),
						loc), this);
			}
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
							ff.makeBinaryExpression(Formula.MAPSTO, x, exists.push(E), loc),
							exists.push(`r),
							loc,
							ff),
						loc),
					loc);
			}
			FunImage(f, w) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						ff.makePowerSetType(E.getType()), loc);
	
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
						translateIn(E, x, loc,	ff),
						loc),
					loc);
			}
			Ran(r) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						((ProductType)`r.getType().getBaseType()).getLeft(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, x, exists.push(E), loc), 
						exists.push(`r), 
						loc, 
						ff),
					loc);
			}
			Dom(r) -> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
				final Expression x = 
					exists.addQuantifier(
						((ProductType)`r.getType().getBaseType()).getRight(), loc);
	
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					translateIn(
						ff.makeBinaryExpression(Formula.MAPSTO, exists.push(E), x, loc), 
						exists.push(`r), 
						loc, 
						ff),
					loc);
			}
			Trel(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				`S = condQuant.condSubstitute(`S);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.REL, `S, `T, loc), 
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`S,
								ff.makeUnaryExpression(Formula.KDOM, E, loc),
								loc),
							ff),
						loc), 
					this);
			}
			Srel(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				`T = condQuant.condSubstitute(`T);

				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.REL, `S, `T, loc), 
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`T,
								ff.makeUnaryExpression(Formula.KRAN, E, loc),
								loc),
							ff),
						loc),
					this);
			}
			Strel(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				`T = condQuant.condSubstitute(`T);

				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
				
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.TREL, `S, `T, loc), 
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`T,
								ff.makeUnaryExpression(Formula.KRAN, E, loc),
								loc),
							ff),
						loc),
					this);
			}
			Tbij(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.TSUR, `S, `T, loc), 
							loc, 
							ff),
					funcInv(E, ff),
					loc),
					this);
			}
			Tsur(S, T) -> {
				final ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				`T = condQuant.condSubstitute(`T);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.TFUN, `S, `T, loc), 
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`T,
								ff.makeUnaryExpression(Formula.KRAN, E, loc),
								loc),
							ff),
						loc),
					this);			
			}
			Psur(S, T) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				`T = condQuant.condSubstitute(`T);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.PFUN, `S, `T, loc), 
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`T,
								ff.makeUnaryExpression(Formula.KRAN, E, loc),
								loc),
							ff),
						loc),
					this);			
			}
			Tinj(S, T) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.TFUN, `S, `T, loc), 
							loc, 
							ff),
						funcInv(E, ff),
						loc),
					this);
			}			
			Pinj(S, T) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.PFUN, `S, `T, loc), 
							loc, 
							ff),
						funcInv(E, ff),
						loc),
					this);		
			}	
			Tfun(S, T) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				`S = condQuant.condSubstitute(`S);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.PFUN, `S, `T, loc), 
							loc, 
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`S,
								ff.makeUnaryExpression(Formula.KDOM, E, loc),
								loc),
							ff),
						loc),
					this);			
			}
			Pfun(S, T) -> {
				ConditionalQuant condQuant = new ConditionalQuant(ff);
				E = condQuant.condSubstitute(E);
				
				E = condQuant.push(E);
				`S = condQuant.push(`S);
				`T = condQuant.push(`T);
								
				return condQuant.conditionalQuantify(
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							E, 
							ff.makeBinaryExpression(Formula.REL, `S, `T, loc), 
							loc, 
							ff),
						func(E, ff),
						loc),
					this);	
			}	
			BoundIdentifier(_) | FreeIdentifier(_) -> {
				if(GoalChecker.isMapletExpression(E))
					return ff.makeRelationalPredicate(Formula.IN, E, right, loc);
				else {
					final DecomposedQuant exists = new DecomposedQuant(ff);
					final Expression x = exists.addQuantifier(E.getType(), loc);
	
	    			return exists.makeQuantifiedPredicate(
						Formula.EXISTS,
						FormulaConstructor.makeLandPredicate(
							ff,
							translateIn(x, exists.push(right), loc,	ff),
							translateEqual(
								ff.makeRelationalPredicate(
									Formula.EQUAL, x, exists.push(E), loc),
								ff),
							loc),
						loc);
				}
			}	
			_ -> {
				return null;
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
			Equal(Mapsto(x, y), Mapsto(a,b)) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translate(ff.makeRelationalPredicate(Formula.EQUAL, `x, `a, loc), ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, `y, `b, loc), ff),
					loc);						
			}
			Equal(n@Identifier(), Card(S)) | Equal(Card(S), n@Identifier())-> {
				final DecomposedQuant exists = new DecomposedQuant(ff);
	
				final Expression bij = ff.makeBinaryExpression(
						Formula.TBIJ,
						`S,
						ff.makeBinaryExpression(
								Formula.UPTO,
								ff.makeIntegerLiteral(BigInteger.ONE, null),
								`n,
								loc),
						loc);

				final Expression x = 
					exists.addQuantifier(bij.getType().getBaseType(), "b", loc);
				
	    		return exists.makeQuantifiedPredicate(
					Formula.EXISTS,
					translateIn(x, exists.push(bij), loc, ff),
					loc);
			}
			Equal(n@Identifier(), Bool(P)) | Equal(Bool(P), n@Identifier())-> {
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					ff.makeRelationalPredicate(
						Formula.EQUAL,
						`n,
						ff.makeAtomicExpression(Formula.TRUE, loc),
						loc),
					translate(`P, ff),
					loc);
			}
			Equal(Bool(P1), Bool(P2)) -> {
				return ff.makeBinaryPredicate(
					Formula.LEQV,
					translate(`P1, ff),
					translate(`P2, ff),
					loc);
			}
			Equal(TRUE(), Bool(P)) | Equal(Bool(P), TRUE()) -> {
				return translate(P, ff);
			}
			Equal(FALSE(), Bool(P)) | Equal(Bool(P), FALSE()) -> {
				return ff.makeUnaryPredicate(Formula.NOT, translate(P, ff), loc);
			}
			Equal(n@Identifier(), Min(S)) | Equal(Min(S), n@Identifier()) -> {
				//  x = min(S) == x ? S ? (?x1. x1 ? S ? x ? x1)
				final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = 
					forall.addQuantifier(ff.makeIntegerType(), loc);
				
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(`n, `S, loc, ff),
					forall.makeQuantifiedPredicate(
						Formula.FORALL,
						ff.makeBinaryPredicate(
							Formula.LIMP,
							translateIn(x, forall.push(`S), loc, ff),
							translate(
								ff.makeRelationalPredicate(Formula.LE, forall.push(`n), x, loc),
								ff),
							loc),
						loc),
					loc);
			}
			Equal(n@Identifier(), Max(S)) | Equal(Max(S), n@Identifier()) -> {
				//  x = max(S) == x ? S ? (?x1. x1 ? S ? x1 ? x)
				final DecomposedQuant forall = new DecomposedQuant(ff);
				final Expression x = 
					forall.addQuantifier(ff.makeIntegerType(), loc);
	
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(`n, `S, loc, ff),
					forall.makeQuantifiedPredicate(
						Formula.FORALL,
						ff.makeBinaryPredicate(
							Formula.LIMP,
							translateIn(x, forall.push(`S), loc, ff),
							translate(
								ff.makeRelationalPredicate(Formula.LE, x, forall.push(`n), loc),
								ff),
							loc),
						loc),
					loc);
			}
			Equal(FunImage(r, E), x) | Equal(x, FunImage(r, E)) -> {
				return translateIn(
					ff.makeBinaryExpression(Formula.MAPSTO, `E, `x, loc), `r, loc, ff);
			}
			Equal(FALSE(), E) | Equal(E, FALSE()) -> {
				return ff.makeUnaryPredicate(
					Formula.NOT,
					ff.makeRelationalPredicate(
						Formula.EQUAL,
						E,
						ff.makeAtomicExpression(Formula.TRUE, loc),
						loc),
					loc);						
			}
			Equal(S, T) -> {
				if(`S.getType().getBaseType() != null) {
					return FormulaConstructor.makeLandPredicate(
						ff,
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`S,
								`T,
								loc),
							ff),
						translate(
							ff.makeRelationalPredicate(
								Formula.SUBSETEQ,
								`T,
								`S,
								loc),
							ff),
						loc);						
				}
			}	
			_ -> {
				return super.translate(pred, ff);
			}
		}
	}
}