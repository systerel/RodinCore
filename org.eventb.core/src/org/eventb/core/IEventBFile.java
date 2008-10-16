/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IRodinFile;

/**
 * Common protocol for Event-B files. This interface groups together various
 * methods for accessing to all the files associated to an event-B component.
 * <p>
 * The information associated to an event-B context is stored in the following
 * files:
 * <ul>
 * <li>an unchecked version of the context {@link IContextFile}</li>
 * <li>an statically checked version of the context {@link ISCContextFile}</li>
 * <li>a proof obligation file {@link IPOFile}</li>
 * <li>a proof status file {@link IPSFile}</li>
 * <li>a proof file {@link IPRFile}</li>
 * </ul>
 * </p>
 * <p>
 * The information associated to an event-B machine is stored in the following
 * files:
 * <ul>
 * <li>an unchecked version of the machine {@link IMachineFile}</li>
 * <li>an statically checked version of the machine {@link ISCMachineFile}</li>
 * <li>a proof obligation file {@link IPOFile}</li>
 * <li>a proof status file {@link IPSFile}</li>
 * <li>a proof file {@link IPRFile}</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
@Deprecated
public interface IEventBFile extends IRodinFile {

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
	IContextFile getContextFile();

	/**
	 * Returns a handle to the unchecked version of this component seen as an
	 * event-B machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding machine
	 */
	IMachineFile getMachineFile();

	/**
	 * Returns a handle to the checked version of this component seen as an
	 * event-B context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding context
	 */
	ISCContextFile getSCContextFile();

	/**
	 * Returns a handle to the checked version of this component seen as an
	 * event-B machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding machine
	 */
	ISCMachineFile getSCMachineFile();

	/**
	 * Returns a handle to the file containing proof obligations for this
	 * component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof obligation file of this component
	 */
	IPOFile getPOFile();

	/**
	 * Returns a handle to the file containing proofs for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	IPRFile getPRFile();

	/**
	 * Returns a handle to the file containing proof status for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof status file of this component
	 */
	IPSFile getPSFile();

}
