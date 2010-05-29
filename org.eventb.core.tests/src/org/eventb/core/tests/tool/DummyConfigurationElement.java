/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added new localized methods for Eclipse 3.6
 *******************************************************************************/
package org.eventb.core.tests.tool;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eventb.core.IContextRoot;

/**
 * This is a dummy implementation of configuration elements. All contents should
 * be ignored in tests!
 * 
 * @author Stefan Hallerstede
 */
public class DummyConfigurationElement implements IConfigurationElement {

	private static final IConfigurationElement[] NO_ELEMENTS = new IConfigurationElement[0];

	public Object createExecutableExtension(String propertyName) {
		return null;
	}

	public String getAttribute(String name) {
		if (name.equals("input"))
			return IContextRoot.ELEMENT_TYPE.getId();
		else
			return name;
	}

	public String getAttribute(String attrName, String locale) {
		return getAttribute(attrName);
	}

	public String getAttributeAsIs(String name) {
		return name;
	}

	public String[] getAttributeNames() {
		return new String[0];
	}

	public IConfigurationElement[] getChildren() {
		return NO_ELEMENTS;
	}

	public IConfigurationElement[] getChildren(String name) {
		return NO_ELEMENTS;
	}

	public IContributor getContributor() {
		return null;
	}

	public IExtension getDeclaringExtension() {
		return null;
	}

	public String getName() {
		return "name";
	}

	public String getNamespace() {
		return "org.name.bin";
	}

	public String getNamespaceIdentifier() {
		return "org.name.bin";
	}

	public Object getParent() {
		return null;
	}

	public String getValue() {
		return "value";
	}

	public String getValue(String locale) {
		return getValue();
	}

	public String getValueAsIs() {
		return "value";
	}

	public boolean isValid() {
		return true;
	}

}
