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
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * Protocol for accessing the variant of a machine.
 *
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IMachineVariantInfo extends IPOGState {

	final static IStateType<IMachineVariantInfo> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".machineVariantInfo");

	/**
	 * Returns the parsed and type-checked variant expression, or <code>null</code> 
	 * if the machine does not have a variant.
	 * 
	 * @return the parsed and type-checked variant expression, or <code>null</code> 
	 * 		if the machine does not have a variant
	 */
	Expression getExpression();
	
	/**
	 * Returns a handle to the variant, or <code>null</code> if the machine does not have a variant.
	 * 
	 * @return a handle to the variant, or <code>null</code> if the machine does not have a variant
	 */
	ISCVariant getVariant();
	
	/**
	 * Returns whether the machine has a variant.
	 * 
	 * @return whether the machine has a variant
	 */
	boolean machineHasVariant();

}
