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
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A modifier class for extends context elements.
 *         </p>
 */
public class ExtendsContextModifier implements IElementModifier {

	/* (non-Javadoc)
	 * @see org.eventb.ui.IElementModifier#modify(org.rodinp.core.IRodinElement, java.lang.String)
	 */
	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		// Try to set the abstract context name if the element is an extends
		// context element.
		if (element instanceof IExtendsContext) {
			IExtendsContext extendsContext = (IExtendsContext) element;
			String abstractContextName = null;
			try {
				abstractContextName = extendsContext.getAbstractContextName();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			// Try to set the abstract context name if it does not exists
			// or not equal to the input text.
			if (abstractContextName == null
					|| !abstractContextName.equals(text))
				extendsContext.setAbstractContextName(text,
						new NullProgressMonitor());
		}
		// Do nothing if the element is not an extends context element.
		return;
	}

}
