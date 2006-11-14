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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IExtendsContext;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of
 *         <code>org.eventb.ui.IElementLabelProvider</code> providing label
 *         for extends context element using the abstract context name
 */
public class ExtendsContextLabelProvider implements IElementLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.ui.IElementLabelProvider#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj) {
		if (obj instanceof IExtendsContext) {
			try {
				return ((IExtendsContext) obj)
						.getAbstractContextName(new NullProgressMonitor());
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
