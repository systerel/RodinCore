/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
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
