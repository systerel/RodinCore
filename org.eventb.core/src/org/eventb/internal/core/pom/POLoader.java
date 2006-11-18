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
import org.eventb.core.IPSStatus;
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
	
	private static void getTypeEnvironment(
			final IPOFile poFile, IPOPredicateSet predSet, ITypeEnvironment env) throws RodinDBException {
		for (IPOIdentifier identifier : predSet.getIdentifiers()) {
			String name = identifier.getElementName();
			Type type =  identifier.getType(FormulaFactory.getDefault());
			assert (name!=null && type !=null);
			env.addName(name,type);
		}
		IPOPredicateSet parentSet = predSet.getParentPredicateSet();
		if (parentSet == null)
			return;
		else
			getTypeEnvironment(poFile, parentSet, env);
	}

	// Run from the builder. Does not lock the pofile
	public static Map<String, IProverSequent> readPOs(final IPOFile poFile) throws RodinDBException {
		final IPOSequent[] poSequents = poFile.getSequents();
		Map<String, IProverSequent> result = new HashMap<String, IProverSequent>(poSequents.length);
		for (IPOSequent poSeq:poSequents){
			String name = poSeq.getElementName();
			ITypeEnvironment typeEnv = ASTLib.makeTypeEnvironment();
			getTypeEnvironment(poFile, poSeq.getHypothesis(), typeEnv);
			Set<Hypothesis> hypotheses = readPredicates(poSeq.getHypothesis(), typeEnv);
			Predicate goal = 
				poSeq.getGoal().getPredicate(FormulaFactory.getDefault(), typeEnv);
			IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
			result.put(name,seq);
		}
		
			return result;			
	}
	
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
		getTypeEnvironment(poFile, poSeq.getHypothesis(), typeEnv);
		Set<Hypothesis> hypotheses = readPredicates(poSeq.getHypothesis(),typeEnv);
		Predicate goal = poSeq.getGoal().getPredicate(FormulaFactory.getDefault(), typeEnv);
		IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
		return seq;
	}
	
		
	// Lock the pofile before calling this method
	@Deprecated
	public static IProverSequent makeSequent(final IPSStatus prSeq) throws RodinDBException{
		IPSFile prFile = (IPSFile) prSeq.getOpenable();
		final IPOFile poFile = prFile.getPOFile();
		IProverSequent readPO = readPO(poFile,prSeq.getElementName());
		return readPO;
	}
		
	private static Set<Hypothesis> readPredicates(IPOPredicateSet poPredSet, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (IPOPredicate poPred:poPredSet.getPredicates()){
			result.add(new Hypothesis(
					poPred.getPredicate(FormulaFactory.getDefault(), typeEnv)));
		}
		if (poPredSet.getParentPredicateSet() != null) 
			result.addAll(readPredicates(poPredSet.getParentPredicateSet(),typeEnv));
		return result;
	}
	
}
