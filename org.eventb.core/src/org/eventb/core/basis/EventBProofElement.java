/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common implementation for Event-B Proof elements.
 * <p>
 * This implementation is intended to be sub-classed by clients.
 * </p>
 * 
 * @author Farhad Mehta
 * 
 */
public abstract class EventBProofElement extends InternalElement {

	public EventBProofElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, comment, monitor);
	}

	public String getComment(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
	}
	
	public boolean hasComment(IProgressMonitor monitor) throws RodinDBException {
		return hasAttribute(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
	}

	public void setConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE, confidence, monitor);
	}
	
	public int getConfidence(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE, monitor);
	}
	
	public boolean hasConfidence(IProgressMonitor monitor) throws RodinDBException {
		return hasAttribute(EventBAttributes.CONFIDENCE_ATTRIBUTE, monitor);
	}
	
	public void setGoal(Predicate goal, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String ref = store.putPredicate(goal);
		setAttributeValue(EventBAttributes.GOAL_ATTRIBUTE, ref , monitor);
	}
	
	public Predicate getGoal(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException {
		String ref = getAttributeValue(EventBAttributes.GOAL_ATTRIBUTE, monitor);
		return store.getPredicate(ref, monitor);
	}
	
	public boolean hasGoal(IProgressMonitor monitor) throws RodinDBException {
		return hasAttribute(EventBAttributes.GOAL_ATTRIBUTE, monitor);
	}

	public void setHyps(Collection<Predicate> hyps, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		StringBuilder refs = new StringBuilder();
		Boolean notEmpty = false;
		for (Predicate pred : hyps) {
			if (notEmpty) refs.append(";"); 
			refs.append(store.putPredicate(pred));
			notEmpty = true;
		}
		setAttributeValue(EventBAttributes.HYPS_ATTRIBUTE, refs.toString(), monitor);
	}
	
	public Set<Predicate> getHyps(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException {
		String sepRefs = getAttributeValue(EventBAttributes.HYPS_ATTRIBUTE, monitor);
		String[] refs = sepRefs.split(";");
		HashSet<Predicate> hyps = new HashSet<Predicate>(refs.length);
		for(String ref : refs){
			if (ref.length()!=0) hyps.add(store.getPredicate(ref, monitor));
		}
		return hyps;
	}
	
	public boolean hasHyps(IProgressMonitor monitor) throws RodinDBException {
		return hasAttribute(EventBAttributes.HYPS_ATTRIBUTE, monitor);
	}
	
	public FreeIdentifier[] getFreeIdents(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] children = getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
		FreeIdentifier[] freeIdents = new FreeIdentifier[children.length];
		for (int i = 0; i < freeIdents.length; i++) {
			freeIdents[i] = ((IPRIdentifier)children[i]).getIdentifier(factory, monitor);			
		}
		return freeIdents;
	}
	
	public void setFreeIdents(FreeIdentifier[] freeIdents, IProgressMonitor monitor) throws RodinDBException {
		
		for (int i = 0; i < freeIdents.length; i++) {
			IPRIdentifier prIdent = (IPRIdentifier) 
			getInternalElement(IPRIdentifier.ELEMENT_TYPE, freeIdents[i].getName());
			prIdent.create(null, monitor);
			prIdent.setType(freeIdents[i].getType(), monitor);
		}
	}
}
