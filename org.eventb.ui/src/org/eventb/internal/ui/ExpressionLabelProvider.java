/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eventb.core.IExpressionElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A label provider class for expression element. Return the expression
 *         string if the element is indeed expression element, otherwise, return
 *         <code>null</code>.
 *         </p>
 */
public class ExpressionLabelProvider implements IElementLabelProvider {

	/* (non-Javadoc)
	 * @see org.eventb.ui.IElementLabelProvider#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IExpressionElement) {
			return ((IExpressionElement) obj).getExpressionString();
		}
		return null;
	}

}
