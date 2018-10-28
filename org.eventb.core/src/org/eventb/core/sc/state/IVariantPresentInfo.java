/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * State component telling whether a valid variant is present in the current
 * machine being checked.
 *
 * @author Laurent Voisin
 * @since 3.4
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IVariantPresentInfo extends ISCState {

	final static IStateType<IVariantPresentInfo> STATE_TYPE = SCCore
			.getToolStateType(EventBPlugin.PLUGIN_ID + ".variantPresentInfo");

	/**
	 * Returns whether the machine contains a valid variant.
	 * 
	 * @return <code>true</code> iff the machine contains a valid variant
	 * @throws CoreException if this state component is mutable
	 */
	boolean isTrue() throws CoreException;

	/**
	 * Sets whether the current machine contains a valid variant.
	 * 
	 * @param present whether the variant is present
	 * @throws CoreException if this state component is immutable
	 */
	void set(boolean present) throws CoreException;

}
