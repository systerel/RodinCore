/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Expression;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * State component for the variant of the current machine being checked.
 *
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IVariantInfo extends ISCState {

	final static IStateType<IVariantInfo> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".variantInfo");
	
	/**
	 * Returns the parsed and type-checked variant.
	 * 
	 * @return the parsed and type-checked variant
	 * @throws CoreException if this state component is mutable
	 */
	Expression getExpression() throws CoreException;
	
}
