/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.IState;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IIdentifierTable extends IState, Iterable<FreeIdentifier> {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".identifierTable";
	
	void addIdentifier(FreeIdentifier identifier);
	
	boolean containsIdentifier(FreeIdentifier identifier);
	
	void save(IInternalParent parent, IProgressMonitor monitor) throws RodinDBException;

}
