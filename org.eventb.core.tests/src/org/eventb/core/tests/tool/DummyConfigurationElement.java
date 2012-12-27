/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
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

	@Override
	public Object createExecutableExtension(String propertyName) {
		return null;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equals("input"))
			return IContextRoot.ELEMENT_TYPE.getId();
		else
			return name;
	}

	@Override
	public String getAttribute(String attrName, String locale) {
		return getAttribute(attrName);
	}

	@Deprecated
	@Override
	public String getAttributeAsIs(String name) {
		return name;
	}

	@Override
	public String[] getAttributeNames() {
		return new String[0];
	}

	@Override
	public IConfigurationElement[] getChildren() {
		return NO_ELEMENTS;
	}

	@Override
	public IConfigurationElement[] getChildren(String name) {
		return NO_ELEMENTS;
	}

	@Override
	public IContributor getContributor() {
		return null;
	}

	@Override
	public IExtension getDeclaringExtension() {
		return null;
	}

	@Override
	public String getName() {
		return "name";
	}

	@Deprecated
	@Override
	public String getNamespace() {
		return "org.name.bin";
	}

	@Override
	public String getNamespaceIdentifier() {
		return "org.name.bin";
	}

	@Override
	public Object getParent() {
		return null;
	}

	@Override
	public String getValue() {
		return "value";
	}

	@Override
	public String getValue(String locale) {
		return getValue();
	}

	@Deprecated
	@Override
	public String getValueAsIs() {
		return "value";
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
