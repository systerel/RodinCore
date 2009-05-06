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
 *     Systerel - used XSLWriter
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class SimpleConversionSheet extends ConversionSheetWithTransformer {

	private final Conversion[] conversions;
		
	public SimpleConversionSheet(IConfigurationElement configElement, IInternalElementType<?> type) {
		super(configElement, type);
		IConfigurationElement[] confElements = configElement.getChildren("element");
		Conversion[] convs = new Conversion[confElements.length];
		final List<String> paths = new ArrayList<String>(confElements.length);
		boolean hasRoot = false;
		for (int i=0; i<confElements.length; i++) {
			convs[i] = new ElementConversion(confElements[i], this);
			hasRoot |= convs[i].isRoot();
			String path = convs[i].getPath();
			if (paths.contains(path)) {
				throw new IllegalStateException(
						"Paths of different groups of operations must be distinct");
			} else {
				paths.add(path);
			}
		}
		if (hasRoot) {
			conversions = convs;
		} else {
			conversions = new Conversion[convs.length + 1];
			System.arraycopy(convs, 0, conversions, 0, convs.length);
			conversions[convs.length] = new FakeRootConversion(type, this);
		}
	}

	public boolean hasSorter() {
		return false;
	}

	public void addSorter(XSLWriter writer) {
		// do nothing
	}

	@Override
	protected void addTemplates(XSLWriter writer) {
		for (Conversion conversion : conversions) {
			conversion.addTemplates(writer);
		}
	}
	
	@Override
	protected void addPostfix(XSLWriter writer) {
		writer.appendCopyAllTemplate(this);
		writer.endTransform();
	}

}

