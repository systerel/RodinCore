/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.version.conf;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Stefan Hallerstede
 *
 */
public class Element extends ConfElement {

	private final String path;
	private final Operation[] operations;
	
	public Element(String path, Operation... operations) {
		this.path = path;
		this.operations = operations;
		for (Operation operation : operations)
			operation.setParent(this);
	}

	@Override
	public IConfigurationElement[] getChildren()
			throws InvalidRegistryObjectException {
		return operations;
	}

	@Override
	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
		ArrayList<IConfigurationElement> elements = new ArrayList<IConfigurationElement>(operations.length);
		for (Operation operation : operations)
			if (operation.getName().equals(name))
				elements.add(operation);
		return elements.toArray(new Element[elements.size()]);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("elementPath"))
			return path;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getName()
	 */
	public String getName() throws InvalidRegistryObjectException {
		return "element";
	}

}
