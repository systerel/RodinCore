/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.autocompletion;

import org.eclipse.jface.fieldassist.IContentProposalListener2;

/**
 * Event-B interface for content proposal adapters.
 * 
 * @author Thomas Muller
 * @see IContentProposalListener2
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
