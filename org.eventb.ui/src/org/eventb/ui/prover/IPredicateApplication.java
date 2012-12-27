/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover;

import org.eclipse.swt.graphics.Image;

/**
 * A tactic application not located inside a formula.
 * <p>
 * Implementors of this interface will be applied through an icon hyperlink
 * beside a hypothesis or goal predicate text.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.1
 */
public interface IPredicateApplication extends ITacticApplication {

	/**
	 * Returns the icon associated with this tactic application.
	 * <p>
	 * Defaults to the icon provided in the extension point if <code>null</code>
	 * .
	 * </p>
	 * 
	 * @return an icon Image or <code>null</code>
	 */
	Image getIcon();

	/**
	 * Returns the label associated with this tactic application.
	 * <p>
	 * Defaults to the tooltip provided in the extension point if
	 * <code>null</code>.
	 * </p>
	 * 
	 * @return a tooltip String or <code>null</code>
	 */
	String getTooltip();

}
