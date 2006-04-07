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
		pred = IdentifierDecomposition.decomposeIdentifiers(pred, ff);
		pred = ExprReorganizer.reorganize(pred, ff);
		pred = new Translator().translate(pred, ff);
		pred = ExpressionPurifier.purify(pred, ff);
		return pred;
	}
	
	%include {Formula.tom}
	@Override
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
	    	Equal(left, right) -> {
	        	return translateEqual (pred,  ff);
	        }		   		      	
	    	In(E, rhs) -> {
	    		return translateIn (`E, `rhs, loc, ff);
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
	    		QuantMapletBuilder mb = new QuantMapletBuilder();
	    		Expression a, b, f;
	    		List<BoundIdentDecl> forallDecls;
	    		LinkedList<BoundIdentDecl> existDecls = new LinkedList<BoundIdentDecl>();
	    		
	    		Type elementType = `S.getType().getBaseType();
	    		
	    		mb.calculate(ff.makePowerSetType(ff.makeProductType(elementType, elementType)), loc, ff);
	    		existDecls.addAll(mb.X());
	    		f = mb.V();
	    		
				mb.calculate(elementType, existDecls.size(), loc, ff);
	    		existDecls.addAll(mb.X());
	    		b = mb.V();
	    		
				mb.calculate(elementType, existDecls.size(), loc, ff);
	    		forallDecls = mb.X();
	    		a = mb.V();
	    		
	    		int identCount = forallDecls.size() + existDecls.size();
	    		
	    		return ff.makeQuantifiedPredicate(
	    			Formula.FORALL,
	    			forallDecls,
	    			ff.makeQuantifiedPredicate(
	    				Formula.EXISTS,
			    		existDecls,
			    		translateIn(
			    			f,
			    			ff.makeBinaryExpression(
			    				Formula.TINJ,
			    				`S.shiftBoundIdentifiers(identCount, ff),
			    				ff.makeBinaryExpression(Formula.UPTO, a, b, loc),
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
		try {
    		return translateIn_E(expr, rhs, loc, ff);
		}
		catch(TranslationException ex) {}
		
		try {
	    	return translateIn_EF(expr, rhs, loc, ff);
		}
		catch(TranslationException ex) {}
		
		try {
    		return translateIn_EF_G(expr, rhs, loc, ff);
		}
		catch(TranslationException ex) {}
				
		try {
    		return translateIn_E_FG(expr, rhs, loc, ff);
		}
		catch(TranslationException ex) {}
		
   		try {
	   		return translateIn_EF_GH(expr, rhs, loc, ff);
	   	}
	   	catch(TranslationException ex) {
	   		throw new AssertionError(ex);
	   	}
	}
	
	protected Predicate translateIn_E(Expression E, Expression right, SourceLocation loc, FormulaFactory ff) 
		throws TranslationException {
	
		QuantMapletBuilder mb = new QuantMapletBuilder();

		%match (Expression right) {
			Pow(child) -> {
				mb.calculate(E.getType().getBaseType(), 0, loc, ff);
	
	    		return ff.makeQuantifiedPredicate(
					Formula.FORALL,
					mb.X(),
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(mb.V(), E.shiftBoundIdentifiers(mb.offset(), ff), loc, ff),
						translateIn(mb.V(), `child.shiftBoundIdentifiers(mb.offset(), ff),	loc, ff), 
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
				mb.calculate(`S.getType().getBaseType(), loc, ff);
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					mb.X(),
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(mb.V(), `S.shiftBoundIdentifiers(mb.offset(), ff), loc, ff),
						translateIn(E.shiftBoundIdentifiers(mb.offset(), ff), mb.V(), loc, ff),
						loc),
					loc);
			}
			Inter(S) -> {
				mb.calculate(`S.getType().getBaseType(), loc, ff);
				return ff.makeQuantifiedPredicate(
					Formula.FORALL,
					mb.X(),
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(mb.V(), `S.shiftBoundIdentifiers(mb.offset(), ff), loc, ff),
						translateIn(E.shiftBoundIdentifiers(mb.offset(), ff), mb.V(), loc, ff),
						loc),
					loc);
			}
			Pow1(T) -> {
				mb.calculate(E.getType(), 0, loc, ff);
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeUnaryExpression(Formula.POW, `T, loc), loc, ff),
					ff.makeQuantifiedPredicate(
						Formula.EXISTS,
						mb.X(),
						translateIn(mb.V(), E.shiftBoundIdentifiers(mb.offset(), ff), loc, ff),
						loc),
					loc);					
			}
			EmptySet() -> {
				return ff.makeLiteralPredicate(Formula.BFALSE, loc);
			}
			SetExtension(members) -> {
				if(`members.length == 0) {
					return ff.makeLiteralPredicate(Formula.BFALSE, loc);
				}
				else{
					LinkedList<Predicate> predicates = new LinkedList<Predicate>();
					for(Expression member: `members){
						predicates.add(
							ff.makeRelationalPredicate(Formula.EQUAL, E, member, loc));
								
					}
					if(predicates.size() == 1) {
						return translate(predicates.getFirst(), ff);
					}
					else{
						return translate(
							ff.makeAssociativePredicate(Formula.LOR, predicates, loc), ff);
					}
				}
			}
			UpTo(a, b) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					ff.makeRelationalPredicate(
						Formula.GE,
						translate(E, ff), 
						translate(`a, ff),
						loc),
					ff.makeRelationalPredicate(
						Formula.LE,
						translate(E, ff), 
						translate(`b, ff),
						loc),
					loc);
			}
			SetMinus(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, `S, loc, ff),
					ff.makeUnaryPredicate(Formula.NOT, translateIn(E, `T, loc, ff), loc),
					loc);
			}
			BInter(children) -> {
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				for(Expression child: `children) {
					preds.add(translateIn(E, child, loc, ff));
				}
				return ff.makeAssociativePredicate(Formula.LAND, preds, loc);
			}
			BUnion(children) -> {
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				for(Expression child: `children) {
					preds.add(translateIn(E, child, loc, ff));
				}
				return ff.makeAssociativePredicate(Formula.LOR,	preds, loc);
			}
			Rel(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ, 
							ff.makeUnaryExpression(Formula.KDOM, E, loc),
							`S,
							loc),
						ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ, 
							ff.makeUnaryExpression(Formula.KRAN, E, loc),
							`T,
							loc),
						ff),
					loc);
			}
			RelImage(r, w) -> {
				mb.calculate(((ProductType)`r.getType().getBaseType()).getLeft(), 0, loc, ff);
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					mb.X(),
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(mb.V(), `w.shiftBoundIdentifiers(mb.offset(), ff), loc, ff),
						translateIn(
							ff.makeBinaryExpression(
								Formula.MAPSTO, 
								mb.V(), 
								E.shiftBoundIdentifiers(mb.offset(), ff), 
								loc),
							`r.shiftBoundIdentifiers(mb.offset(), ff),
							loc,
							ff),
						loc),
					loc);
			}
			FunImage(f, w) -> {
				mb.calculate(ff.makePowerSetType(E.getType()), loc, ff);
				
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					mb.X(),
					FormulaConstructor.makeLandPredicate(
						ff,
						translateIn(
							ff.makeBinaryExpression(
								Formula.MAPSTO, 
								`w.shiftBoundIdentifiers(mb.offset(), ff), 
								mb.V(),	
								loc),
							 `f.shiftBoundIdentifiers(mb.offset(), ff), 
							 loc, 
							 ff),
						translateIn(E, mb.V(), loc,	ff),
						loc),
					loc);
			}
			Ran(r) -> {
				mb.calculate(((ProductType)`r.getType().getBaseType()).getLeft(), 0, loc, ff);
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					mb.X(),
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							mb.V(), 
							E.shiftBoundIdentifiers(mb.offset(), ff), 
							loc), 
						`r.shiftBoundIdentifiers(mb.offset(), ff), 
						loc, 
						ff),
					loc);
			}
			Dom(r) -> {
				mb.calculate(((ProductType)`r.getType().getBaseType()).getRight(), 0, loc, ff);
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					mb.X(),
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							E.shiftBoundIdentifiers(mb.offset(), ff), 
							mb.V(), loc), 
						`r.shiftBoundIdentifiers(mb.offset(), ff), 
						loc, 
						ff),
					loc);
			}
			Trel(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.REL, `S, `T, loc), loc, ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ,
							`S,
							ff.makeUnaryExpression(Formula.KDOM, E, loc),
							loc),
						ff),
					loc);
			}
			Srel(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.REL, `S, `T, loc), loc, ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ,
							`T,
							ff.makeUnaryExpression(Formula.KRAN, E, loc),
							loc),
						ff),
					loc);
			}
			Strel(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.TREL, `S, `T, loc), loc, ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ,
							`T,
							ff.makeUnaryExpression(Formula.KRAN, E, loc),
							loc),
						ff),
					loc);
			}
			Tbij(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.TSUR, `S, `T, loc), loc, ff),
					funcInv(E, ff),
					loc);
			}
			Tsur(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.TFUN, `S, `T, loc), loc, ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ,
							`T,
							ff.makeUnaryExpression(Formula.KRAN, E, loc),
							loc),
						ff),
					loc);			
			}
			Psur(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.PFUN, `S, `T, loc), loc, ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ,
							`T,
							ff.makeUnaryExpression(Formula.KRAN, E, loc),
							loc),
						ff),
					loc);			
			}
			Tinj(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.TFUN, `S, `T, loc), loc, ff),
					funcInv(E, ff),
					loc);
			}			
			Pinj(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.PFUN, `S, `T, loc), loc, ff),
					funcInv(E, ff),
					loc);		
			}	
			Tfun(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.PFUN, `S, `T, loc), loc, ff),
					translate(
						ff.makeRelationalPredicate(
							Formula.SUBSETEQ,
							`S,
							ff.makeUnaryExpression(Formula.KDOM, E, loc),
							loc),
						ff),
					loc);			
			}
			Pfun(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, ff.makeBinaryExpression(Formula.REL, `S, `T, loc), loc, ff),
					func(E, ff),
					loc);	
			}	
			BoundIdentifier(_) | FreeIdentifier(_) -> {
				if(GoalChecker.isMapletExpression(E, ff))
					return ff.makeRelationalPredicate(Formula.IN, E, right, loc);
				else {
					mb.calculate(E.getType(), loc, ff);
					return ff.makeQuantifiedPredicate(
						Formula.EXISTS,
						mb.X(),
						FormulaConstructor.makeLandPredicate(
							ff,
							ff.makeRelationalPredicate(
								Formula.IN, 
								mb.V(), 
								right.shiftBoundIdentifiers(mb.offset(), ff), 
								loc),
							ff.makeRelationalPredicate(
								Formula.EQUAL, 
								mb.V(), 
								E.shiftBoundIdentifiers(mb.offset(), ff), 
								loc),
							loc),
						loc);
				}
			}	
			
			P -> {
				throw new TranslationException("No Mapping for: " + E + " in " + right);
	    	}
		}					
	}
	
	protected Predicate translateIn_EF(Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff)
		throws TranslationException {
		Expression E = null, F = null;
		%match(Expression expr) {
			Mapsto(left, right) -> {
				E = `left; F = `right;
			}
		}
		if(E == null) throw new TranslationException();
		
		%match(Expression rhs) {
			Cprod(S, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, `S, loc, ff),
					translateIn(F, `T, loc, ff),
					loc);			
			}
			Ovr(children) -> {
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				Expression maplet = ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc);
				
				for(int i = 0; i < `children.length; i++) {
					LinkedList<Expression> exprs = new LinkedList<Expression>();
					
					for(int j = i + 1; j < `children.length; j++) {
						exprs.add(ff.makeUnaryExpression(Formula.KDOM, `children[j], loc));
					}
					
					if(exprs.size() > 0) {
						Expression sub;

						if(exprs.size() > 1) sub = ff.makeAssociativeExpression(Formula.KUNION, exprs, loc);
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
				
				return ff.makeAssociativePredicate(
					Formula.LOR,
					preds,
					loc);
			}
			RanSub(r, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
					ff.makeUnaryPredicate(Formula.NOT, translateIn(F, `T, loc, ff), loc),
					loc);
			}
			DomSub(S, r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
					ff.makeUnaryPredicate(Formula.NOT, translateIn(E, `S, loc, ff), loc),
					loc);
			}
			RanRes(r, T) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
					translateIn(F, `T, loc, ff),
					loc);
			}
			DomRes(S, r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r,	loc, ff),
					translateIn(E, `S, loc, ff),
					loc);
			}
			Id(S) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(E, `S, loc, ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, E, F, loc), ff),
					loc);
			}
			Fcomp(children) -> {
				QuantMapletBuilder mb = new QuantMapletBuilder();
				LinkedList<BoundIdentDecl> X = new LinkedList<BoundIdentDecl>();
				Expression[] V = new Expression[`children.length + 1];
				LinkedList<Predicate> preds = new LinkedList<Predicate>();
				
				V[0] = E; 
				V[`children.length] = F;
				
				for(int i = 0; i < `children.length; i++) {
					Type type = ((ProductType)`children[i].getType().getBaseType()).getRight();
					mb.calculate(type, X.size(), loc, ff);
					X.addAll(mb.X());
					V[i+1] = mb.V();
				}			
				for(int i = 0; i < `children.length; i++) {
					preds.add(
						translateIn(
							ff.makeBinaryExpression(Formula.MAPSTO, V[i], V[i+1], loc), 
							`children[i].shiftBoundIdentifiers(X.size(), ff),
							loc,
							ff));								
				}				
								
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					X,
					ff.makeAssociativePredicate(
						Formula.LAND,
						preds,
						loc),
					loc);						
			}
			Bcomp(children) -> {
				return translateIn(
					ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc),
					ff.makeAssociativeExpression(Formula.FCOMP, children, loc),
					loc,
					ff);
			}
			Converse(r) -> {
				return translateIn(ff.makeBinaryExpression(Formula.MAPSTO, F, E, loc), `r, loc, ff);
			}
			_ -> {
				throw new AssertionError("No Mapping for: " + expr + " in " + rhs);
	    	}
		}
	}
	
	protected Predicate translateIn_EF_G(Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff)
		throws TranslationException {
		Expression E = null, F = null, G = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), three) -> {
				E = `one; F = `two; G = `three;
			}
		}
		if(E == null) throw new TranslationException();
		
		%match(Expression rhs) {
			Prj1(r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r, loc, ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, G, E, loc), ff),
					loc);
			}
			Prj2(r) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `r, loc, ff),
					translate(ff.makeRelationalPredicate(Formula.EQUAL, G, F, loc), ff),
					loc);
			}
			P -> {
				throw new TranslationException("No Mapping!");
	    	}
		}
	}

	protected Predicate translateIn_E_FG(Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff) 
		throws TranslationException {
		Expression E = null, F = null, G = null;
		%match(Expression expr){
			Mapsto(one, Mapsto(two, three)) -> {
				E = `one; F = `two; G = `three;
			}
		}
		if(E == null) throw new TranslationException();

		%match(Expression rhs){
			Dprod(p, q) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, F, loc), `p, loc, ff),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, G, loc), `q, loc, ff),
					loc);
			}
			_ -> {
				throw new TranslationException("No Mapping!");
	    	}
		}
	}

	protected Predicate translateIn_EF_GH(Expression expr, Expression rhs, SourceLocation loc, FormulaFactory ff)
		throws TranslationException {
		Expression E = null, F = null, G = null, H = null;
		%match(Expression expr){
			Mapsto(Mapsto(one, two), Mapsto(three, four)) -> {
				E = `one; F = `two; G = `three; H = `four;
			}
		}
		if(E == null) throw new TranslationException();

		%match(Expression rhs) {
			Pprod(p, q) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, E, G, loc), `p, loc, ff),
					translateIn(ff.makeBinaryExpression(Formula.MAPSTO, F, H, loc), `q, loc, ff),
					loc);
			}
			P -> {
				throw new TranslationException("No Mapping!");
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
		QuantMapletBuilder mb = new QuantMapletBuilder();
		LinkedList<BoundIdentDecl> X = new LinkedList<BoundIdentDecl>();
		Expression A, B, C;
		
		Type dom = ((ProductType)f.getType().getBaseType()).getLeft();
		Type ran = ((ProductType)f.getType().getBaseType()).getRight();
		
		if(inverse) { Type t = dom; dom = ran; ran = dom; }

		mb.calculate(dom, 0, loc, ff);
		A = mb.getMaplet();
		X.addAll(mb.getIdentDecls());
		
		mb.calculate(ran, X.size(), loc, ff);
		B = mb.getMaplet();
		X.addAll(mb.getIdentDecls());
		

		mb.calculate(ran, X.size(), loc, ff);
		C = mb.getMaplet();
		X.addAll(mb.getIdentDecls());

		return ff.makeQuantifiedPredicate(
			Formula.FORALL,
			X,
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
						f.shiftBoundIdentifiers(X.size(), ff), 
						loc, 
						ff),
					translateIn(
						ff.makeBinaryExpression(
							Formula.MAPSTO, 
							inverse ? C : A, 
							inverse ? A : C, 
							loc),	
						f.shiftBoundIdentifiers(X.size(), ff), 
						loc, 
						ff),
					loc),
				translate(ff.makeRelationalPredicate(Formula.EQUAL, B, C, loc), ff),
				loc),
			loc);	
	}
	
	protected Predicate translateEqual (Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		QuantMapletBuilder mb = new QuantMapletBuilder();
		
		%match(Predicate pred) {
			Equal(Mapsto(x, y), Mapsto(a,b)) -> {
				return FormulaConstructor.makeLandPredicate(
					ff,
					ff.makeRelationalPredicate(Formula.EQUAL, `x, `a, loc),
					ff.makeRelationalPredicate(Formula.EQUAL, `y, `b, loc),
					loc);						
			}
			Equal(n@Identifier(), Card(S)) | Equal(Card(S), n@Identifier())-> {
				Expression bij = ff.makeBinaryExpression(
						Formula.TBIJ,
						`S,
						ff.makeBinaryExpression(
								Formula.UPTO,
								ff.makeIntegerLiteral(BigInteger.ONE, null),
								`n,
								loc),
						loc);
				mb.calculate(bij.getType().getBaseType(), loc, ff);
				
				bij = bij.shiftBoundIdentifiers(mb.offset(), ff);

				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					mb.X(),
					translateIn(mb.V(), bij, loc, ff),
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
					`P,
					loc);
			}
			Equal(FunImage(r, E1), E2) | Equal(E2, FunImage(r, E1)) -> {
				throw new AssertionError("Function application not yet supported");
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
			P -> {
				/*
				throw new AssertionError("not yet supported!: " + pred);*/
				return super.translate(pred, ff);
			}
		}
	}
}