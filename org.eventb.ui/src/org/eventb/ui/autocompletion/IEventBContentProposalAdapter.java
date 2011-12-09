/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.ui.autocompletion;

import org.eclipse.jface.fieldassist.IContentProposalListener2;

/**
 * Event-B interface for content proposal adapters.
 * 
 * @author Thomas Muller
 * 
 * @since 2.4
 */
public interface IEventBContentProposalAdapter extends
		IContentProposalListener2 {

	/**
	 * Returns whether the content proposal pop-up is open or not.
	 * 
	 * @return <code>true</code> iff the proposal pop-up is open
	 */
	boolean isProposalPopupOpen();

}
