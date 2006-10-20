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

import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of
 *         <code>org.eventb.ui.IElementLabelProvider</code> which used the
 *         content of elements for the labels
 */
public class ContentElementLabelProvider implements IElementLabelProvider {

	/* (non-Javadoc)
	 * @see org.eventb.ui.IElementLabelProvider#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj) {
		if (obj instanceof IInternalElement) {
			try {
				return ((IInternalElement) obj).getContents();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return "";
			}
		}
		return "";
	}

}
