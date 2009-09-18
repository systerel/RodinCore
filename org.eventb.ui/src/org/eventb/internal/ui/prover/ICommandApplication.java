/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.swt.graphics.Image;
import org.eventb.ui.prover.IProofCommand;

/**
 * Common protocol for command applications.
 * <p>
 * Used as the alternative to ITacticApplication. Encapsulates data suitable for
 * applying a IProofCommand.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.1
 * @noimplement
 */
public interface ICommandApplication {

	/**
	 * Returns the proof command to be applied.
	 * 
	 * @return the proof command to be applied
	 */
	IProofCommand getProofCommand();

	/**
	 * Returns the icon associated to the command application.
	 * 
	 * @return an icon Image associated to the command application
	 */
	Image getIcon();

	/**
	 * Returns the tooltip associated to the command application.
	 * 
	 * @return a tooltip String associated to the command application
	 */
	String getTooltip();
}
