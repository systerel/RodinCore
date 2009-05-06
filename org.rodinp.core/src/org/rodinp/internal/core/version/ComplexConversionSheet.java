/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.internal.core.version;

import static org.rodinp.internal.core.Buffer.VERSION_ATTRIBUTE;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;

public abstract class ComplexConversionSheet extends
		ConversionSheetWithTransformer {

	public ComplexConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		super(configElement, type);
	}

	@Override
	protected void addTemplates(XSLWriter writer) {
		addRootTemplate(writer);
		addComplexTemplates(writer);
	}

	private void addRootTemplate(XSLWriter writer) {
		writer.beginTemplate("/" + getType().getId());
		writer.beginElement(getType().getId());
		writer.simpleAttribute(VERSION_ATTRIBUTE, String.valueOf(getVersion()));
		writer.simpleApplyTemplates(XSLConstants.XSL_ALL);
		writer.endElement();
		writer.endTemplate();
	}

	protected abstract void addComplexTemplates(XSLWriter writer);

}