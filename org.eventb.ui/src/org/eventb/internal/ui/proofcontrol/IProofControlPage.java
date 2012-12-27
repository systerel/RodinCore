/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import org.eclipse.ui.part.IPage;

/**
 * @author htson
 *         <p>
 *         This is the interface for the Proof Control pages.
 */
public interface IProofControlPage extends IPage {

	/**
	 * Return the global input from the Text Area.
	 * <p>
	 * 
	 * @return the string input from the Text Area
	 */
	String getInput();
}
