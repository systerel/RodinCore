/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class Converter {
	
	private ConversionSheet[] sheets = new ConversionSheet[0];
	
	public byte[] convert(byte[] source, long currentVersion, long targetVersion) throws RodinDBException {
		final int start = find(currentVersion);
		final int end = find(targetVersion) - 1;
		byte[] result = source;
		if (start <= end) {
			for (int i=start; i<= end; i++) {
				result = computeConversion(result, i);
			}
		}
		return result;
	}
	
	private int find(long currentVersion) {
		for (int i=0; i<sheets.length; i++) {
			if (sheets[i].getVersion() > currentVersion)
				return i;
		}
		return sheets.length;
	}

	private byte[] computeConversion(byte[] source, int index) throws RodinDBException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(source);
		StreamSource s = new StreamSource(inputStream);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(source.length);
		StreamResult r = new StreamResult(outputStream);
		Transformer transformer = sheets[index].getTransformer();
		try {
			transformer.transform(s , r);
		} catch (TransformerException e) {
			throw new RodinDBException(e, IRodinDBStatusConstants.CONVERSION_ERROR);
		}
		return outputStream.toByteArray();
	}
	
	ConversionSheet addConversionSheet(
			IConfigurationElement configElement, 
			IFileElementType<IRodinFile> type) {
		int length = sheets.length;
		ConversionSheet sheet = ConversionSheetFactory.makeConversionSheet(configElement, type);
		int newPos = length;
		for (int i=0; i<length; i++) {
			if (sheets[i].getVersion() < sheet.getVersion())
				continue;
			else if (sheets[i].getVersion() > sheet.getVersion()) {
				newPos = i;
				break;
			} else {
				throw new IllegalStateException(
						"Conversion sheet not unique (version " + 
						sheet.getVersion() + ", rootType: " + type + ")");
			}
			
		}
		ConversionSheet[] ns = new ConversionSheet[length+1];
		System.arraycopy(sheets, 0, ns, 0, newPos);
		System.arraycopy(sheets, newPos, ns, newPos+1, length-newPos);
		sheets = ns;
		sheets[newPos] = sheet;
		
		return sheet;
	}

	public ConversionSheet[] getConversionSheets() {
		return sheets.clone();
	}
		
}
