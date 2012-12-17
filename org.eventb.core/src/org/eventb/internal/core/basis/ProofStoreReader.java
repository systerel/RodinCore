/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored expression and predicate storage
 *     Systerel - moved used reasoners to proof root
 ******************************************************************************/
package org.eventb.internal.core.basis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.IPRExprRef;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRReasoner;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.eventb.core.IPRStringInput;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.rodinp.core.RodinDBException;

/**
 * @since 1.0
 */
public class ProofStoreReader implements IProofStoreReader {

	private final IPRProof prProof;
	private final FormulaFactory factory;

	private ISealedTypeEnvironment baseTypEnv = null;
	private final Map<String, Predicate> predicates = new HashMap<String, Predicate>();
	private final Map<String, Expression> expressions = new HashMap<String, Expression>();
	private final Map<String, IReasonerDesc> reasoners = new HashMap<String, IReasonerDesc>();

	@Override
	public FormulaFactory getFormulaFactory() {
		return factory;
	}

	public ProofStoreReader(IPRProof prProof, FormulaFactory factory) {
		this.prProof = prProof;
		this.factory = factory;
	}

	/**
	 * @since 3.0 : the returned type environment became immutable
	 */
	@Override
	public ISealedTypeEnvironment getBaseTypeEnv() throws RodinDBException {
		if (baseTypEnv == null) {
			ITypeEnvironmentBuilder buildTypEnv = factory.makeTypeEnvironment();
			for (String set : prProof.getSets()) {
				buildTypEnv.addGivenSet(set);
			}
			for (IPRIdentifier ident : prProof.getIdentifiers()) {
				buildTypEnv.add(ident.getIdentifier(factory));
			}
			baseTypEnv = buildTypEnv.makeSnapshot();
		}
		return baseTypEnv;
	}

	@Override
	public Predicate getPredicate(String ref) throws RodinDBException {
		Predicate pred = predicates.get(ref);
		if (pred == null) {
			getBaseTypeEnv();
			final IPRStoredPred prPred = prProof.getPredicate(ref);
			pred = prPred.getPredicate(factory, baseTypEnv);
			predicates.put(ref, pred);
		}
		return pred;
	}

	@Override
	public Expression getExpression(String ref) throws RodinDBException {
		Expression expr = expressions.get(ref);
		if (expr == null) {
			getBaseTypeEnv();
			final IPRStoredExpr prExpr = prProof.getExpression(ref);
			expr = prExpr.getExpression(factory, baseTypEnv);
			expressions.put(ref, expr);
		}
		return expr;
	}

	/**
	 * @since 2.2
	 */
	@Override
	public IReasonerDesc getReasoner(String ref) throws RodinDBException {
		IReasonerDesc reasoner = reasoners.get(ref);
		if (reasoner == null) {
			final IPRReasoner prReas = prProof.getReasoner(ref);
			if (prReas.exists()) {
				reasoner = prReas.getReasoner();
			} else { // old storage: ref is reasoner id
				reasoner = SequentProver.getReasonerRegistry().getReasonerDesc(
						ref);
			}

			reasoners.put(ref, reasoner);
		}
		return reasoner;
	}

	// TODO : return null in case key is not present instead of throwing an
	// exception
	public static class Bridge implements IReasonerInputReader {

		private final IPRProofRule prProofRule;
		private final IProofStoreReader store;
		private final int confidence;
		private final String displayName;
		private final Predicate goal;
		private final Set<Predicate> neededHyps;
		private final IAntecedent[] antecedents;

		public Bridge(IPRProofRule prProofRule, IProofStoreReader store,
				int confidence, String displayName, Predicate goal,
				Set<Predicate> neededHyps, IAntecedent[] antecedents) {

			this.prProofRule = prProofRule;
			this.store = store;
			this.confidence = confidence;
			this.displayName = displayName;
			this.goal = goal;
			this.neededHyps = neededHyps;
			this.antecedents = antecedents;
		}

		@Override
		public Expression[] getExpressions(String key)
				throws SerializeException {
			try {
				final IPRExprRef prExprRef = prProofRule.getPRExprRef(key);
				return prExprRef.getExpressions(store);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		@Override
		public Predicate[] getPredicates(String key) throws SerializeException {
			try {
				final IPRPredRef prPredRef = prProofRule.getPRPredRef(key);
				return prPredRef.getPredicates(store);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		@Override
		public String getString(String key) throws SerializeException {
			try {
				final IPRStringInput prStringInput = prProofRule
						.getPRStringInput(key);
				return prStringInput.getString();
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		@Override
		public IAntecedent[] getAntecedents() {
			return antecedents;
		}

		@Override
		public int getConfidence() {
			return confidence;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public Predicate getGoal() {
			return goal;
		}

		@Override
		public Set<Predicate> getNeededHyps() {
			return neededHyps;
		}

		/**
		 * @since 2.0
		 */
		@Override
		public FormulaFactory getFormulaFactory() {
			return store.getFormulaFactory();
		}

	}

}
