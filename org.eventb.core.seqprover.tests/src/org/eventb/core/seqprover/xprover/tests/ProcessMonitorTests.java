/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.eventb.core.seqprover.xprover.Cancellable;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * Unit tests for class ProcessMonitor.
 * 
 * @author Laurent Voisin
 */
public class ProcessMonitorTests {

	private static final String BUNDLE_NAME = "org.eventb.core.seqprover.tests";
	private static final Bundle bundle = Platform.getBundle(BUNDLE_NAME);

	private static final IPath progLocalPath = new Path("$os$/simple");
	private static final String cmd = BundledFileExtractor.extractFile(bundle,
			progLocalPath, true).toOSString();

	private static final IPath dataLocalPath = new Path("lib/data.txt");
	private static final String dataFilename = BundledFileExtractor
			.extractFile(bundle, dataLocalPath, false).toOSString();

	private static Cancellable notCancelled = new Cancellable() {
		public boolean isCancelled() {
			return false;
		}
	};

	private static Cancellable cancelled = new Cancellable() {
		public boolean isCancelled() {
			return true;
		}
	};

	private static final String data = readData();

	private static String readData() {
		final String lineSep = Platform.getOS().equals(Platform.OS_WIN32) ? "\r\n"
				: "\n";
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					dataFilename));
			final StringBuilder builder = new StringBuilder();
			String s;
			while ((s = reader.readLine()) != null) {
				builder.append(s);
				builder.append(lineSep);
			}
			reader.close();
			return builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static Process launch(String... args) throws IOException {
		final int length = args.length;
		final String[] cmdArray = new String[length + 1];
		cmdArray[0] = cmd;
		System.arraycopy(args, 0, cmdArray, 1, length);
		return Runtime.getRuntime().exec(cmdArray);
	}

	private static void assertProcessResult(ProcessMonitor mon, int expCode,
			String output, String error) {

		final int exitCode = mon.exitCode();
		if (expCode < 0) {
			// Special case for don't care exit code
			if (exitCode == 0) {
				fail("Expected failure but got a 0 exit code.");
			}
		} else {
			assertEquals("Unexpected exit value", expCode, exitCode);
		}
		assertEquals(output, new String(mon.output()));
		assertEquals(error, new String(mon.error()));
	}

	/**
	 * Ensures that a successful process can be monitored and the exit value
	 * retrieved.
	 */
	@Test
	public void exitSuccess() throws Exception {
		Process process = launch();
		ProcessMonitor mon = new ProcessMonitor(null, process, notCancelled);
		assertProcessResult(mon, 0, "", "");
	}

	/**
	 * Ensures that a failed process can be monitored and the exit value
	 * retrieved.
	 */
	@Test
	public void exitFailure() throws Exception {
		Process process = launch("error");
		ProcessMonitor mon = new ProcessMonitor(null, process, notCancelled);
		assertProcessResult(mon, 1, "", "");
	}

	/**
	 * Ensures that the output of a monitored process can be read.
	 */
	@Test
	public void outputFromFile() throws Exception {
		Process process = launch("-o", dataFilename);
		ProcessMonitor mon = new ProcessMonitor(null, process, notCancelled);
		assertProcessResult(mon, 0, data, "");
	}

	/**
	 * Ensures that the input of a monitored process is actually readable by the
	 * process.
	 */
	@Test
	public void input() throws Exception {
		Process process = launch("-o", "-");
		InputStream input = new ByteArrayInputStream(data.getBytes());
		ProcessMonitor mon = new ProcessMonitor(input, process, notCancelled);
		assertProcessResult(mon, 0, data, "");
	}

	/**
	 * Ensures that the error of a monitored process can be read.
	 */
	@Test
	public void errorFromFile() throws Exception {
		Process process = launch("-e", dataFilename);
		ProcessMonitor mon = new ProcessMonitor(null, process, notCancelled);
		assertProcessResult(mon, 0, "", data);
	}

	/**
	 * Ensures that a long running process can be cancelled.
	 */
	@Test
	public void cancel() throws Exception {
		Process process = launch("-s");
		ProcessMonitor mon = new ProcessMonitor(null, process, cancelled);
		assertProcessResult(mon, -1, "", "");
	}

}
