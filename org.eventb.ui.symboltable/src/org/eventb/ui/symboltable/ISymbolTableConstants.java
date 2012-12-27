/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.symboltable;

/**
 * This interface exports plug-in constants to be used by other plug-ins.
 * 
 * @author "Nicolas Beauger"
 * @since 1.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISymbolTableConstants {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eventb.ui.symboltable";

	// The View ID
	public static final String SYMBOL_TABLE_VIEW_ID = PLUGIN_ID + ".view";

}
