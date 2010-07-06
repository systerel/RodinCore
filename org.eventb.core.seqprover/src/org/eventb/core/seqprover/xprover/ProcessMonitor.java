/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.eventb.internal.core.seqprover.xprover.StreamPumper;

/**
 * Utility class for monitoring an external process.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class ProcessMonitor {

	private static final int DEFAULT_SIZE = 1024;
	private static final long DEFAULT_PERIOD = 317;

	private final Process process;
	
	private final ByteArrayOutputStream outputStream;
	private byte[] output;

	private final ByteArrayOutputStream errorStream;
	private byte[] error;
	
	/**
	 * Creates a new process launcher with the given input stream and command
	 * line.
	 * 
	 * @param input
	 * 	  stream containing the input of the process (might be <code>null</code>)
	 * @param  process
	 *    a process
	 * @param cancellable
	 *    a cancellable task
	 */
	public ProcessMonitor(InputStream input, final Process process,
			final Cancellable cancellable) {

		this.process = process;
		
		final StreamPumper inputPumper;
		if (input != null) {
			inputPumper = new StreamPumper("inputPumper", input, process
					.getOutputStream());
			inputPumper.start();
		} else {
			inputPumper = null;
			try {
				process.getOutputStream().close();
			} catch (IOException e) {
				// ignore
			}
		}

		outputStream = new ByteArrayOutputStream(DEFAULT_SIZE);
		final StreamPumper outputPumper = new StreamPumper("outputPumper",
				process.getInputStream(), outputStream);
		outputPumper.start();

		errorStream = new ByteArrayOutputStream(DEFAULT_SIZE);
		final StreamPumper errorPumper = new StreamPumper("errorPumper",
				process.getErrorStream(), errorStream);
		errorPumper.start();

		final Timer timer = new Timer(true);
		try {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (cancellable.isCancelled()) {
						process.destroy();
						if (XProverReasoner.DEBUG) {
							System.out.println("Destroying external prover.");
						}
					}
				}
			}, DEFAULT_PERIOD, DEFAULT_PERIOD);

			process.waitFor();
			if (inputPumper != null) 
				inputPumper.join();
			outputPumper.join();
			errorPumper.join();
		} catch (InterruptedException e) {
			// ignore, process will be destroyed
		} finally {
			timer.cancel();
			destroyAndWaitForProcess(process);
		}
	}

	private void destroyAndWaitForProcess(Process process) {
		// Ensures that the process has indeed terminated
		while (true) {
			try {
				process.exitValue();
				if (XProverReasoner.DEBUG) {
					System.out.println("External prover is now terminated.");
				}
				break;
			} catch (IllegalThreadStateException e) {
				// Ignore, process has not yet terminated
			}
			process.destroy();
			try {
				Thread.sleep(DEFAULT_PERIOD);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public int exitCode() {
		return process.exitValue();
	}
	
	public synchronized byte[] output() {
		if (output == null) {
			output = outputStream.toByteArray();
		}
		return output;
	}
	
	public synchronized byte[] error() {
		if (error == null) {
			error = errorStream.toByteArray();
		}
		return error;
	}

}
