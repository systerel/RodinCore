/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.itemdescription;

/**
 * Public interface for the editor item descriptions. Users shall implement
 * inherited interfaces {@link IElementDesc} and {@link IAttributeDesc}.
 * 
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IItemDesc {

	/**
	 * Returns the prefix that should be displayed before the item corresponding
	 * to this description.
	 * 
	 * @return the string prefix to be displayed before the item of the current
	 *         description
	 */
	public String getPrefix();

}
