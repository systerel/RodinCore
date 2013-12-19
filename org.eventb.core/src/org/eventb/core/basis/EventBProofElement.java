/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - serialization of reasoner version through rule name
 *     Systerel - added unselected hyps attribute
 *     Systerel - used nested classes instead of anonymous ones
 *     Systerel - collected used reasoners and moved them to proof root
 *     Systerel - added hidden hyps attribute
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONFIDENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.GOAL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.HIDDEN_HYPS_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.HYPS_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.INF_HYPS_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.MANUAL_PROOF_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.UNSEL_HYPS_ATTRIBUTE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRProofInfoElement;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
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
 * @since 1.0
 */
public abstract class EventBProofElement extends InternalElement implements
		IPRProofInfoElement, IPOStampedElement {

	static class EmptySkeleton implements IProofSkeleton {

		private final String comment;

		public EmptySkeleton(String comment) {
			this.comment = comment;
		}

		@Override
		public IProofSkeleton[] getChildNodes() {
			return NO_CHILDREN;
		}

		@Override
		public String getComment() {
			return comment;
		}

		@Override
		public IProofRule getRule() {
			return null;
		}
	}

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

	@Override
	public void setConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException {
		if (confidence != IConfidence.UNATTEMPTED) {
			setAttributeValue(CONFIDENCE_ATTRIBUTE, confidence, monitor);
		} else {
			removeAttribute(CONFIDENCE_ATTRIBUTE, monitor);
		}
	}
	
	@Override
	public int getConfidence() throws RodinDBException {
		if (!hasConfidence()) return IConfidence.UNATTEMPTED;
		return getAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE);
	}
	
	private boolean hasConfidence() throws RodinDBException {
		return hasAttribute(CONFIDENCE_ATTRIBUTE);
	}
	
	@Override
	public boolean getHasManualProof() throws RodinDBException {
		return isAttributeTrue(MANUAL_PROOF_ATTRIBUTE);
	}
	
	@Override
	public void setHasManualProof(boolean value, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeTrue(MANUAL_PROOF_ATTRIBUTE, value, monitor);
	}

	/**
	 * Returns whether this attribute exists and has a <code>true</code> value.
	 * 
	 * @param attrType
	 *    attribute to test
	 * @return <code>true</code> iff both the attribute exists and is true
	 * @throws RodinDBException
	 */
	public boolean isAttributeTrue(IAttributeType.Boolean attrType)
			throws RodinDBException {
		return hasAttribute(attrType) && getAttributeValue(attrType);
	}
	
	/**
	 * Sets the given attribute to the given value, removing the attribute if
	 * this would result in setting it to its default value (<code>false</code>).
	 * 
	 * @param attrType
	 *            attribute to set
	 * @param value
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 */
	public void setAttributeTrue(final IAttributeType.Boolean attrType,
			boolean value, IProgressMonitor monitor) throws RodinDBException {

		if (value) {
			setAttributeValue(attrType, true, monitor);
		} else {
			removeAttribute(attrType, monitor);
		}
	}
	
	@Override
	public boolean hasPOStamp() throws RodinDBException {
		return hasAttribute(EventBAttributes.POSTAMP_ATTRIBUTE);
	}
	
	@Override
	public long getPOStamp() throws RodinDBException {
		return getAttributeValue(EventBAttributes.POSTAMP_ATTRIBUTE);
	}
	
	@Override
	public void setPOStamp(long stamp, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.POSTAMP_ATTRIBUTE, stamp, monitor);
	}

	
	public void setGoal(Predicate goal, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String ref = store.putPredicate(goal);
		setAttributeValue(GOAL_ATTRIBUTE, ref , monitor);
	}
	
	public Predicate getGoal(IProofStoreReader store) throws CoreException {
		String ref = getAttributeValue(GOAL_ATTRIBUTE);
		return store.getPredicate(ref);
	}
	
	public boolean hasGoal() throws RodinDBException {
		return hasAttribute(GOAL_ATTRIBUTE);
	}

	private static String serializeCSV(Collection<Predicate> hyps,
			IProofStoreCollector store) throws RodinDBException {
		StringBuilder refs = new StringBuilder();
		String sep = "";
		for (Predicate pred : hyps) {
			refs.append(sep);
			sep = ",";
			refs.append(store.putPredicate(pred));
		}
		return refs.toString();
	}

	private static Set<Predicate> deserializeCSV(String sepRefs,
			IProofStoreReader store) throws CoreException {
		final String[] refs = sepRefs.split(",");
		final Set<Predicate> hyps = new HashSet<Predicate>(refs.length);
		for (String ref : refs) {
			if (!ref.isEmpty()) hyps.add(store.getPredicate(ref));
		}
		return hyps;
	}

	public void setHyps(Collection<Predicate> hyps, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String refs = serializeCSV(hyps, store);
		setAttributeValue(HYPS_ATTRIBUTE, refs, monitor);
	}

	public Set<Predicate> getHyps(IProofStoreReader store) throws CoreException {
		String sepRefs = getAttributeValue(HYPS_ATTRIBUTE);
		return deserializeCSV(sepRefs, store);
	}

	/**
	 * @since 2.0
	 */
	public void setUnselHyps(Collection<Predicate> hyps, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String refs = serializeCSV(hyps, store);
		setAttributeValue(UNSEL_HYPS_ATTRIBUTE, refs, monitor);
	}
	
	/**
	 * @since 2.0
	 */
	public Set<Predicate> getUnselHyps(IProofStoreReader store) throws CoreException {
		String sepRefs = getAttributeValue(UNSEL_HYPS_ATTRIBUTE);
		return deserializeCSV(sepRefs, store);
	}

	/**
	 * @since 3.0
	 */
	public void setHiddenHyps(Collection<Predicate> hyps,
			IProofStoreCollector store, IProgressMonitor monitor)
			throws RodinDBException {
		String refs = serializeCSV(hyps, store);
		setAttributeValue(HIDDEN_HYPS_ATTRIBUTE, refs, monitor);
	}

	/**
	 * @since 3.0
	 */
	public Set<Predicate> getHiddenHyps(IProofStoreReader store)
			throws CoreException {
		String sepRefs = getAttributeValue(HIDDEN_HYPS_ATTRIBUTE);
		return deserializeCSV(sepRefs, store);
	}

	public void setInfHyps(Collection<Predicate> hyps, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String refs = serializeCSV(hyps, store);
		setAttributeValue(INF_HYPS_ATTRIBUTE, refs, monitor);
	}
	
	public Set<Predicate> getInfHyps(IProofStoreReader store) throws CoreException {
		String sepRefs = getAttributeValue(INF_HYPS_ATTRIBUTE);
		return deserializeCSV(sepRefs, store);
	}
	
	public boolean hasHyps() throws RodinDBException {
		return hasAttribute(HYPS_ATTRIBUTE);
	}
	
	/**
	 * @since 2.0
	 */
	public boolean hasUnselHyps() throws RodinDBException {
		return hasAttribute(UNSEL_HYPS_ATTRIBUTE);
	}
	
	public FreeIdentifier[] getFreeIdents(FormulaFactory factory) throws CoreException {
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

		final IPRProofRule prRule = getProofRule(skel.getRule(), store);
		prRule.create(null,null);
		
		prRule.setProofRule(skel, store, monitor);
	}

	private IPRProofRule getProofRule(IProofRule rule, IProofStoreCollector store) {
		final String ref = store.putReasoner(rule.getReasonerDesc());
		return getProofRule(ref);
	}
	
	public IProofSkeleton getSkeleton(IProofStoreReader store) throws CoreException {
		final String comment = getComment();

		IPRProofRule[] rules = getProofRules();
		if (rules.length == 0) {
			return new EmptySkeleton(comment);
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
