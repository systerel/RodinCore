/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.xprover;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Asynchronously read all data from the given input stream and send it to the
 * output stream.
 * 
 * The design of this class is strongly inspired from a similar class in Apache
 * Ant, version 1.6.5.
 * 
 * @author Laurent Voisin
 */
public class StreamPumper extends Thread {
	
    private static final int SIZE = 128;
    private final InputStream inputStream;
	private final OutputStream outputStream;
	
	/**
	 * Creates a new stream pumper for the given input stream.
	 * 
	 * @param threadName name of this thread
	 * @param is input stream to read from
	 * @param os output stream to write to
	 */
	public StreamPumper(String threadName, InputStream is, OutputStream os) {
		super(threadName);
		this.setDaemon(true);
		this.inputStream = is;
		this.outputStream = os;
	}

	private void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			// Ignore
		}
	}
	
	@Override
	public void run() {
        final byte[] buf = new byte[SIZE];

        int length;
        try {
            while ((length = inputStream.read(buf)) > 0) {
            	outputStream.write(buf, 0, length);
            }
        } catch (IOException e) {
            // ignore errors
        } finally {
            close(inputStream);
            close(outputStream);
        }
	}
}
