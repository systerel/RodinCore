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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;


/**
 * @author Stefan Hallerstede
 *
 */
public class SimpleConversion extends Conversion {

	private final String type;
	private final String version;
	private final Element[] elements;
	
	public SimpleConversion(String bundle, String type, String version, Element... elements) {
		super(bundle);
		this.type = type;
		this.version = version;
		this.elements = elements;
		for (Element element : elements) {
			element.setParent(this);
		}
	}

	@Override
	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("element"))
			return elements;
		else
			return super.getChildren(name);
	}

	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("type"))
			return type;
		else if (name.equals("version"))
			return version;
		else
			return null;
	}
	
	public String getName() throws InvalidRegistryObjectException {
		return "simple";
	}
	

}
