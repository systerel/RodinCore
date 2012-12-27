/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed input parameter of convert() to InputStream
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class Converter {

	private static final ConversionSheet[] NO_SHEETS = new ConversionSheet[0];

	private static final int MIN_BUFFER_SIZE = 4096;

	private ConversionSheet[] sheets = NO_SHEETS;

	public byte[] convert(InputStream inputStream, long currentVersion,
			long targetVersion) throws RodinDBException {
		final int start = find(currentVersion);
		final int end = find(targetVersion);
		byte[] bytes = null;
		if (start < end) {
			bytes = computeConversion(inputStream, start);
			for (int i = start + 1; i < end; i++) {
				final InputStream is = new ByteArrayInputStream(bytes);
				bytes = computeConversion(is, i);
			}
		}
		return bytes;
	}

	private int find(long currentVersion) {
		for (int i = 0; i < sheets.length; i++) {
			if (sheets[i].getVersion() > currentVersion)
				return i;
		}
		return sheets.length;
	}

	private byte[] computeConversion(InputStream inputStream, int index)
			throws RodinDBException {
		final StreamSource s = new StreamSource(inputStream);
		final int sz = getOutputBufferSize(inputStream);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(sz);
		final StreamResult r = new StreamResult(outputStream);
		final Transformer transformer = sheets[index].getTransformer();
		try {
			transformer.transform(s, r);
		} catch (TransformerException e) {
			throw new RodinDBException(e,
					IRodinDBStatusConstants.CONVERSION_ERROR);
		}
		return outputStream.toByteArray();
	}

	private int getOutputBufferSize(InputStream inputStream) {
		try {
			final int result = inputStream.available();
			if (result >= MIN_BUFFER_SIZE) {
				return result;
			}
		} catch (IOException e) {
			// Ignore, will return default
		}
		return MIN_BUFFER_SIZE;
	}

	ConversionSheet addConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		int length = sheets.length;
		ConversionSheet sheet = ConversionSheetFactory.makeConversionSheet(
				configElement, type);
		int newPos = length;
		for (int i = 0; i < length; i++) {
			if (sheets[i].getVersion() < sheet.getVersion())
				continue;
			else if (sheets[i].getVersion() > sheet.getVersion()) {
				newPos = i;
				break;
			} else {
				throw new IllegalStateException(
						"Conversion sheet not unique (version "
								+ sheet.getVersion() + ", rootType: " + type
								+ ")");
			}

		}
		ConversionSheet[] ns = new ConversionSheet[length + 1];
		System.arraycopy(sheets, 0, ns, 0, newPos);
		System.arraycopy(sheets, newPos, ns, newPos + 1, length - newPos);
		sheets = ns;
		sheets[newPos] = sheet;

		return sheet;
	}

	public ConversionSheet[] getConversionSheets() {
		return sheets.clone();
	}

}
