/*******************************************************************************
 * Copyright (c) 2008, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - added DEFAULT_CONFIGURATION
 *******************************************************************************/
package org.eventb.core;

import static org.eventb.core.EventBPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.IPOGFilterModule;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.internal.core.tool.types.IModule;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * The contents of Event-B models is interpreted with respect to configurations:
 * a configuration describes a certain composition of modules of the static
 * checker and the proof obligation generator.
 * <p>
 * The value of the configuration, a string, is used to retrieve instances of
 * static checkers proof obligation generators. They are declared in extensions
 * of type <code>configuration</code> contributed to the extension point
 * <code>configurations</code>.
 * </p>
 * <p>
 * Each configuration selects a set of processor and filter modules.
 * </p>
 * 
 * @see IModule
 * @see ISCFilterModule
 * @see ISCProcessorModule
 * @see IPOGFilterModule
 * @see IPOGProcessorModule
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IConfigurationElement extends IRodinElement {

	/**
	 * Default configuration of the Static Checker and POG tools which
	 * implements forward simulation.
	 */
	String DEFAULT_CONFIGURATION = PLUGIN_ID + ".fwd";

	/**
	 * Sets the configuration of this element.
	 * 
	 * @param configuration
	 *            the specified configuration
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setConfiguration(String configuration, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the configuration of this element.
	 * 
	 * @return the configuration of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	public String getConfiguration() throws RodinDBException;

	/**
	 * Returns whether this element has a configuration.
	 * 
	 * @return whether this element has a configuration
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean hasConfiguration() throws RodinDBException;
}
