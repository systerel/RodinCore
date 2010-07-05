/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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

import org.eclipse.core.runtime.IAdaptable;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IRodinFile;
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
 * </p>
 * <p>
 * Another method is to ask for the Event-B project of an Event-B root element:
 * 
 * <pre>
 *    IEventBRoot root = ...;
 *    IEventBProject evbProject = root.getEventBProject();
 * </pre>
 * 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
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
	IRodinFile getContextFile(String componentName);

	/**
	 * Returns a handle to the root of the unchecked version of the context with
	 * the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the root of the unchecked version of the context
	 */
	IContextRoot getContextRoot(String componentName);

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
	IRodinFile getMachineFile(String componentName);

	/**
	 * Returns a handle to the root of the unchecked version of the machine with
	 * the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the root of the unchecked version of the machine
	 */
	IMachineRoot getMachineRoot(String componentName);

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
	IRodinFile getSCContextFile(String componentName);

	/**
	 * Returns a handle to the root of the statically checked version of the
	 * context with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the root of the unchecked version of the context
	 */
	ISCContextRoot getSCContextRoot(String componentName);

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
	IRodinFile getSCMachineFile(String componentName);

	/**
	 * Returns a handle to the root of the statically checked version of the
	 * machine with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the root of the unchecked version of the machine
	 */
	ISCMachineRoot getSCMachineRoot(String componentName);

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
	IRodinFile getPOFile(String componentName);

	/**
	 * Returns a handle to the root element containing proof obligations for the
	 * component with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the proof obligation root element of this component
	 */
	IPORoot getPORoot(String componentName);

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
	IRodinFile getPRFile(String componentName);

	/**
	 * Returns a handle to the root element containing proofs for the component
	 * with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the proof root element of this component
	 */
	IPRRoot getPRRoot(String componentName);

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
	IRodinFile getPSFile(String componentName);

	/**
	 * Returns a handle to the root element containing proof status for the
	 * component with the given name.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @param componentName
	 *            name of the component (i.e, without any extension)
	 * @return a handle to the proof status root element of this component
	 */
	IPSRoot getPSRoot(String componentName);

	/**
	 * Returns the underlying Rodin project.
	 * 
	 * @return the underlying Rodin project
	 */
	IRodinProject getRodinProject();
	
	/**
	 * Returns the formula factory associated to this project.
	 * 
	 * @return the associated formula factory
	 */
	FormulaFactory getFormulaFactory();

}
