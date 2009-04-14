/*******************************************************************************
 * Copyright (c) 2008, 2009 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added attribute modification
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class SourceConversionSheet extends ComplexConversionSheet {
	
	private final String source;

	public SourceConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		super(configElement, type);
		source = configElement.getAttribute("source");
	}
	
	public SourceConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type, String source) {
		super(configElement, type);
		this.source = source;
	}
	
	@Override
	protected String computeSheet() {
		return computeSheet(source);
	}


}
