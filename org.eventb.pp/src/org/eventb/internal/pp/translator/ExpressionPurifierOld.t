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
public class ExpressionPurifier extends IdentityTranslator {
	private static final int shiftOffset = 100000; //Integer.MAX_VALUE / 2000;
	private List<Predicate> bindings = new LinkedList<Predicate>();
	private List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	
	protected enum ExpressionState { Floating, Maplet, Set, Arithmetic};
	protected ExpressionState exprState;
	
	protected ExpressionPurifier(List<BoundIdentDecl> alreadyBound){
		identDecls.addAll(alreadyBound);
	}
	
	public static Predicate purify(Predicate P, FormulaFactory ff) {
		SourceLocation loc = P.getSourceLocation();
		ExpressionPurifier purifier = 
			new ExpressionPurifier(new LinkedList<BoundIdentDecl>());
/*
		Predicate purifiedP = purifier.translate(P);
		
		if(purifier.bindings.size() == 0) {
			return purifiedP;
		}
		else {
			LinkedList<Predicate> purifiedBindings = new LinkedList<Predicate>();

			while(purifier.bindings.size() > 0) { 
				Predicate act = purifier.bindings.remove(0);
				purifiedBindings.add(purifier.translate(act, ff));
			}
			
			return ff.makeQuantifiedPredicate(
				Formula.FORALL,
				purifier.identDecls,
				ff.makeBinaryPredicate(
						Formula.LIMP,
						FormulaConstructor.makeLandPredicate(ff, purifiedBindings, loc),
						purifiedP,
						loc),
				loc;
		}
	*/	
		if(!(P instanceof QuantifiedPredicate)) {
			P = ff.makeQuantifiedPredicate(Formula.FORALL, new BoundIdentDecl[0], P,	loc);
		}
		QuantifiedPredicate result = (QuantifiedPredicate)purifier.translate(P, ff);
		if(result.getBoundIdentifiers().length == 0) 
			return result.getPredicate();
		else
			return result;		
	}
	
	protected BoundIdentifier addBoundIdentifier(
		Type type, SourceLocation loc, FormulaFactory ff) {
		identDecls.add(
			ff.makeBoundIdentDecl(
				type.getBaseType() == null ? "x" : "X",
				loc,
				type));

		return ff.makeBoundIdentifier(identDecls.size() - 1, loc, type);	
	}
	
	protected BoundIdentifier bindExpression(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		BoundIdentifier ident = addBoundIdentifier(expr.getType(), loc, ff);
		bindings.add(ff.makeRelationalPredicate(Formula.EQUAL, ident, expr, loc));
		return ident;		
	}
	
	%include {Formula.tom}
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
	    	ForAll(is, P) | Exists(is, P)-> {
	    		pred = pred.shiftBoundIdentifiers(shiftOffset, ff);
	    		`P = ((QuantifiedPredicate)pred).getPredicate();
 		    	
		    	ExpressionPurifier purifier = new ExpressionPurifier(Arrays.asList(`is));
				Predicate purifiedP = purifier.translate(`P, ff);
				LinkedList<Predicate> purifiedBindings = new LinkedList<Predicate>();
				
				while(purifier.bindings.size() > 0) { 
					Predicate act = purifier.bindings.remove(0);
					purifiedBindings.add(purifier.translate(act, ff));
				}
				
				Predicate quantPred = null;
				if (pred.getTag() == Formula.FORALL) {
					if(purifiedBindings.size() == 0)
						quantPred = purifiedP;
					else
						quantPred = ff.makeBinaryPredicate(
							Formula.LIMP,
							FormulaConstructor.makeLandPredicate(ff, purifiedBindings, loc),
							purifiedP,
							loc);
				}
				else {
					purifiedBindings.add(purifiedP);
					quantPred = FormulaConstructor.makeLandPredicate(ff, purifiedBindings, loc);
				}
				
				Predicate result = ff.makeQuantifiedPredicate(pred.getTag(), purifier.identDecls, quantPred, loc);
				
				return result.shiftBoundIdentifiers(-shiftOffset, ff);	    		
	    	}
	    	Lt(AE1, AE2) | Le(AE1, AE2) | Gt(AE1, AE2) | Ge(AE1, AE2) -> {
				exprState = ExpressionState.Arithmetic;
				return super.translate(pred, ff);
			}
			In(ME1, SE1) -> {
				exprState = ExpressionState.Maplet;
				Expression purifiedME1 = translate(`ME1, ff);
				
				exprState = ExpressionState.Set;
				Expression purifiedSE1 = translate(`SE1, ff);
				
				if(`ME1 == purifiedME1 && `SE1 == purifiedSE1)
					return pred;
				else
					return ff.makeRelationalPredicate(Formula.IN, purifiedME1, purifiedSE1, loc);
			}	
			Equal(E1, E2) | NotEqual(E1, E2) -> {
				exprState = ExpressionState.Floating;
				Expression purifiedE1 = translate(`E1, ff);
				Expression purifiedE2 = translate(`E2, ff);
				
				if(`E1 == purifiedE1 && `E2 == purifiedE2)
					return pred;
				else
					return ff.makeRelationalPredicate(pred.getTag(), purifiedE1, purifiedE2, loc);
			}	
			Subset(E1, E2) | SubsetEq(E1, E2) | NotSubset(E1, E2) | NotSubsetEq(E1, E2) -> {
				exprState = ExpressionState.Set;
				Expression purifiedE1 = translate(`E1, ff);
				Expression purifiedE2 = translate(`E2, ff);
				
				if(`E1 == purifiedE1 && `E2 == purifiedE2)
					return pred;
				else
					return ff.makeRelationalPredicate(pred.getTag(), purifiedE1, purifiedE2, loc);
			}
			Finite(SE) -> {
				exprState = ExpressionState.Set;
				Expression purifiedSE = translate(`SE, ff);
				
				if(`SE == purifiedSE)
					return pred;
				else
					return ff.makeSimplePredicate(pred.getTag(), purifiedSE, loc);
			}
	   	   	_ -> {
	   	   		//throw new AssertionError("Undefined: " + pred);
	    		return super.translate(pred, ff);
	    	}
	    }
	}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		if(exprState == ExpressionState.Floating) {
			if(expr.getType() instanceof ProductType)
				exprState = ExpressionState.Maplet;
			else if(expr.getType() instanceof PowerSetType)
				exprState = ExpressionState.Set;
			else
				exprState = ExpressionState.Arithmetic;
		}
		switch(exprState) {
			case Maplet: 
				return translateMaplet(expr, ff);
			case Set:
				return translateSet(expr, ff);
			case Arithmetic:
				return translateArithmetic(expr, ff);
			default:
				throw new AssertionError("Unknown state: " + exprState);
		}
	}
	
	protected Expression translateMaplet(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		%match(Expression expr) {
			Mapsto(l, r) -> {
				return ff.makeBinaryExpression(
					Formula.MAPSTO,
					translate(`l, ff),
					translate(`r, ff),
					loc);
			}
			BoundIdentifier(_) | FreeIdentifier(_) | INTEGER() | BOOL() -> { 
				return expr;
			}
			_ -> {
				return bindExpression(expr, ff);
			}			
		} 
	}
	
	protected Expression translateSet(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		%match(Expression expr) {
			Cset(is, P, E) | Qinter(is, P, E) | Qunion(is, P, E) -> {
				`P = `P.shiftBoundIdentifiers(shiftOffset, ff);
				`E = `E.shiftBoundIdentifiers(shiftOffset, ff);
		    	
		    	ExpressionPurifier purifier = new ExpressionPurifier(Arrays.asList(`is));
				Predicate purifiedP = purifier.translate(`P, ff);
				if(expr.getTag() == Formula.CSET)
					purifier.exprState = ExpressionState.Floating;
				else
					purifier.exprState = ExpressionState.Set;
					
				Expression purifiedE = purifier.translate(`E, ff);
				LinkedList<Predicate> purifiedBindings = new LinkedList<Predicate>();
				
				while(purifier.bindings.size() > 0) { 
					Predicate act = purifier.bindings.remove(0);
					purifiedBindings.add(purifier.translate(act, ff));
				}
			
				purifiedBindings.add(purifiedP);
				
				Expression result = ff.makeQuantifiedExpression(
					expr.getTag(), 
					purifier.identDecls, 
					FormulaConstructor.makeLandPredicate(ff, purifiedBindings, loc),
					purifiedE,
					loc,
					QuantifiedExpression.Form.Explicit);
				
				return result.shiftBoundIdentifiers(-shiftOffset, ff);	  
			}
			Pow(_) | Pow1(_) | BUnion(_) | BInter(_) | Bcomp(_) | Fcomp(_) | Ovr(_) | Rel(_, _) | Trel(_,_) | Srel(_, _) | 
			Strel(_, _) | Pfun(_, _) | Tfun(_, _) | Pinj(_, _) | Tinj(_, _) | Psur(_, _) | Tsur(_, _) | Tbij(_, _) |
			SetMinus(_, _) | Cprod(_, _) | Dprod(_, _) | DomRes(_, _) | DomSub(_, _) | RanRes(_, _) | RanSub(_, _) | 
			UpTo(_, _) | FunImage(_, _) | RelImage(_, _)| Dom(_) | Ran(_) | Union(_) | Inter(_) | Prj1(_) | Prj2(_)-> {
				exprState = ExpressionState.Set;
				return super.translate(expr, ff);
			}
			SetExtension(children) -> {
				exprState = ExpressionState.Maplet;
				return super.translate(expr, ff);
			}
			
			BoundIdentifier(_) | FreeIdentifier(_) | INTEGER() | BOOL() | Natural() | Natural1() | EmptySet() -> { 
				return expr;
			}
			_ -> {
				return bindExpression(expr, ff);
			}			
		} 
	}
	
	protected Expression translateArithmetic(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();

		%match(Expression expr) {
			Card(children) -> {
				return bindExpression(expr, ff);
			}
			_ -> {	
				return super.translate(expr, ff);
			}
		}
		
	}
}