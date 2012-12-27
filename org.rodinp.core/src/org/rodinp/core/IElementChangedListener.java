/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.IElementChangedListener
 *******************************************************************************/
package org.rodinp.core;

/**
 * An element changed listener receives notification of changes to Rodin
 * elements maintained by the Rodin database.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IElementChangedListener {

	/**
	 * Notifies that one or more Rodin elements have changed. The specific
	 * details of the change are described by the given event.
	 * 
	 * @param event
	 *            the change event
	 */
	void elementChanged(ElementChangedEvent event);

}
