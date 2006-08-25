/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;


import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Formula;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IParsedFormula extends IState {
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".parsedFormula";

	void setFormula(Formula formula);
	
	Formula getFormula();
}
