/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.rules;

import java.util.Collection;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.prover.IProofRule;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;


/**
 * Implementation of a proof rule.
 * 
 * @author Laurent Voisin
 */
public abstract class ProofRule implements IProofRule {

	private final String displayName;
	private final String ruleID;
	
	public ProofRule(String displayName,String ruleID) {
		this.displayName = displayName;
		this.ruleID = ruleID;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofRule#getDisplayName()
	 */
	public final String getDisplayName() {
		return displayName;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofRule#getRuleID()
	 */
	public final String getRuleID() {
		return ruleID;
	}
	

	/**
	 * Applies this rule to the given proof sequent.
	 * 
	 * @param sequent
	 *            proof sequent to apply the rule to
	 * @return array of proof sequents produced by this rule.
	 */
	public abstract IProverSequent[] apply(IProverSequent sequent);
	
	public abstract Set<Hypothesis> getNeededHypotheses();
	public abstract Set<FreeIdentifier> getNeededFreeIdents();
}
