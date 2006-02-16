/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.protopog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.ITheorem;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class ContextRuleBase {

	private final IPOGContextRule[] rules = new IPOGContextRule[] {
			new IPOGContextRule() {
				// CTX_AXM_WD
				public List<ProofObligation> get(SCContextCache cache) throws RodinDBException {
					ArrayList<ProofObligation> poList = new ArrayList<ProofObligation>(cache.getNewAxioms().length);
					for(IAxiom axiom : cache.getNewAxioms()) {
						Predicate wdPredicate = cache.getPredicate(axiom.getContents()).getWDPredicate(cache.getFactory());
						if(!wdPredicate.equals(cache.BTRUE)) {
							ProofObligation wdObligation = new ProofObligation(
									axiom.getElementName() +  "/WD",
									cache.getHypSetName(axiom.getElementName()),
									new ProofObligation.PForm(wdPredicate),
									"Well-definedness of Axiom"
							);
							wdObligation.sources.put("axiom", axiom.getHandleIdentifier());
							poList.add(wdObligation);
						}
					}
					return poList;
				}
			},
			new IPOGContextRule() {
				// CTX_THM_WD and CTX_THM
				public List<ProofObligation> get(SCContextCache cache) throws RodinDBException {
					ArrayList<ProofObligation> poList = new ArrayList<ProofObligation>(cache.getNewTheorems().length * 2);
					for(ITheorem theorem : cache.getNewTheorems()) {
						Predicate predicate = cache.getPredicate(theorem.getContents());
						Predicate wdPredicate = predicate.getWDPredicate(cache.getFactory());
						if(!wdPredicate.equals(cache.BTRUE)) {
							ProofObligation wdObligation = new ProofObligation(
									theorem.getElementName() + "/WD",
									cache.getHypSetName(theorem.getElementName()),
									new ProofObligation.PForm(wdPredicate),
									"Well-definedness of Theorem"
							);
							wdObligation.sources.put("theorem", theorem.getHandleIdentifier());
							poList.add(wdObligation);
						}
						if(!predicate.equals(cache.BTRUE)) {
							ProofObligation obligation = new ProofObligation(
									theorem.getElementName(),
									cache.getHypSetName(theorem.getElementName()),
									new ProofObligation.PForm(predicate),
									"Truth of Theorem"
							);
							obligation.sources.put("theorem", theorem.getHandleIdentifier());
							poList.add(obligation);
						}
					}
					return poList;
				}
				
			}
	};
	
	public List<IPOGContextRule> getRules() {
		return Arrays.asList(rules);
	}
	
}
