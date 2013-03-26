/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.ui;

import org.rodinp.core.IRodinElement;

/**
 * Common protocol for listening to changes of the Event-B editor.
 * 
 * @author htson
 * @since 3.0
 */
public interface IStatusChangedListener {

	/**
	 * Called when the status (saved/unsaved) of an element has changed.
	 * <p>
	 * This method is called for each new elements that gets created in the
	 * editor, and called again when the edited file is saved to notify that the
	 * elements is no longer new.
	 * </p>
	 * 
	 * @param element
	 *            the element which made the saved/unsaved status change
	 */
	void statusChanged(IRodinElement element);

}
