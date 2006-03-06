/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.externalReasoners.classicB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * @author halstefa
 *
 */
public abstract class ClassicB {
	
	private static final String ML_SUCCESS = "THEORY Etat IS Proved(0) END";
	private static final String PP_SUCCESS = "SUCCES";
	
	public static final long DEFAULT_DELAY = 5000;
	
	public static final long DEFAULT_PERIOD = 317;
	
	private static String iName;
	private static String oName;
	
	private static class ProverTimeout extends TimerTask {
		
		private final Thread thread;
		
		public ProverTimeout(Thread thread) {
			this.thread = thread;
		}
		
		@Override
		public void run() {
			thread.interrupt();
		}
		
	}
	
	private static class ProverCheckCancelled extends TimerTask {
		
		private final Thread thread;
		private final IProgressMonitor monitor;
		
		public ProverCheckCancelled(Thread thread, IProgressMonitor monitor) {
			this.thread = thread;
			this.monitor = monitor;
		}
		
		@Override
		public void run() {
			if(monitor.isCanceled())
				thread.interrupt();
		}
		
	}
	
	private static void makeTempFileNames() throws IOException {
		if (iName != null) {
			// already done
			return;
		}
		iName = File.createTempFile("eventbin", null).getCanonicalPath();
		oName = File.createTempFile("eventbou", null).getCanonicalPath();
	}
	
	public static StringBuffer translateSequent(
			ITypeEnvironment typeEnvironment, Predicate[] hypotheses,
			Predicate goal) {
		
		StringBuffer result = new StringBuffer();
		SyntaxVisitor visitor = new SyntaxVisitor();
		FormulaFactory factory = FormulaFactory.getDefault();
		if (typeEnvironment != null) {
			boolean first = true;
			for (String name : typeEnvironment.getNames()) {
				final Type type = typeEnvironment.getType(name);
				type.toExpression(factory).accept(visitor);
				if (first)
					first = false;
				else
					result.append(" & ");
				result.append(visitor.getRenamedIdentName(name));
				result.append(" : ");
				result.append(visitor.getString());
				visitor.clear();
			}
		}
		boolean first = (typeEnvironment == null || typeEnvironment.isEmpty());
		for (Predicate predicate : hypotheses) {
			predicate.accept(visitor);
			if (first)
				first = false;
			else
				result.append(" & ");
			result.append(visitor.getString());
			visitor.clear();
		}
		if (result.length() != 0)
			result.append(" => ");
		goal.accept(visitor);
		result.append(visitor.getString());
		return result;
	}
	
	private static void printPP(StringBuffer input) throws IOException {
		PrintStream stream = new PrintStream(iName);
		stream.printf("Flag(FileOn(\"%s\")) & Set(toto | ", oName);
		stream.print(input);
		stream.println(" )");
		stream.close();
	}
	
	// Fills the output file with some random characters that can not be
	// considered as a success.
	private static void printDefaultOutput() throws IOException {
		PrintStream stream = new PrintStream(oName);
		stream.println("FAILED");
		stream.close();
	}

	// Removes temporary files
	private static void cleanup() {
		new File(iName).delete();
		new File(oName).delete();
	}
	
	public static boolean callPKforPP(StringBuffer input)
	throws IOException, InterruptedException {
		
		if (! ProverShell.areToolsPresent())
			return false;
		try {
			makeTempFileNames();
			printPP(input);
			return runPK(ProverShell.getPPParserCommand(iName));
		} finally {
			cleanup();
		}
	}
	
	public static boolean proveWithPP(StringBuffer input, long delay)
	throws IOException {
		return proveWithPP(input, delay, null);
	}

	public static boolean proveWithPP(StringBuffer input, long delay, IProgressMonitor monitor)
	throws IOException {
		
		if (! ProverShell.areToolsPresent())
			return false;
		try {
			makeTempFileNames();
			printPP(input);
			printDefaultOutput();
			final String[] cmdArray = ProverShell.getPPCommand(iName);
			return callProver(cmdArray, delay, PP_SUCCESS, monitor);
		} finally {
			cleanup();
		}
	}
	
	private static void printML(String input) throws IOException {
		PrintStream stream = new PrintStream(iName);
		stream.println("THEORY Lemma;Unproved IS");
		stream.println(input);
		stream.print("WHEN Force IS (0;1;2;3) WHEN FileOut IS \"");
		stream.print(oName);
		stream.println("\"");
		stream.println("WHEN Options IS ? & ? & ? & OK & \"\" & dummy & KO");
		stream.println("END");
		stream.close();
	}
	
	public static boolean callPKforML(StringBuffer input)
	throws IOException, InterruptedException {
		
		if (! ProverShell.areToolsPresent())
			return false;
		try {
			makeTempFileNames();
			printML(patchSequentForML(input.toString()));
			return runPK(ProverShell.getMLParserCommand(iName));
		} finally {
			cleanup();
		}
	}

	private static boolean runPK(final String[] cmdArray)
	throws IOException, InterruptedException {
		
		final Process process = Runtime.getRuntime().exec(cmdArray);
		final int status = process.waitFor();
		final InputStream error = process.getErrorStream();
		final InputStreamReader errorReader = new InputStreamReader(error);
		final String result = new BufferedReader(errorReader).readLine();
		return result == null && status == 0;
	}
	
	private static String patchSequentForML(String sequent) {
		String moinsE = sequent.replace("_moinsE", "-");
		String multE = moinsE.replace("_multE", "*");
		return multE;
	}
	
	public static boolean proveWithML(StringBuffer input)
	throws IOException, InterruptedException {
		return proveWithML(input, DEFAULT_DELAY);
	}
	
	private static boolean callProver(String[] cmdArray, long delay, String successMsg, IProgressMonitor monitor) 
	throws IOException {
		
		Process process = null;
		try {
			Timer timer = new Timer();
			if (delay >0) {
				timer.schedule(new ProverTimeout(Thread.currentThread()), delay);
			}
			if (monitor != null) {
				timer.schedule(new ProverCheckCancelled(Thread.currentThread(), monitor), DEFAULT_PERIOD, DEFAULT_PERIOD);
			}
			process = Runtime.getRuntime().exec(cmdArray);
			process.waitFor();
			timer.cancel();
			// showOutput();
			return checkResult(successMsg);
		} catch (InterruptedException e) {
			return checkResult(successMsg);
		} finally {
			// clear interrupted status			
			Thread.currentThread().isInterrupted();
			if (process != null)
				process.destroy();
		}
	}
	
	public static boolean proveWithML(StringBuffer input, long delay)
	throws IOException {
		return proveWithML(input, delay, null);
	}

	public static boolean proveWithML(StringBuffer input, long delay, IProgressMonitor monitor)
	throws IOException {
		
		if (! ProverShell.areToolsPresent())
			return false;

		try {
			makeTempFileNames();
			printML(patchSequentForML(input.toString()));
			printDefaultOutput();
			final String[] cmdArray = ProverShell.getMLCommand(iName);
			return callProver(cmdArray, delay, ML_SUCCESS, monitor);
		} finally {
			cleanup();
		}
	}
	
	private static boolean checkResult(String expected) throws IOException {
		final int length = expected.length();
		final InputStream is = new FileInputStream(oName);
		final InputStreamReader isr = new InputStreamReader(is);
		final char[] cbuf = new char[length];
		final int count = isr.read(cbuf);
		if (count < length)
			return false;
		final String actual = new String(cbuf, 0, count);
		return expected.equals(actual);
	}
	
	// For debugging purpose
	@SuppressWarnings("unused")
	private static void showOutput() throws IOException {
		InputStream is = new FileInputStream(oName);
		InputStreamReader isr = new InputStreamReader(is);
		char[] cbuf = new char[1024];
		int count = isr.read(cbuf);
		System.out.println("Read '" + new String(cbuf, 0 , count) + "'");
		
	}
	
}
