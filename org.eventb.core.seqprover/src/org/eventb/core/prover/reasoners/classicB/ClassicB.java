/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.reasoners.classicB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	public static boolean DEBUG = false;
	
	private static final String ML_SUCCESS = "THEORY Etat IS Proved(0) END";
	private static final String PP_SUCCESS = "SUCCES";
	
	public static final long DEFAULT_DELAY = 5000;
	
	public static final long DEFAULT_PERIOD = 317;
	
	private static File iFile;
	private static File oFile;
	
	private static class ProverTimeout extends TimerTask {
		
		private final Process process;
		
		public ProverTimeout(Process process) {
			this.process = process;
		}
		
		@Override
		public void run() {
			process.destroy();
			if (ClassicB.DEBUG)
				System.out.println("External prover timeout reached.");
		}
		
	}
	
	private static class ProverCheckCancelled extends TimerTask {
		
		private final Process process;
		private final IProgressMonitor monitor;
		
		public ProverCheckCancelled(Process process, IProgressMonitor monitor) {
			this.process = process;
			this.monitor = monitor;
		}
		
		@Override
		public void run() {
			if (monitor.isCanceled()) {
				process.destroy();
				if (ClassicB.DEBUG)
					System.out.println("External prover cancelled by monitor.");
			}
		}
		
	}
	
	private static void makeTempFileNames() throws IOException {
		if (iFile != null) {
			// already done
			return;
		}
		iFile = File.createTempFile("eventbin", null);
		if (ClassicB.DEBUG)
			System.out.println("Created temporary input file '" + iFile + "'");
		oFile = File.createTempFile("eventbou", null);
		if (ClassicB.DEBUG)
			System.out.println("Created temporary output file '" + oFile + "'");
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
		PrintStream stream = new PrintStream(iFile);
		stream.printf("Flag(FileOn(\"%s\")) & Set(toto | ", oFile);
		stream.print(input);
		stream.println(" )");
		stream.close();
	}
	
	// Fills the output file with some random characters that can not be
	// considered as a success.
	private static void printDefaultOutput() throws IOException {
		PrintStream stream = new PrintStream(oFile);
		stream.println("FAILED");
		stream.close();
	}

	// Removes temporary files
	private static void cleanup() {
		iFile.delete();
		oFile.delete();
	}
	
	public static boolean callPKforPP(StringBuffer input)
	throws IOException, InterruptedException {
		
		if (! ProverShell.areToolsPresent())
			return false;
		try {
			makeTempFileNames();
			printPP(input);
			return runPK(ProverShell.getPPParserCommand(iFile));
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
		
		if (! ProverShell.areToolsPresent()) {
			if (ClassicB.DEBUG)
				System.out.println("Some B4free tool is absent.");
			return false;
		}
		try {
			makeTempFileNames();
			printPP(input);
			if (ClassicB.DEBUG) {
				System.out.println("Launching PP with input:\n" + input);
			}
			printDefaultOutput();
			final String[] cmdArray = ProverShell.getPPCommand(iFile);
			return callProver(cmdArray, delay, PP_SUCCESS, monitor);
		} finally {
			cleanup();
		}
	}
	
	private static void printML(String input, String forces) throws IOException {
		PrintStream stream = new PrintStream(iFile);
		stream.println("THEORY Lemma;Unproved IS");
		stream.println(input);
		stream.print("WHEN Force IS (");
		stream.print(forces);
		stream.print(") WHEN FileOut IS \"");
		stream.print(oFile);
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
			printML(patchSequentForML(input.toString()), "0");
			return runPK(ProverShell.getMLParserCommand(iFile));
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
	
	private static boolean callProver(String[] cmdArray, long delay, String successMsg, IProgressMonitor monitor) 
	throws IOException {

		if (ClassicB.DEBUG) {
			System.out.println("About to launch prover command:");
			System.out.print("   ");
			for (String arg: cmdArray) {
				System.out.print(' ');
				System.out.print(arg);
			}
			System.out.println();
			System.out.println("    delay: " + delay + "ms");
			System.out.println("    success is: " + successMsg);
		}
		
		ProcessBuilder builder = new ProcessBuilder(cmdArray);
		builder.redirectErrorStream(true);
		Process process = null;
		Timer timer = new Timer();
		StreamPumper pumper;
		try {
			process = builder.start();
			if (delay >0) {
				timer.schedule(new ProverTimeout(process), delay);
			}
			if (monitor != null) {
				timer.schedule(new ProverCheckCancelled(process, monitor), 
						0, DEFAULT_PERIOD);
			}
			process.getOutputStream().close();
			pumper = new StreamPumper(process.getInputStream(), ClassicB.DEBUG);
			pumper.start();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				process.destroy();
			}
			pumper.join();
			if (ClassicB.DEBUG) {
				System.out.println("-- Begin dump of process output --");
				final String output = pumper.getData();
				if (output.length() == 0 || output.endsWith("\n")) {
					System.out.print(output);
				} else {
					System.out.println(output);
				}
				System.out.println("-- End dump of process output --");
				System.out.println("Result file contains:");
				showOutput();
			}
			timer.cancel();
		} catch (InterruptedException e) {
			// just ignore
		} finally {
			// clear interrupted status			
			Thread.interrupted();
			if (process != null)
				process.destroy();
		}
		final boolean success = checkResult(successMsg);
		if (ClassicB.DEBUG)
				System.out.println("Prover " +
						(success ? "succeeded" : "failed"));
		return success;
	}
	
	public static boolean proveWithML(StringBuffer input, String forces,
			long delay) throws IOException {
		return proveWithML(input, forces, delay, null);
	}

	public static boolean proveWithML(StringBuffer input, String forces,
			long delay, IProgressMonitor monitor) throws IOException {
		
		if (! ProverShell.areToolsPresent())
			return false;

		try {
			makeTempFileNames();
			printML(patchSequentForML(input.toString()), forces);
			if (ClassicB.DEBUG) {
				System.out.println("Launching ML with input:\n" + input);
			}
			printDefaultOutput();
			final String[] cmdArray = ProverShell.getMLCommand(iFile);
			return callProver(cmdArray, delay, ML_SUCCESS, monitor);
		} finally {
			cleanup();
		}
	}
	
	private static boolean checkResult(String expected) throws IOException {
		final int length = expected.length();
		final FileReader isr = new FileReader(oFile);
		final char[] cbuf = new char[length];
		final int count = isr.read(cbuf);
		if (count < length)
			return false;
		final String actual = new String(cbuf, 0, count);
		return expected.equals(actual);
	}
	
	// For debugging purpose
	private static void showOutput() throws IOException {
		final FileReader isr = new FileReader(oFile);
		final char[] cbuf = new char[1024];
		int count = isr.read(cbuf);
		System.out.println(new String(cbuf, 0 , count));
	}
	
}
