/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

/**
 * Common protocol for internal SC machines.
 * <p>
 * An internal SC machine is the internalized form of a statically checked
 * machine. Internal SC machines are used to remove (transitive) dependencies
 * between machines. SC machines on wich a machine depends are simply copied
 * inside the SC machine.
 * </p>
 * <p>
 * An internal SC machine has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}. Its child elements
 * can be manipulated via interface {@link org.eventb.core.ISCContext}. This
 * interface itself does not contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCMachine
 * 
 * @author Stefan Hallerstede
 */
public interface ISCInternalMachine extends ISCMachine {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scInternalMachine"; //$NON-NLS-1$

	// No additional method

}
