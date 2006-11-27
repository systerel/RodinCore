/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A hint for a proof of a proof obligation. A hint is labeled so that hints can be
 * accumulated. This is a marker interface to permit systematic treatment of hints
 * in implementations. All hints should extend this interface.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * 
 * @see IPOSelectionHint
 * 
 * @author Stefan Hallerstede
 */
public interface IPOHint extends ILabeledElement {
	
	// marker interface
}
