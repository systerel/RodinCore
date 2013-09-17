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


import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Formula;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * Parsed formulas cannot be passed as parameters to filter modules.
 * They are accessible by means of this state component instead.
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IParsedFormula extends ISCState {
	
	final static IStateType<IParsedFormula> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".parsedFormula");
	
	/**
	 * Returns the current parsed <b>but not type-checked</b> formula.
	 * 
	 * @return the current parsed but not type-checked formula
	 */
	Formula<?> getFormula();
}
