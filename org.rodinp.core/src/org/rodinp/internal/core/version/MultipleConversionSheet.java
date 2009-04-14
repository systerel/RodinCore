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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Nicolas Beauger
 * 
 */
public class MultipleConversionSheet extends ComplexConversionSheet {

	private final List<PatternConversion> conversions = new ArrayList<PatternConversion>();

	public MultipleConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		super(configElement, type);
		final IConfigurationElement[] confElements = configElement
				.getChildren("pattern");
		for (IConfigurationElement confElem : confElements) {
			conversions.add(new PatternConversion(confElem));
		}
	}

	@Override
	protected String computeSheet() {
		final StringBuffer buffer = new StringBuffer();
		for (PatternConversion patternConv : conversions) {
			patternConv.addTemplates(buffer);
		}

		return computeSheet(buffer.toString());
	}

}
