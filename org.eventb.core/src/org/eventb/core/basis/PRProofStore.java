/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.PR_SETS_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRProofStore;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofStore extends InternalElement implements IPRProofStore {

	private static final String[] NO_STRINGS = new String[0];

	public PRProofStore(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public IPRStoredExpr getExpression(String name) {
		return (IPRStoredExpr) getInternalElement(IPRStoredExpr.ELEMENT_TYPE, name);
	}

	public IPRStoredExpr[] getExpressions() throws RodinDBException {
		return (IPRStoredExpr[]) getChildrenOfType(IPRStoredExpr.ELEMENT_TYPE);
	}

	public IPRIdentifier getIdentifier(String name) {
		return (IPRIdentifier) getInternalElement(IPRIdentifier.ELEMENT_TYPE, name);
	}

	public IPRIdentifier[] getIdentifiers() throws RodinDBException {
		return (IPRIdentifier[]) getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
	}

	public IPRStoredPred getPredicate(String name) {
		return (IPRStoredPred) getInternalElement(IPRStoredPred.ELEMENT_TYPE, name);
	}

	public IPRStoredPred[] getPredicates() throws RodinDBException {
		return (IPRStoredPred[]) getChildrenOfType(IPRStoredPred.ELEMENT_TYPE);
	}

	public String[] getSets() throws RodinDBException {
		if (hasAttribute(PR_SETS_ATTRIBUTE)) {
			String value = getAttributeValue(PR_SETS_ATTRIBUTE);
			return value.split(",");
		}
		return NO_STRINGS;
	}

	public void setSets(String[] sets, IProgressMonitor monitor) throws RodinDBException {
		final int length = sets.length;
		if (length == 0) {
			removeAttribute(PR_SETS_ATTRIBUTE, monitor);
			return;
		}
		if (length == 1) {
			setAttributeValue(PR_SETS_ATTRIBUTE, sets[0], monitor);
			return;
		}
		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (String name: sets) {
			builder.append(sep);
			sep = ",";
			builder.append(name);
		}
		setAttributeValue(PR_SETS_ATTRIBUTE, builder.toString(), monitor);
	}
	
}
