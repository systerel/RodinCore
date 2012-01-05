/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added command wrapping utility method
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.xprover.StreamPumper;
import org.osgi.framework.Bundle;

/**
 * Utility class for monitoring an external process.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class ProcessMonitor {

	private static final IPath LOCAL_WRAPPER_PATH = new Path("shell/wrapper.sh");

	private static boolean needsWrapper() {
		final String os = Platform.getOS();
		return Platform.OS_LINUX.equals(os) || Platform.OS_MACOSX.equals(os);
	}

	private static final String wrapperPath;
	static {
		if (needsWrapper()) {
			final Bundle bundle = Platform.getBundle(SequentProver.PLUGIN_ID);
			final IPath path = BundledFileExtractor.extractFile(bundle,
					LOCAL_WRAPPER_PATH, true);
			wrapperPath = path != null ? path.toOSString() : null;
		} else {
			wrapperPath = "";
		}
	}

	/**
	 * Wraps the given command through a shell-script so that interruption is
	 * properly handled.
	 * 
	 * @param command
	 *            a command to wrap
	 * @return the wrapped command, or the given command if wrapper is not
	 *         needed.
	 * @since 2.4
	 */
	public static String[] wrapCommand(String... command) {
		if (needsWrapper()) {
			final int length = command.length;
			final String[] wrapped = new String[length + 1];
			wrapped[0] = wrapperPath;
			System.arraycopy(command, 0, wrapped, 1, length);
			return wrapped;
		} else {
			return command;
		}
	}
	
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
						if (AbstractXProverReasoner.DEBUG) {
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
				if (AbstractXProverReasoner.DEBUG) {
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
