/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added constructor for fake executable extension description
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.rodinp.internal.core.util.Util;

/**
 * Description of an executable extension (tool or extractor) registered with
 * the tool manager.
 * <p>
 * Implements lazy class loading for the executable extension.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class ExecutableExtensionDesc {

	// Fully qualified name of the plugin that provides this executable extension
	protected final String bundleName;

	// Fully qualified name of the class implementing this executable extension
	protected final String className;

	// Configuration element describing this executable extension
	private final IConfigurationElement configElement;

	// Human-readable name of this executable extension
	protected final String name;

	// Instance of the executable extension (lazily loaded)
	private Object instance;

	/**
	 * Creates a new executable extension description.
	 * 
	 * @param configElement
	 *            description of this executable extension in the Eclipse registry
	 */
	public ExecutableExtensionDesc(IConfigurationElement configElement) {
		this.bundleName = configElement.getNamespaceIdentifier();
		this.configElement = configElement;
		this.name = configElement.getAttribute("name");
		this.className = configElement.getAttribute("class");
	}

	/**
	 * Creates a new fake executable extension description.
	 * 
	 * @param name
	 *            name of this executable extension
	 */
	public ExecutableExtensionDesc(String name) {
		this.bundleName = null;
		this.configElement = null;
		this.name = name;
		this.className = null;
	}

	/**
	 * Returns the unique identifier of this executable extension.
	 * 
	 * @return Returns the id of this executable extension.
	 */
	public abstract String getId();

	/**
	 * Returns the human-readable name of this executable extension.
	 * 
	 * @return Returns the name of this executable extension.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns an instance of this executable extension.
	 * 
	 * @return Returns an instance of this executable extension.
	 */
	protected final Object getExecutableExtension() {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}

	protected Object createInstance() {
		try {
			return configElement.createExecutableExtension("class");
		} catch (InvalidRegistryObjectException iroe) {
			// The registry has changed since creation of this tool
			// description
			// TODO implement dynamic registry update recovery
		} catch (CoreException e) {
			Util.log(e, "when loading executable extension " + className);
		}
		return null;
	}

}
