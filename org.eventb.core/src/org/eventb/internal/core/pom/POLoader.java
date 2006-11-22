package org.eventb.internal.core.pom;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.Util;
import org.rodinp.core.RodinDBException;

public final class POLoader {

	private static final FormulaFactory factory = FormulaFactory.getDefault();

	private POLoader() {
		super();
	}

	/**
	 * Returns the sequent associated to the given proof obligation.
	 * <p>
	 * The PO file containing the proof obligation to read should be locked
	 * before running this method, so that the PO doesn't change while reading
	 * it.
	 * </p>
	 * 
	 * @param poSeq
	 *            the proof obligation to read
	 * @return the sequent of the given proof obligation
	 * @throws RodinDBException
	 */
	public static IProverSequent readPO(IPOSequent poSeq) throws RodinDBException {
		final ITypeEnvironment typeEnv = factory.makeTypeEnvironment();
		final Set<Hypothesis> hypotheses = new HashSet<Hypothesis>();
		loadHypotheses(poSeq, hypotheses, typeEnv);
		final Predicate goal = readGoal(poSeq, typeEnv);
		return ProverFactory.makeSequent(typeEnv,hypotheses,goal);
	}

	/**
	 * Loads the hypotheses of the given PO and appends them to the given set of
	 * hypotheses. The given type environment is enriched with the types of the
	 * free identifiers that occur in the loaded hypotheses.
	 * 
	 * @param poSeq
	 *            PO to read
	 * @param hypotheses
	 *            set of hypotheses where to store the loaded hypotheses
	 * @param typeEnv
	 *            type environment to enrich at the same time
	 * @throws RodinDBException
	 */
	private static void loadHypotheses(IPOSequent poSeq,
			Set<Hypothesis> hypotheses, ITypeEnvironment typeEnv)
			throws RodinDBException {

		IPOPredicateSet[] dbHyps = poSeq.getHypotheses();
		if (dbHyps.length == 0) {
			Util.log(null, "No predicate set in PO " + poSeq);
			return;
		}
		if (dbHyps.length != 1) {
			Util.log(null, "More than one predicate set in PO " + poSeq);
		}
		loadPredicateSet(dbHyps[0], hypotheses, typeEnv);
	}
	
	private static void loadPredicateSet(IPOPredicateSet poPredSet,
			Set<Hypothesis> hypotheses, ITypeEnvironment typeEnv)
			throws RodinDBException {

		final IPOPredicateSet parentSet = poPredSet.getParentPredicateSet();
		if (parentSet != null) {
			loadPredicateSet(parentSet, hypotheses, typeEnv);
		}
		for (final IPOIdentifier poIdent: poPredSet.getIdentifiers()) {
			typeEnv.add(poIdent.getIdentifier(factory));
		}
		for (final IPOPredicate poPred : poPredSet.getPredicates()) {
			final Predicate predicate = poPred.getPredicate(factory, typeEnv);
			final Hypothesis hypothesis = new Hypothesis(predicate);
			hypotheses.add(hypothesis);
		}
	}
	
	/**
	 * Reads the goal of the given proof obligation.
	 * 
	 * @param poSeq
	 *            PO to read
	 * @param typeEnv
	 *            type environment to use
	 * @return the goal of the given PO
	 * @throws RodinDBException
	 */
	private static Predicate readGoal(IPOSequent poSeq, ITypeEnvironment typeEnv)
			throws RodinDBException {
		
		IPOPredicate[] dbGoals = poSeq.getGoals();
		if (dbGoals.length == 0) {
			Util.log(null, "No goal for PO " + poSeq);
			return null;
		}
		if (dbGoals.length != 1) {
			Util.log(null, "More than one goal for PO " + poSeq);
		}
		return dbGoals[0].getPredicate(factory, typeEnv);
	}

}
