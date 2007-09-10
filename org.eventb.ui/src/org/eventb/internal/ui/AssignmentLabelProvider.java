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

import org.eventb.core.IAssignmentElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This label provider class for assignment elements which return the
 *         assignment string.
 *         </p>
 */
public class AssignmentLabelProvider implements IElementLabelProvider {

	/* (non-Javadoc)
	 * @see org.eventb.ui.IElementLabelProvider#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IAssignmentElement) {
			return ((IAssignmentElement) obj).getAssignmentString();
		}
		return null;
	}

}
