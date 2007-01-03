/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;


import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Formula;

/**
 * Parsed formulas cannot be passed as parameters to filter modules.
 * They are accessible by means of this state component instead.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IParsedFormula extends IState {
	
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".parsedFormula";
	
	/**
	 * Returns the current parsed <b>but not type-checked</b> formula.
	 * 
	 * @return the current parsed but not type-checked formula
	 */
	Formula getFormula();
}
