/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.RodinDBException;

/**
 * State component to store reserved names of a component. Most name conflicts
 * are detected using a {@link ISymbolTable} but this is not always practical.
 * For instance, the parameters of abstract events must not conflict with
 * constants and variables, but they are not yet part of the symbol table when
 * checking constants and variables. This state element allows to check this
 * kind of conflict without having to juggle with symbol tables.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @author Laurent Voisin
 * @since 3.1
 */
public interface IReservedNameTable extends ISCState {

	/**
	 * Unique type of this state component.
	 */
	IStateType<IReservedNameTable> STATE_TYPE = SCCore
			.getToolStateType(EventBPlugin.PLUGIN_ID + ".reservedNameTable");

	/**
	 * Adds a new reserved name together with its associated warning.
	 * 
	 * @param name
	 *            some identifier name
	 * @param warning
	 *            the warning to issue in case of conflict
	 */
	void add(String name, ISymbolWarning warning);

	/**
	 * Tells whether the given name has been reserved
	 * 
	 * @param name
	 *            the identifier name to test for conflict
	 * @return <code>true</code> iff the name has already been reserved
	 */
	boolean isInConflict(String name);

	/**
	 * Issue all the warnings that have been associated to the given name.
	 * 
	 * @param name
	 *            some identifier name
	 * @param display
	 *            the display to use for creating the warning markers
	 * @throws RodinDBException
	 *             in case of error creating the markers
	 */
	void issueWarningFor(String name, IMarkerDisplay display)
			throws RodinDBException;

}
