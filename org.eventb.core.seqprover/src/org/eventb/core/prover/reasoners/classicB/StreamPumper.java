/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.reasoners.classicB;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Asynchronously read all data from the given input stream. In debugging mode,
 * data is stored in an array of bytes, and can later be obtained. In normal
 * mode, data is just discarded.
 * 
 * The design of this class is strongly inspired from a similar class in Apache
 * Ant, version 1.6.5.
 * 
 * @author Laurent Voisin
 */
public class StreamPumper extends Thread {
	
    private static final int SIZE = 128;

    private final InputStream inputStream;
	
	private final ByteArrayOutputStream outputStream;

	/**
	 * Creates a new stream pumper for the given input stream.
	 * 
	 * @param is input stream to read from
	 * @param debug if <code>true</code>, data won't be discarded
	 */
	public StreamPumper(InputStream is, boolean debug) {
		this.setDaemon(true);
		this.inputStream = is;
		if (debug) {
			outputStream = new ByteArrayOutputStream();
		} else {
			outputStream = null;
		}
	}

	@Override
	public void run() {
        final byte[] buf = new byte[SIZE];

        int length;
        try {
            while ((length = inputStream.read(buf)) > 0) {
            	if (outputStream != null) {
            		outputStream.write(buf, 0, length);
            	}
            }
        } catch (Exception e) {
            // ignore errors
        }
	}
	
	/**
	 * Returns the data read from the stream.
	 * 
	 * @return the data read from the stream.
	 */
	public String getData() {
		assert outputStream != null;
		return outputStream.toString();
	}

}
