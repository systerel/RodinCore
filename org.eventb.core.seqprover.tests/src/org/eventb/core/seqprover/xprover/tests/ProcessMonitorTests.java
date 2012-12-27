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
package org.eventb.core.seqprover.xprover.tests;

import static java.lang.Runtime.getRuntime;
import static java.util.Arrays.asList;
import static org.eclipse.core.runtime.Platform.OS_LINUX;
import static org.eclipse.core.runtime.Platform.OS_MACOSX;
import static org.eventb.core.seqprover.xprover.ProcessMonitor.wrapCommand;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

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

	private static final String friendCmd = BundledFileExtractor.extractFile(
			bundle, new Path("$os$/friend"), true).toOSString();

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
		assertStreamContents(output, mon.output());
		assertStreamContents(error, mon.error());
	}

	private static void assertStreamContents(String expected, byte[] bytes) {
		if (expected != null) {
			assertEquals(expected, new String(bytes));
		}
	}

	private static final boolean WITH_WRAPPING;
	static {
		final String os = Platform.getOS();
		WITH_WRAPPING = OS_LINUX.equals(os) || OS_MACOSX.equals(os);
	}

	/**
	 * Exit code of a process killed with SIGTERM on Unix (or simply killed on
	 * Windows).
	 */
	private static final int RECEIVED_SIGTERM = WITH_WRAPPING ? 143 : 1;

	/**
	 * Exit code of a process killed with SIGKILL on Unix (or simply killed on
	 * Windows).
	 */
	private static final int RECEIVED_SIGKILL = WITH_WRAPPING ? 137 : 1;

	private static class Timeout implements Cancellable {

		private volatile boolean cancelled;

		public Timeout(long timeout) {
			new Timer(true).schedule(new TimerTask() {
				@Override
				public void run() {
					cancelled = true;
				}
			}, timeout);
		}

		public boolean isCancelled() {
			return cancelled;
		}
	}

	/*
	 * Cancels after 500 ms, leaving time for the OS to launch a program.
	 */
	private static Cancellable timeout() {
		return new Timeout(500); // ms
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

	/**
	 * Ensures that commands are properly wrapped on appropriate platforms.
	 */
	@Test
	public void wrapping() throws Exception {
		final String[] cmd = { "foo", "bar" };
		final String[] actual = wrapCommand(cmd);
		if (WITH_WRAPPING) {
			assertEquals(cmd.length + 1, actual.length);
			assertEquals(asList(cmd), asList(actual).subList(1, actual.length));
		} else {
			assertArrayEquals(cmd, actual);
		}
	}

	/**
	 * Ensures that a long running process which is cooperative is terminated
	 * with signal SIGTERM on Unix (and simply terminated on Windows).
	 */
	@Test(timeout = 2000)
	public void friendly() throws Exception {
		final String[] cmdArray = { friendCmd };
		final Process process = getRuntime().exec(wrapCommand(cmdArray));
		final ProcessMonitor mon = new ProcessMonitor(null, process, timeout());
		assertProcessResult(mon, RECEIVED_SIGTERM, null, null);
	}

	/**
	 * Ensures that a long running process which is not cooperative is
	 * terminated with signal SIGKILL on Unix (and simply terminated on
	 * Windows).
	 */
	@Test(timeout = 2000)
	public void unfriendly() throws Exception {
		final String[] cmdArray = { friendCmd, "some arg" };
		final Process process = getRuntime().exec(wrapCommand(cmdArray));
		final ProcessMonitor mon = new ProcessMonitor(null, process, timeout());
		assertProcessResult(mon, RECEIVED_SIGKILL, null, null);
	}

}
