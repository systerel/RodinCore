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
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class ConfElement implements IConfigurationElement {

	private ConfElement parent;

	private final static IConfigurationElement[] NO_CONF = new IConfigurationElement[0];

	public Object createExecutableExtension(String propertyName) {
		throw new UnsupportedOperationException();
	}

	public String getAttribute(String attrName, String locale) {
		return getAttribute(attrName);
	}

	public String getAttributeAsIs(String name) {
		throw new UnsupportedOperationException();
	}

	public String[] getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	public IConfigurationElement[] getChildren() {
		throw new UnsupportedOperationException();
	}

	public IConfigurationElement[] getChildren(String name) {
		return NO_CONF;
	}

	public IExtension getDeclaringExtension() {
		throw new UnsupportedOperationException();
	}

	public String getNamespace() {
		return null;
	}

	public String getNamespaceIdentifier() {
		return null;
	}

	public IContributor getContributor() {
		return parent.getContributor();
	}

	public void setParent(ConfElement parent) {
		this.parent = parent;
	}

	public ConfElement getParent() {
		return parent;
	}

	public String getValue() {
		throw new UnsupportedOperationException();
	}

	public String getValue(String locale) {
		throw new UnsupportedOperationException();
	}

	public String getValueAsIs() {
		throw new UnsupportedOperationException();
	}

	public boolean isValid() {
		throw new UnsupportedOperationException();
	}

}
