/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONFIDENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.GOAL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.HYPS_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.INF_HYPS_ATTRIBUTE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common implementation for Event-B Proof elements.
 * <p>
 * This implementation is intended to be sub-classed by clients that contribute
 * new proof elements.
 * </p>
 * 
 * @author Farhad Mehta
 * 
 */
public abstract class EventBProofElement extends InternalElement {

	protected static final String[] NO_STRINGS = new String[0];
	protected static final IProofSkeleton[] NO_CHILDREN = new IProofSkeleton[0];

	public EventBProofElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		if (comment == null || comment.length() == 0) {
			removeAttribute(COMMENT_ATTRIBUTE, monitor);
		} else {
			setAttributeValue(COMMENT_ATTRIBUTE, comment, monitor);
		}
	}

	public String getComment() throws RodinDBException {
		if (hasAttribute(COMMENT_ATTRIBUTE)) {
			return getAttributeValue(COMMENT_ATTRIBUTE);
		}
		return "";
	}

	public void setConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(CONFIDENCE_ATTRIBUTE, confidence, monitor);
	}
	
	public int getConfidence() throws RodinDBException {
		return getAttributeValue(CONFIDENCE_ATTRIBUTE);
	}
	
	public boolean hasConfidence() throws RodinDBException {
		return hasAttribute(CONFIDENCE_ATTRIBUTE);
	}
	
	public void setGoal(Predicate goal, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String ref = store.putPredicate(goal);
		setAttributeValue(GOAL_ATTRIBUTE, ref , monitor);
	}
	
	public Predicate getGoal(IProofStoreReader store) throws RodinDBException {
		String ref = getAttributeValue(GOAL_ATTRIBUTE);
		return store.getPredicate(ref);
	}
	
	public boolean hasGoal() throws RodinDBException {
		return hasAttribute(GOAL_ATTRIBUTE);
	}

	public void setHyps(Collection<Predicate> hyps, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		StringBuilder refs = new StringBuilder();
		String sep = "";
		for (Predicate pred : hyps) {
			refs.append(sep);
			sep = ",";
			refs.append(store.putPredicate(pred));
		}
		setAttributeValue(HYPS_ATTRIBUTE, refs.toString(), monitor);
	}
	
	public Set<Predicate> getHyps(IProofStoreReader store) throws RodinDBException {
		String sepRefs = getAttributeValue(HYPS_ATTRIBUTE);
		String[] refs = sepRefs.split(",");
		HashSet<Predicate> hyps = new HashSet<Predicate>(refs.length);
		for(String ref : refs){
			if (ref.length()!=0) hyps.add(store.getPredicate(ref));
		}
		return hyps;
	}
	
	public void setInfHyps(Collection<Predicate> hyps, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		StringBuilder refs = new StringBuilder();
		String sep = "";
		for (Predicate pred : hyps) {
			refs.append(sep);
			sep = ",";
			refs.append(store.putPredicate(pred));
		}
		setAttributeValue(INF_HYPS_ATTRIBUTE, refs.toString(), monitor);
	}
	
	public Set<Predicate> getInfHyps(IProofStoreReader store) throws RodinDBException {
		String sepRefs = getAttributeValue(INF_HYPS_ATTRIBUTE);
		String[] refs = sepRefs.split(",");
		HashSet<Predicate> hyps = new HashSet<Predicate>(refs.length);
		for(String ref : refs){
			if (ref.length()!=0) hyps.add(store.getPredicate(ref));
		}
		return hyps;
	}
	
	public boolean hasHyps() throws RodinDBException {
		return hasAttribute(HYPS_ATTRIBUTE);
	}
	
	public FreeIdentifier[] getFreeIdents(FormulaFactory factory) throws RodinDBException {
		IPRIdentifier[] children = getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
		FreeIdentifier[] freeIdents = new FreeIdentifier[children.length];
		for (int i = 0; i < freeIdents.length; i++) {
			freeIdents[i] = children[i].getIdentifier(factory);			
		}
		return freeIdents;
	}
	
	public void setFreeIdents(FreeIdentifier[] freeIdents, IProgressMonitor monitor) throws RodinDBException {
		
		for (int i = 0; i < freeIdents.length; i++) {
			IPRIdentifier prIdent = getInternalElement(
					IPRIdentifier.ELEMENT_TYPE, freeIdents[i].getName());
			prIdent.create(null, monitor);
			prIdent.setType(freeIdents[i].getType(), monitor);
		}
	}

	public void setSkeleton(IProofSkeleton skel, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		
		// write out the comment of the root node
		final String comment = skel.getComment();
		setComment(comment, null);
		
		if (skel.getRule() == null) return;
				
		IPRProofRule prRule = getProofRule(
				skel.getRule().generatedBy().getReasonerID());
		prRule.create(null,null);
		
		prRule.setProofRule(skel, store, monitor);
	}

	public IProofSkeleton getSkeleton(IProofStoreReader store) throws RodinDBException {
		final String comment = getComment();

		IPRProofRule[] rules = getProofRules();
		if (rules.length == 0) {
			return new IProofSkeleton() {
				public IProofSkeleton[] getChildNodes() {
					return NO_CHILDREN;
				}
				public String getComment() {
					return comment;
				}
				public IProofRule getRule() {
					return null;
				}
			};
		}
		if (rules.length != 1) {
			Util.log(null, "More than one rule in proof skeleton node " + this);
		}
		return rules[0].getProofSkeleton(store, comment);
	}


	public IPRProofRule getProofRule(String name) {
		return getInternalElement(IPRProofRule.ELEMENT_TYPE, name);
	}

	public IPRProofRule[] getProofRules() throws RodinDBException {
		return getChildrenOfType(IPRProofRule.ELEMENT_TYPE);
	}
}
