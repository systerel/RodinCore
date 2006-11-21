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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.rodinp.core.RodinDBException;

public final class POLoader {

	private static final FormulaFactory factory = FormulaFactory.getDefault();

	private POLoader() {
		super();
	}

	// Run from the builder. Does not lock the pofile
	public static Map<String, IProverSequent> readPOs(final IPOFile poFile) throws RodinDBException {
		final IPOSequent[] poSequents = poFile.getSequents();
		Map<String, IProverSequent> result = new HashMap<String, IProverSequent>(poSequents.length);
		for (IPOSequent poSeq:poSequents){
			String name = poSeq.getElementName();
			ITypeEnvironment typeEnv = factory.makeTypeEnvironment();
			getTypeEnvironment(poFile, poSeq.getHypothesis(), typeEnv);
			Set<Hypothesis> hypotheses = readPredSet(poSeq.getHypothesis(), typeEnv);
			Predicate goal = 
				poSeq.getGoal().getPredicate(factory, typeEnv);
			IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
			result.put(name,seq);
		}
		
			return result;			
	}
	
	// lock the PO file before reading
	public static IProverSequent readPO(IPOSequent poSeq) throws RodinDBException {
		if (! poSeq.exists()) return null;
		IPOFile poFile = (IPOFile) poSeq.getOpenable();
		ITypeEnvironment typeEnv = factory.makeTypeEnvironment();
		getTypeEnvironment(poFile, poSeq.getHypothesis(), typeEnv);
		Set<Hypothesis> hypotheses = readPredSet(poSeq.getHypothesis(),typeEnv);
		Predicate goal = poSeq.getGoal().getPredicate(factory, typeEnv);
		IProverSequent seq = ProverFactory.makeSequent(typeEnv,hypotheses,goal);
		return seq;
	}

	private static void getTypeEnvironment(
			final IPOFile poFile, IPOPredicateSet predSet, ITypeEnvironment env) throws RodinDBException {
		for (IPOIdentifier identifier : predSet.getIdentifiers()) {
			String name = identifier.getElementName();
			Type type =  identifier.getType(factory);
			assert (name!=null && type !=null);
			env.addName(name,type);
		}
		IPOPredicateSet parentSet = predSet.getParentPredicateSet();
		if (parentSet == null)
			return;
		else
			getTypeEnvironment(poFile, parentSet, env);
	}
	
	private static Set<Hypothesis> readPredSet(IPOPredicateSet poPredSet, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (IPOPredicate poPred:poPredSet.getPredicates()){
			result.add(new Hypothesis(
					poPred.getPredicate(factory, typeEnv)));
		}
		if (poPredSet.getParentPredicateSet() != null) 
			result.addAll(readPredSet(poPredSet.getParentPredicateSet(),typeEnv));
		return result;
	}
	
}
