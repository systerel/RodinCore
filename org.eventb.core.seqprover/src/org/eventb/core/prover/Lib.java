package org.eventb.core.prover;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public final class Lib {

	public final static FormulaFactory ff = FormulaFactory.getDefault();
	public final static Predicate True = ff.makeLiteralPredicate(Formula.BTRUE,null);
	public final static Predicate False = ff.makeLiteralPredicate(Formula.BFALSE,null);
	
	
	public static Set<Predicate> singHyp(Predicate h){
		return Collections.singleton(h);
	}
	
	public static boolean isTrue(Predicate P){
		if (P instanceof LiteralPredicate && P.getTag() == Formula.BTRUE) 
			return true;
		return false;
	}
	
	public static boolean isFalse(Predicate P){
		if (P instanceof LiteralPredicate && P.getTag() == Formula.BFALSE) 
			return true;
		return false;
	}
	
	public static boolean isEmptySet(Expression e){
		if (e instanceof AtomicExpression && e.getTag() == Formula.EMPTYSET) 
			return true;
		return false;
	}
	
	public static boolean isUnivQuant(Predicate P){
		if (P instanceof QuantifiedPredicate && P.getTag() == Formula.FORALL) 
			return true;
		return false;
	}
	
	public static boolean isDisj(Predicate P){
		if (P instanceof AssociativePredicate && P.getTag() == Formula.LOR) 
			return true;
		return false;
	}
	
	public static boolean isNeg(Predicate P){
		if (P instanceof UnaryPredicate && P.getTag() == Formula.NOT) 
			return true;
		return false;
	}
	
	public static Predicate negPred(Predicate P){
		if (! isNeg(P)) return null;
		return ((UnaryPredicate)P).getChild();
	}
	
	public static boolean isConj(Predicate P){
		if (P instanceof AssociativePredicate && P.getTag() == Formula.LAND) 
			return true;
		return false;
	}
	
	public static boolean isExQuant(Predicate P){
		if (P instanceof QuantifiedPredicate && P.getTag() == Formula.EXISTS) 
			return true;
		return false;
	}
	
	public static boolean isImp(Predicate P){
		if (P instanceof BinaryPredicate && P.getTag() == Formula.LIMP) 
			return true;
		return false;
	}
	
	public static Predicate impRight(Predicate P){
		if (! isImp(P)) return null;
		return ((BinaryPredicate)P).getRight();
	}
	
	public static Predicate impLeft(Predicate P){
		if (! isImp(P)) return null;
		return ((BinaryPredicate)P).getLeft();
	}
	
	public static Predicate[] conjuncts(Predicate P){
		if (! isConj(P)) return null;
		return ((AssociativePredicate)P).getChildren();
	}
	
	public static Predicate[] disjuncts(Predicate P){
		if (! isDisj(P)) return null;
		return ((AssociativePredicate)P).getChildren();
	}
	
	public static boolean isEq(Predicate P){
		if (P instanceof RelationalPredicate && P.getTag() == Formula.EQUAL) 
			return true;
		return false;
	}
	
	public static Expression eqLeft(Predicate P){
		if (! isEq(P)) return null;
		return ((RelationalPredicate)P).getLeft();
	}
	
	public static Expression eqRight(Predicate P){
		if (! isEq(P)) return null;
		return ((RelationalPredicate)P).getRight();
	}
	
	public static boolean isNotEq(Predicate P){
		if (P instanceof RelationalPredicate && P.getTag() == Formula.NOTEQUAL) 
			return true;
		return false;
	}
	
	public static boolean isInclusion(Predicate P){
		if (P instanceof RelationalPredicate && P.getTag() == Formula.IN) 
			return true;
		return false;
	}
	
	public static boolean isNotInclusion(Predicate P){
		if (P instanceof RelationalPredicate && P.getTag() == Formula.NOTIN) 
			return true;
		return false;
	}
	
	
	public static Expression getElement(Predicate P){
		if (!(P instanceof RelationalPredicate && 
				(P.getTag() == Formula.NOTIN || P.getTag() == Formula.IN)))
		return null;
		return ((RelationalPredicate)P).getLeft();
	}
	
	public static Expression getSet(Predicate P){
		if (!(P instanceof RelationalPredicate && 
				(P.getTag() == Formula.NOTIN || P.getTag() == Formula.IN)))
		return null;
		return ((RelationalPredicate)P).getRight();
	}
	
	public static boolean isSubset(Predicate P){
		if (P instanceof RelationalPredicate && P.getTag() == Formula.SUBSET) 
			return true;
		return false;
	}
	
	public static boolean isNotSubset(Predicate P){
		if (P instanceof RelationalPredicate && P.getTag() == Formula.NOTSUBSET) 
			return true;
		return false;
	}
	
	public static Expression subset(Predicate P){
		if ((! isSubset(P)) || (! isNotSubset(P))) return null;
		return ((RelationalPredicate)P).getLeft();
	}
	
	public static Expression superset(Predicate P){
		if ((! isSubset(P)) || (! isNotSubset(P))) return null;
		return ((RelationalPredicate)P).getRight();
	}
	
	public static Expression notEqRight(Predicate P){
		if (! isNotEq(P)) return null;
		return ((RelationalPredicate)P).getRight();
	}
	
	public static Expression notEqLeft(Predicate P){
		if (! isNotEq(P)) return null;
		return ((RelationalPredicate)P).getLeft();
	}
	
	
	// TODO : Remove this function after type synthesis is implemented.
	private static void typeCheckAfterConstruction(Formula f){
//		ITypeCheckResult tcr = f.typeCheck(te);
//		assert tcr.isSuccess();
//		assert tcr.getInferredEnvironment().isEmpty();
		assert f.isTypeChecked();
		// assert f.isWellFormed();
	}
	
	
	public static Predicate makeGoal(Set<Predicate> Hyps,Predicate goal){
		Predicate result = goal;
		for (Predicate hyp : Hyps){
			result = makeImp(hyp,result);
		}
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeGoal(Predicate[] Hyps,Predicate goal){
		Predicate result = goal;
		for (Predicate hyp : Hyps){
			result = makeImp(hyp,result);
		}
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeNeg(Predicate P){
		Predicate result = ff.makeUnaryPredicate(Formula.NOT,P,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate[] makeNeg(Predicate[] Ps){
		Predicate[] result = new Predicate[Ps.length];
		for (int i=0;i < Ps.length;i++)
			result[i] = makeNeg(Ps[i]);
		return result;
	}
	
	public static Predicate makeConj(Predicate...conjuncts)
	{
		if (conjuncts.length == 0) return True;
		if (conjuncts.length == 1) return conjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LAND,conjuncts,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeDisj(Predicate...disjuncts)
	{
		if (disjuncts.length == 0) return False;
		if (disjuncts.length == 1) return disjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LOR,disjuncts,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeConj(Collection<Predicate> conjuncts)
	{
		Predicate[] conjunctsArray = new Predicate[conjuncts.size()];
		conjuncts.toArray(conjunctsArray);
		return makeConj(conjunctsArray);
	}
	
	public static Predicate makeImp(Predicate left, Predicate right)
	{
		Predicate result = ff.makeBinaryPredicate(Formula.LIMP,left,right,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeEq(Expression left, Expression right)
	{
		Predicate result = ff.makeRelationalPredicate(Formula.EQUAL,left,right,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeNotEq(Expression left, Expression right)
	{
		Predicate result = ff.makeRelationalPredicate(Formula.NOTEQUAL,left,right,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeInclusion(Expression element, Expression set)
	{
		Predicate result = ff.makeRelationalPredicate(Formula.IN,element,set,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate makeNotInclusion(Expression element, Expression set)
	{
		Predicate result = ff.makeRelationalPredicate(Formula.NOTIN,element,set,null);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	
//	// Temporary solution to avoid fail typechecks for unbound idents
//	// used in external reasoner ExF where is is later typechecked
//	public static Predicate makeUncheckedImp(Predicate left, Predicate right)
//	{
//		Predicate result = ff.makeBinaryPredicate(Formula.LIMP,left,right,null);
//		return result;
//	}
//	
//	// Temporary solution to avoid fail typechecks for unbound idents
//	public static Predicate makeUncheckedNeg(Predicate p)
//	{
//		Predicate result = ff.makeUnaryPredicate(Formula.NOT,p,null);
//		return result;
//	}
	
	public static Predicate makeImp(Predicate...imps)
	{
		if (imps.length == 0) return True;
		if (imps.length == 1) return imps[0];
		Predicate result = imps[imps.length - 1];
		for (int i=imps.length-2; i==0 ;i--){
			result = ff.makeBinaryPredicate(Formula.LIMP,imps[i],result,null);
		}
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate instantiateBoundIdents(Predicate P,Expression[] instantiations){
		if (! (P instanceof QuantifiedPredicate)) return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		Predicate result = qP.instantiate(instantiations,ff);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static BoundIdentDecl[] getBoundIdents(Predicate P){
		if (! (P instanceof QuantifiedPredicate)) return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		return qP.getBoundIdentifiers();
	}
	
	// Note returned predicate will have bound variables.
	// Always use in conjunction with makeUnivQuant() or makeExQuant()
	public static Predicate getBoundPredicate(Predicate P){
		if (! (P instanceof QuantifiedPredicate)) return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		return qP.getPredicate();
	}
	
	public static Predicate makeUnivQuant(
			BoundIdentDecl[] boundIdents,
			Predicate boundPred){
		Predicate result = ff.makeQuantifiedPredicate(Formula.FORALL,boundIdents,boundPred,null);
		typeCheckAfterConstruction(result);
		return result;
		
	}
	
	public static Predicate makeExQuant(
			BoundIdentDecl[] boundIdents,
			Predicate boundPred){
		Predicate result = ff.makeQuantifiedPredicate(Formula.EXISTS,boundIdents,boundPred,null);
		typeCheckAfterConstruction(result);
		return result;
		
	}
	
	public static Predicate WD(Formula f){
		Predicate result = f.getWDPredicate(ff);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Predicate WD(Collection<? extends Formula> formulae){
		Set<Predicate> WD = new HashSet<Predicate>(formulae.size());
		for (Formula formula : formulae){
			WD.add(WD(formula));
		}	
		return makeConj(WD);
	}
	
	public static Predicate WD(Formula[] formulae){
		Set<Predicate> WD = new HashSet<Predicate>(formulae.length);
		for (Formula formula : formulae){
			if (formula != null) WD.add(WD(formula));
		}	
		return makeConj(WD);
	}

	public static Expression parseExpression(String str){
		IParseResult plr = ff.parseExpression(str);
		if (plr.isSuccess()) return plr.getParsedExpression();
		else return null;
	}
	
	public static Type parseType(String str){
		IParseResult plr = ff.parseType(str);
		if (plr.isSuccess()) return plr.getParsedType();
		else return null;
	}
	
	public static Expression typeToExpression(Type type){
		Expression result = type.toExpression(ff);
		typeCheckAfterConstruction(result);
		return result;
	}
	
	public static Assignment parseAssignment(String str){
		IParseResult plr = ff.parseAssignment(str);
		if (plr.isSuccess()) return plr.getParsedAssignment();
		else return null;
	}
	
	public static Predicate parsePredicate(String str){
		IParseResult plr = ff.parsePredicate(str);
		if (plr.isSuccess()) return plr.getParsedPredicate();
		// else System.out.println(str+" : "+plr.getProblems());
		return null;
	}
	
	public static Predicate applyDeterministicAssignment(BecomesEqualTo a, Predicate p){
		if (! (a instanceof BecomesEqualTo)) return null;
		assert (a instanceof BecomesEqualTo);
		return p.applyAssignment((BecomesEqualTo) a,ff);
	}
	
	public static Predicate rewrite(Predicate P, FreeIdentifier from, Expression to){
		if (! Arrays.asList(P.getFreeIdentifiers()).contains(from)) return P;
		Map<FreeIdentifier,Expression> subst = new HashMap<FreeIdentifier,Expression>();
		subst.put(from,to);
		return P.substituteFreeIdents(subst,ff);
	}
	
	
	public static boolean isWellTyped(Formula f, ITypeEnvironment t) {
		ITypeCheckResult tcr = f.typeCheck(t);
		// new free variables introduced
		if (tcr.isSuccess()) {
			return tcr.getInferredEnvironment().isEmpty();
		}
		return false;
	}
	
	public static boolean isWellTypedInstantiation(Expression e, Type expT,ITypeEnvironment te) {
		ITypeCheckResult tcr = e.typeCheck(te,expT);
		// new free variables introduced
		if (tcr.isSuccess()) {
			return tcr.getInferredEnvironment().isEmpty();
		}
		return false;
	}
	
	public static ITypeEnvironment typeCheck(Formula f) {
		ITypeCheckResult tcr = f.typeCheck(ff.makeTypeEnvironment());
		if (! tcr.isSuccess()) return null;
		return tcr.getInferredEnvironment();
	}
	
	public static ITypeEnvironment typeCheck(Formula f,ITypeEnvironment initialTypeEnvironment) {
		ITypeCheckResult tcr = f.typeCheck(initialTypeEnvironment);
		if (! tcr.isSuccess()) return null;
		if (tcr.getInferredEnvironment().isEmpty()) return tcr.getInitialTypeEnvironment();
		ITypeEnvironment result = initialTypeEnvironment.clone();
		result.addAll(tcr.getInferredEnvironment());
		return result;
	}
	
	public static ITypeEnvironment typeCheck(Formula... formulae) {
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (Formula f:formulae)
		{
			typeEnvironment = typeCheck(f,typeEnvironment);
			if (typeEnvironment == null) return null;
		}
		return typeEnvironment;
	}
	
	public static IProverSequent makeSequent(ITypeEnvironment typeEnvironment,Set<Hypothesis> hyps,Predicate goal){
		return new SimpleProverSequent(typeEnvironment,hyps,goal);
	}
	
	public static boolean identical(IProverSequent S1,IProverSequent S2){
		if (! S1.goal().equals(S2.goal())) return false;
		if (! S1.selectedHypotheses().equals(S2.selectedHypotheses())) return false;
		if (! S1.hiddenHypotheses().equals(S2.hiddenHypotheses())) return false;
		if (! S1.visibleHypotheses().equals(S2.visibleHypotheses())) return false;
		if (! S1.hypotheses().equals(S2.hypotheses())) return false;
		if (! S1.typeEnvironment().equals(S2.typeEnvironment())) return false;
		return true;
	}
	
	public static boolean sufficient(IProverSequent S1,IProverSequent S2){
		if (! S1.goal().equals(S2.goal())) return false;
		// TODO : take care of selected & hidden hyps eventually
		if (! S1.hypotheses().containsAll(S2.hypotheses())) return false;
		if (! S1.typeEnvironment().containsAll(S2.typeEnvironment())) return false;
		return true;
	}
	
	public static Action deselect(Set<Hypothesis> toDeselect){
		return new Action(ActionType.DESELECT,toDeselect);
	}
	
	public static Action select(Set<Hypothesis> toSelect){
		return new Action(ActionType.SELECT,toSelect);
	}
	
	public static Action hide(Set<Hypothesis> toHide){
		return new Action(ActionType.HIDE,toHide);
	}
	
	public static Action show(Set<Hypothesis> toShow){
		return new Action(ActionType.SHOW,toShow);
	}
	
	public static Action deselect(Hypothesis toDeselect){
		return new Action(ActionType.DESELECT,toDeselect);
	}

	public static ITypeEnvironment makeTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}
	
}