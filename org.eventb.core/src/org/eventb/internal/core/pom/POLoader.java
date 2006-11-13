package org.eventb.internal.core.pom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSstatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.basis.ASTLib;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.rodinp.core.RodinDBException;

public final class POLoader {

	private POLoader() {
		super();
	}

	// Run from the builder. Does not lock the pofile
	public static Map<String, IProverSequent> readPOs(final IPOFile poFile) throws RodinDBException {
		final IPOSequent[] poSequents = poFile.getSequents(null);
		Map<String, IProverSequent> result = new HashMap<String, IProverSequent>(poSequents.length);
		ITypeEnvironment globalTypeEnv = ASTLib.makeTypeEnvironment();
		addIdents(poFile.getIdentifiers(), globalTypeEnv);		
		for (IPOSequent poSeq:poSequents){
			String name = poSeq.getName();
			ITypeEnvironment typeEnv = globalTypeEnv.clone();
			addIdents(poSeq.getIdentifiers(),typeEnv);
			Set<Hypothesis> hypotheses = readPredicates(poSeq.getHypothesis(null), typeEnv);
			Predicate goal = 
				poSeq.getGoal(null).getPredicate(FormulaFactory.getDefault(), typeEnv);
			IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
			result.put(name,seq);
		}
		
			return result;			
	}
	
//	public static IProverSequent readPO(IPOFile poFile,String name) throws RodinDBException {
//		ITypeEnvironment typeEnv = Lib.ff.makeTypeEnvironment();
//		addIdents(poFile.getIdentifiers(), typeEnv);
//		IPOSequent poSeq = (IPOSequent) poFile.getInternalElement(IPOSequent.ELEMENT_TYPE,name);
//		if (! poSeq.exists()) return null;
//		addIdents(poSeq.getIdentifiers(),typeEnv);
//		Set<Hypothesis> hypotheses = readPredicates(poSeq.getHypothesis(),typeEnv);
//		Predicate goal = readPredicate(poSeq.getGoal(),typeEnv);
//		IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
//		return seq;
//	}
	
	private static IProverSequent readPO(IPOFile poFile,String name) throws RodinDBException {
		if (! poFile.exists()) return null;
		IPOSequent poSeq = (IPOSequent) poFile.getInternalElement(IPOSequent.ELEMENT_TYPE,name);
		return readPO(poSeq);
	}
	
	// lock the PO file before reading
	public static IProverSequent readPO(IPOSequent poSeq) throws RodinDBException {
		if (! poSeq.exists()) return null;
		IPOFile poFile = (IPOFile) poSeq.getOpenable();
		ITypeEnvironment typeEnv = ASTLib.makeTypeEnvironment();
		addIdents(poFile.getIdentifiers(), typeEnv);
		addIdents(poSeq.getIdentifiers(),typeEnv);
		Set<Hypothesis> hypotheses = readPredicates(poSeq.getHypothesis(null),typeEnv);
		Predicate goal = poSeq.getGoal(null).getPredicate(FormulaFactory.getDefault(), typeEnv);
		IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
		return seq;
	}
	
	
//	public static IProverSequent makeSequent(IPRSequent prSeq) throws RodinDBException{
//		ITypeEnvironment typeEnv = Lib.ff.makeTypeEnvironment();
//		IPRFile prFile = (IPRFile) prSeq.getOpenable();
//		addIdents(prFile.getIdentifiers(), typeEnv);
//		addIdents(prSeq.getIdentifiers(),typeEnv);
//		Set<Hypothesis> hypotheses = readPredicates(prSeq.getHypothesis(),typeEnv);
//		Predicate goal = readPredicate(prSeq.getGoal(),typeEnv);
//		IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
//		return seq;
//	}
	
	// Lock the pofile before calling this method
	@Deprecated
	public static IProverSequent makeSequent(final IPSstatus prSeq) throws RodinDBException{
		IPSFile prFile = (IPSFile) prSeq.getOpenable();
		final IPOFile poFile = prFile.getPOFile();
		IProverSequent readPO = readPO(poFile,prSeq.getName());
		return readPO;
	}
		
	private static Set<Hypothesis> readPredicates(IPOPredicateSet poPredSet, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (IPOPredicate poPred:poPredSet.getPredicates(null)){
			result.add(new Hypothesis(
					poPred.getPredicate(FormulaFactory.getDefault(), typeEnv)));
		}
		if (poPredSet.getParentPredicateSet(null) != null) 
			result.addAll(readPredicates(poPredSet.getParentPredicateSet(null),typeEnv));
		return result;
	}


//	private static Predicate readPredicate(IPOPredicate poPred, ITypeEnvironment typeEnv) throws RodinDBException {
//			Predicate pred =  ASTLib.parsePredicate(poPred.getContents());
//			// System.out.println("Pred : " + poPred.getContents() +" Parsed : "+ pred);
//			assert pred != null;
//			boolean wellTyped = ASTLib.typeCheckClosed(pred,typeEnv);
//			assert wellTyped;
//			return pred;
//	}


	private static void addIdents(IPOIdentifier[] poIdents, ITypeEnvironment typeEnv) throws RodinDBException {
		for (IPOIdentifier poIdent: poIdents){
			String name = poIdent.getName();
			Type type =  poIdent.getType(FormulaFactory.getDefault());
			assert (name!=null && type !=null);
			typeEnv.addName(name,type);
		}
	}
	
}
