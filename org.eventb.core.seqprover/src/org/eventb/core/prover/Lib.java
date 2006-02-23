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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
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
	
	public static boolean isUnivQuant(Predicate P){
		if (P instanceof QuantifiedPredicate & P.getTag() == Formula.FORALL) 
			return true;
		return false;
	}
	
	public static boolean isDisj(Predicate P){
		if (P instanceof AssociativePredicate & P.getTag() == Formula.LOR) 
			return true;
		return false;
	}
	
	public static boolean isConj(Predicate P){
		if (P instanceof AssociativePredicate & P.getTag() == Formula.LAND) 
			return true;
		return false;
	}
	
	public static boolean isExQuant(Predicate P){
		if (P instanceof QuantifiedPredicate & P.getTag() == Formula.EXISTS) 
			return true;
		return false;
	}
	
	public static boolean isImp(Predicate P){
		if (P instanceof BinaryPredicate & P.getTag() == Formula.LIMP) 
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
		if (P instanceof RelationalPredicate & P.getTag() == Formula.EQUAL) 
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
	
	public static Predicate makeGoal(ITypeEnvironment te,Set<Predicate> Hyps,Predicate goal){
		Predicate result = goal;
		for (Predicate hyp : Hyps){
			result = makeImp(te,hyp,result);
		}
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	public static Predicate makeGoal(ITypeEnvironment te,Predicate[] Hyps,Predicate goal){
		Predicate result = goal;
		for (Predicate hyp : Hyps){
			result = makeImp(te,hyp,result);
		}
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	public static Predicate makeNeg(ITypeEnvironment te,Predicate P){
		Predicate result = ff.makeUnaryPredicate(Formula.NOT,P,null);
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	public static Predicate makeConj(ITypeEnvironment te,Predicate...conjuncts)
	{
		if (conjuncts.length == 0) return True;
		if (conjuncts.length == 1) return conjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LAND,conjuncts,null);
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	public static Predicate makeConj(ITypeEnvironment te,Collection<Predicate> conjuncts)
	{
		Predicate[] conjunctsArray = new Predicate[conjuncts.size()];
		conjuncts.toArray(conjunctsArray);
		return makeConj(te,conjunctsArray);
	}
	
	public static Predicate makeImp(ITypeEnvironment te,Predicate left, Predicate right)
	{
		Predicate result = ff.makeBinaryPredicate(Formula.LIMP,left,right,null);
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	// Temporary solution to avoid fail typechecks for unbound idents
	// used in external reasoner ExF where is is later typechecked
	public static Predicate makeUncheckedImp(Predicate left, Predicate right)
	{
		Predicate result = ff.makeBinaryPredicate(Formula.LIMP,left,right,null);
		return result;
	}
	
	public static Predicate makeImp(ITypeEnvironment te,Predicate...imps)
	{
		if (imps.length == 0) return True;
		if (imps.length == 1) return imps[0];
		Predicate result = imps[imps.length - 1];
		for (int i=imps.length-2; i==0 ;i--){
			result = ff.makeBinaryPredicate(Formula.LIMP,imps[i],result,null);
		}
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	public static Predicate instantiateBoundIdents(ITypeEnvironment te,Predicate P,Expression[] instantiations){
		if (! (P instanceof QuantifiedPredicate)) return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		Predicate result = qP.instantiate(instantiations,ff);
		assert (typeCheck(result,te)!=null);
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
	
	public static Predicate makeUnivQuant(ITypeEnvironment te,
			BoundIdentDecl[] boundIdents,
			Predicate boundPred){
		Predicate result = ff.makeQuantifiedPredicate(Formula.FORALL,boundIdents,boundPred,null);
		assert (typeCheck(result,te)!=null);
		return result;
		
	}
	
	public static Predicate WD(ITypeEnvironment te,Formula f){
		Predicate result = f.getWDPredicate(ff);
		assert (typeCheck(result,te)!=null);
		return result;
	}
	
	public static Predicate WD(ITypeEnvironment te,Collection<? extends Formula> formulae){
		Set<Predicate> WD = new HashSet<Predicate>(formulae.size());
		for (Formula formula : formulae){
			WD.add(WD(te,formula));
		}	
		return makeConj(te,WD);
	}
	
	public static Predicate WD(ITypeEnvironment te,Formula[] formulae){
		Set<Predicate> WD = new HashSet<Predicate>(formulae.length);
		for (Formula formula : formulae){
			if (formula != null) WD.add(WD(te,formula));
		}	
		return makeConj(te,WD);
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
	
//	public static boolean typeCheck(Formula f) {
//		ITypeCheckResult tcr = f.typeCheck(ff.makeTypeEnvironment());
//		return tcr.isSuccess();
//	}
//	
//	public static boolean typeCheck(Formula[] formulae) {
//		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
//		for (Formula f:formulae)
//		{
//			ITypeCheckResult tcr = f.typeCheck(typeEnvironment);
//			if (! tcr.isSuccess()) return false;
//			if (! tcr.getInferredEnvironment().isEmpty()) 
//				typeEnvironment.addAll(tcr.getInferredEnvironment());
//		}
//		return true;
//	}
	
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

// //****************** OLD VERSIONS OF CONSTRUCTORS WITHOUT TYPECHECK **************************
//
//public static Predicate makeGoal(Set<Predicate> Hyps,Predicate goal,ITypeEnvironment typEnv){
//	Predicate result = goal;
//	for (Predicate hyp : Hyps){
//		result = makeImp(hyp,result);
//	}
//	assert (typeCheck(result,typEnv)!=null);
//	return result;
//}
//
//public static Predicate makeGoal(Predicate[] Hyps,Predicate goal){
//	Predicate result = goal;
//	for (Predicate hyp : Hyps){
//		result = makeImp(hyp,result);
//	}
//	return result;
//}
//
//public static Predicate makeNeg(Predicate P){
//	return ff.makeUnaryPredicate(Formula.NOT,P,null);
//}
//
//public static Predicate makeConj(Predicate...conjuncts)
//{
//	if (conjuncts.length == 0) return True;
//	if (conjuncts.length == 1) return conjuncts[0];
//	return ff.makeAssociativePredicate(Formula.LAND,conjuncts,null);
//}
//
//public static Predicate makeConj(Collection<Predicate> conjuncts)
//{
//	Predicate[] conjunctsArray = new Predicate[conjuncts.size()];
//	conjuncts.toArray(conjunctsArray);
//	return makeConj(conjunctsArray);
//}
//
//public static Predicate makeImp(Predicate left, Predicate right)
//{
//	return ff.makeBinaryPredicate(Formula.LIMP,left,right,null);
//}
//
//public static Predicate makeImp(Predicate...imps)
//{
//	if (imps.length == 0) return True;
//	if (imps.length == 1) return imps[0];
//	Predicate result = imps[imps.length - 1];
//	for (int i=imps.length-2; i==0 ;i--){
//		result = ff.makeBinaryPredicate(Formula.LIMP,imps[i],result,null);
//	}
//	return result;
//}
//
//
//public static Predicate instantiateBoundIdents(Predicate P,Expression[] instantiations){
//	if (! (P instanceof QuantifiedPredicate)) return null;
//	QuantifiedPredicate qP = (QuantifiedPredicate) P;
//	return qP.instantiate(instantiations,ff);
