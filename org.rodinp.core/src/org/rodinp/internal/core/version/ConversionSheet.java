/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core.version;

import javax.xml.transform.Transformer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConversionSheet extends ExtensionDesc {
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer("SHEET ");
		String bundleName = getBundleName();
		b.append(bundleName == null ? "?" : bundleName);
		b.append(":");
		b.append(type);
		b.append(":");
		b.append(version);
		return b.toString();
	}


	private final long version;
	private final IInternalElementType<?> type;
	
	public ConversionSheet(IConfigurationElement configElement, IInternalElementType<?> type) {
		super(configElement);
		this.type = type;
		String vString = configElement.getAttribute("version");
		try {
			version = Long.parseLong(vString);
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Invalid version number " + vString, e);
		}
		if (version < 1) {
			throw new IllegalStateException("Invalid version number " + vString);
		}
	}
	
	public void checkBundle(String typeString, ExtensionDesc desc) {
		assert desc.getBundleName() != null;
		
		String typeBundle = typeString.substring(0, typeString.lastIndexOf('.'));
		
		if (typeBundle.equals(desc.getBundleName())) {
			return;
		} else {
			throw new IllegalStateException("Conversion sheet not in contributing bundle: "
					+ typeString + ":" + getVersion());
		}
	}
	
	public abstract Transformer getTransformer() throws RodinDBException;

	public long getVersion() {
		return version;
	}
	
	public IInternalElementType<?> getType() {
		return type;
	}

}
