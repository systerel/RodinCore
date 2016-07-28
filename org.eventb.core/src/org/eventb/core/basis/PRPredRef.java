/*******************************************************************************
 * Copyright (c) 2006, 2016 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.STORE_REF_ATTRIBUTE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PRPredRef extends InternalElement implements IPRPredRef{

	private static final Predicate[] NO_PREDICATE = new Predicate[0];

	public PRPredRef(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRPredRef> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public Predicate[] getPredicates(IProofStoreReader store)
			throws CoreException {

		final String value = getAttributeValue(STORE_REF_ATTRIBUTE);
		if (value.isEmpty()) {
			return NO_PREDICATE;
		}
		final String[] refs = value.split(",", -1);
		final int length = refs.length;
		final Predicate[] preds = new Predicate[length];
		for (int i = 0; i < preds.length; i++) {
			final String ref = refs[i];
			if (ref.length() == 0) {
				preds[i] = null;
			} else {
				preds[i] = store.getPredicate(ref);
			}
		}
		return preds;
	}

	@Override
	public void setPredicates(Predicate[] preds, IProofStoreCollector store,
			IProgressMonitor monitor) throws RodinDBException {

		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (Predicate pred: preds) {
			builder.append(sep);
			sep = ",";
			if (pred != null) {
				builder.append(store.putPredicate(pred));
			}
		}
		setAttributeValue(STORE_REF_ATTRIBUTE, builder.toString(), monitor);
	}

}
