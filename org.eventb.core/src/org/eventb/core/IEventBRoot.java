/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - introduction of generated elements
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IInternalElement;

/**
 * Common protocol for Event-B files. This interface groups together various
 * methods for accessing to all the files associated to an event-B component.
 * <p>
 * The information associated to an event-B context is stored in the following
 * files:
 * <ul>
 * <li>an unchecked version of the context {@link IContextRoot}</li>
 * <li>an statically checked version of the context {@link ISCContextRoot}</li>
 * <li>a proof obligation file {@link IPORoot}</li>
 * <li>a proof status file {@link IPSRoot}</li>
 * <li>a proof file {@link IPRRoot}</li>
 * </ul>
 * </p>
 * <p>
 * The information associated to an event-B machine is stored in the following
 * files:
 * <ul>
 * <li>an unchecked version of the machine {@link IMachineRoot}</li>
 * <li>an statically checked version of the machine {@link ISCMachineRoot}</li>
 * <li>a proof obligation file {@link IPORoot}</li>
 * <li>a proof status file {@link IPSRoot}</li>
 * <li>a proof file {@link IPRRoot}</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IEventBRoot extends IInternalElement, IGeneratedElement {

	/**
	 * Returns the name of the event-B component associated to this file.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return the name of the event-B component
	 */
	String getComponentName();

	/**
	 * Returns a handle to the event-B project containing this file.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the event-B project of this file
	 */
	IEventBProject getEventBProject();

	/**
	 * Returns a handle to the unchecked version of this component seen as an
	 * event-B context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding context
	 */
	IContextRoot getContextRoot();

	/**
	 * Returns a handle to the unchecked version of this component seen as an
	 * event-B machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding machine
	 */
	IMachineRoot getMachineRoot();

	/**
	 * Returns a handle to the checked version of this component seen as an
	 * event-B context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding context
	 */
	ISCContextRoot getSCContextRoot();

	/**
	 * Returns a handle to the checked version of this component seen as an
	 * event-B machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding machine
	 */
	ISCMachineRoot getSCMachineRoot();

	/**
	 * Returns a handle to the file containing proof obligations for this
	 * component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof obligation file of this component
	 */
	IPORoot getPORoot();

	/**
	 * Returns a handle to the file containing proofs for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	IPRRoot getPRRoot();

	/**
	 * Returns a handle to the file containing proof status for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof status file of this component
	 */
	IPSRoot getPSRoot();

}
