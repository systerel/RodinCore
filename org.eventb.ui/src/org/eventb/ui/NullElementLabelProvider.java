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
package org.eventb.ui;

/**
 * @author htson
 *         <p>
 *         A default implementation of and
 *         <code>org.eventb.ui.IElementLabelProvider</code> which returns
 *         empty string always
 */
public class NullElementLabelProvider implements IElementLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.ui.IElementLabelProvider#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj) {
		return "";
	}

}
