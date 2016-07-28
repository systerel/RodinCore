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
import org.eventb.core.IPRExprRef;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Expression;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PRExprRef extends InternalElement implements IPRExprRef{

	private static final Expression[] NO_EXPRESSION = new Expression[0];

	public PRExprRef(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRExprRef> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public Expression[] getExpressions(IProofStoreReader store)
			throws CoreException {

		final String value = getAttributeValue(STORE_REF_ATTRIBUTE);
		if (value.isEmpty()) {
			return NO_EXPRESSION;
		}
		final String[] refs = value.split(",", -1);
		final int length = refs.length;
		final Expression[] exprs = new Expression[length];
		for (int i = 0; i < exprs.length; i++) {
			final String ref = refs[i];
			if (ref.length() == 0) {
				exprs[i] = null;
			} else {
				exprs[i] = store.getExpression(ref);
			}
		}
		return exprs;
	}

	@Override
	public void setExpressions(Expression[] exprs, IProofStoreCollector store,
			IProgressMonitor monitor) throws RodinDBException {
		
		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (Expression expr: exprs) {
			builder.append(sep);
			sep = ",";
			if (expr != null) {
				builder.append(store.putExpression(expr));
			}
		}
		setAttributeValue(STORE_REF_ATTRIBUTE, builder.toString(), monitor);
	}

}
