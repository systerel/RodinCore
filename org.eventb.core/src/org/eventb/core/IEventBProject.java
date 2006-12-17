/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IAdaptable;
import org.rodinp.core.IRodinProject;

/**
 * Common protocol for Event-B projects. This interface groups together various
 * methods for accessing to all event-B files of a Rodin project.
 * <p>
 * Instances of this interface cannot be built directly. The standard way of
 * getting a handle to a Rodin project is to use the adapter factory from a
 * rodin project:
 * 
 * <pre>
 *    IRodinProject rodinProject = ...;
 *    IEventBProject evbProject =
 *            (IEventBProject) rodinProject.getAdapter(IEventBProject.class);
 * </pre>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @see IEventBFile
 */
public interface IEventBProject extends IAdaptable {

	/**
	 * Returns a handle to the unchecked version of the context with the given
	 * name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the unchecked version of the context
	 */
	IContextFile getContextFile(String componentName);

	/**
	 * Returns a handle to the unchecked version of the machine with the given
	 * name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the unchecked version of the machine
	 */
	IMachineFile getMachineFile(String componentName);

	/**
	 * Returns a handle to the statically checked version of the context with
	 * the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the unchecked version of the context
	 */
	ISCContextFile getSCContextFile(String componentName);

	/**
	 * Returns a handle to the statically checked version of the machine with
	 * the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the unchecked version of the machine
	 */
	ISCMachineFile getSCMachineFile(String componentName);

	/**
	 * Returns a handle to the file containing proof obligations for the
	 * component with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the proof obligation file of this component
	 */
	IPOFile getPOFile(String componentName);

	/**
	 * Returns a handle to the file containing proofs for the component with the
	 * given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the proof file of this component
	 */
	IPRFile getPRFile(String componentName);

	/**
	 * Returns a handle to the file containing proof status for the component
	 * with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the proof status file of this component
	 */
	IPSFile getPSFile(String componentName);

	/**
	 * Returns the underlying Rodin project.
	 * 
	 * @return the underlying Rodin project
	 */
	IRodinProject getRodinProject();

}
