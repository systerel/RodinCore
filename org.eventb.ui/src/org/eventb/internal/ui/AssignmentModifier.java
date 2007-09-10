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
import org.eventb.core.IAssignmentElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         Modifier class for assignment elements which sets the assignment
 *         string.
 *         </p>
 */
public class AssignmentModifier implements IElementModifier {

	/* (non-Javadoc)
	 * @see org.eventb.ui.IElementModifier#modify(org.rodinp.core.IRodinElement, java.lang.String)
	 */
	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		// Try to set the assignment string if element is an assignment element.
		if (element instanceof IAssignmentElement) {
			IAssignmentElement aElement = (IAssignmentElement) element;
			String assignmentString = null;
			try {
				assignmentString = aElement.getAssignmentString();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			
			// Try to set the assignment string if the current assignment string
			// is <code>null</code> or is different from the text
			if (assignmentString == null || !assignmentString.equals(text))
				aElement.setAssignmentString(text, new NullProgressMonitor());
		}
		// Do nothing if the element is not an assignment element.
		return;
	}

}
