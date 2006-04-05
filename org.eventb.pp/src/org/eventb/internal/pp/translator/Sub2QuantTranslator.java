package org.eventb.internal.pp.translator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;

public abstract class Sub2QuantTranslator extends IdentityTranslator {
	private static final int shiftOffset = 100000; //Integer.MAX_VALUE / 2000;
	private List<Predicate> bindings = new LinkedList<Predicate>();
	private List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	
	public static Predicate translate(Predicate P, Sub2QuantTranslator translator, FormulaFactory ff) {
		SourceLocation loc = P.getSourceLocation();

		if(!(P instanceof QuantifiedPredicate)) {
			P = ff.makeQuantifiedPredicate(Formula.FORALL, new BoundIdentDecl[0], P,	loc);
		}
		QuantifiedPredicate result = (QuantifiedPredicate)translator.translate(P, ff);
		if(result.getBoundIdentifiers().length == 0) 
			return result.getPredicate();
		else
			return result;		
	}
	
	protected abstract Sub2QuantTranslator create();
	
	private BoundIdentifier addBoundIdentifier(Type type, SourceLocation loc,
			FormulaFactory ff) {
		identDecls.add(ff.makeBoundIdentDecl(
				type.getBaseType() == null ? "x" : "X", loc, type));

		return ff.makeBoundIdentifier(identDecls.size() - 1, loc, type);
	}
		
	protected BoundIdentifier bindExpression(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		BoundIdentifier ident = addBoundIdentifier(expr.getType(), loc, ff);
		bindings.add(ff.makeRelationalPredicate(Formula.EQUAL, ident, expr, loc));
		return ident;		
	}
	
	private void addAlreadyBound(List<BoundIdentDecl> alreadyBound){
		identDecls.addAll(alreadyBound);
	}
	
	protected Predicate identityTranslate(Predicate pred, FormulaFactory ff) {
		return super.translate(pred, ff);
	}

	@Override
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		if(pred instanceof QuantifiedPredicate) {
			QuantifiedPredicate quantP = (QuantifiedPredicate)
				pred.shiftBoundIdentifiers(shiftOffset, ff);
			Predicate P = quantP.getPredicate();
	    		
			Sub2QuantTranslator translator = create();
			translator.addAlreadyBound(Arrays.asList(quantP.getBoundIdentifiers()));
			Predicate translatedP = translator.translate(P, ff);
			LinkedList<Predicate> translatedBindings = new LinkedList<Predicate>();
			
			while(translator.bindings.size() > 0) { 
				Predicate act = translator.bindings.remove(0);
				translatedBindings.add(translator.translate(act, ff));
			}
			
			Predicate quantPred = null;
			if (pred.getTag() == Formula.FORALL) {
				if(translatedBindings.size() == 0)
					quantPred = translatedP;
				else
					quantPred = ff.makeBinaryPredicate(
						Formula.LIMP,
						FormulaConstructor.makeLandPredicate(ff, translatedBindings, loc),
						translatedP,
						loc);
			}
			else {
				translatedBindings.add(translatedP);
				quantPred = FormulaConstructor.makeLandPredicate(ff, translatedBindings, loc);
			}
			
			Predicate result = ff.makeQuantifiedPredicate(
						pred.getTag(), translator.identDecls, quantPred, loc);
			
			return result.shiftBoundIdentifiers(-shiftOffset, ff);	    		
		}
		else 
    		return super.translate(pred, ff);
	}
	
	protected Expression identityTranslate(Expression pred, FormulaFactory ff) {
		return super.translate(pred, ff);
	}
	
	@Override
	protected Expression translate(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		if(expr instanceof QuantifiedExpression) {
			QuantifiedExpression quantExpr = (QuantifiedExpression)
				expr.shiftBoundIdentifiers(shiftOffset, ff);
			
			Predicate P = quantExpr.getPredicate();
			Expression E = quantExpr.getExpression();
					    	
		    Sub2QuantTranslator translator = create();
		    translator.addAlreadyBound(Arrays.asList(quantExpr.getBoundIdentifiers()));
			
			Predicate translatedP = translator.translate(P, ff);
			Expression translatedE = translator.translate(E, ff);
			
			LinkedList<Predicate> translatedBindings = new LinkedList<Predicate>();
			
			while(translator.bindings.size() > 0) { 
				Predicate act = translator.bindings.remove(0);
				translatedBindings.add(translator.translate(act, ff));
			}
		
			translatedBindings.add(translatedP);
			
			Expression result = ff.makeQuantifiedExpression(
				expr.getTag(), 
				translator.identDecls, 
				FormulaConstructor.makeLandPredicate(ff, translatedBindings, loc),
				translatedE,
				loc,
				QuantifiedExpression.Form.Explicit);
			
			return result.shiftBoundIdentifiers(-shiftOffset, ff);	  
		}
		else
			return super.translate(expr, ff);
	}
}
