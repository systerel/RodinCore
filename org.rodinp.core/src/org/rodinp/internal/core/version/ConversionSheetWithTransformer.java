/*******************************************************************************
 * Copyright (c) 2008 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConversionSheetWithTransformer extends ConversionSheet {

	private Transformer transformer;

	public ConversionSheetWithTransformer(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		super(configElement, type);
	}

	protected abstract String computeSheet();
	
	@Override
	public Transformer getTransformer() throws RodinDBException {
		if (transformer == null) {
			
			if (TransformerFactory.newInstance().getFeature(DOMSource.FEATURE)) {
				String convDoc = computeSheet();
				final Source source = new StreamSource(new StringReader(convDoc));
				TransformerFactory factory = TransformerFactory.newInstance();
				try {
					transformer = factory.newTransformer(source);
				} catch (TransformerConfigurationException e) {
					throw new RodinDBException(e, IRodinDBStatusConstants.CONVERSION_ERROR);
				}
			} else {
				throw new IllegalStateException("Rodin DB Conversion requires the DOM source feature");
			}
		}
		return transformer;
	}


}
