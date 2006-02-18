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
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * @author halstefa
 *
 */
public abstract class ClassicB {
	
	private static final String ML_SUCCESS = "THEORY Etat IS Proved(0) END\n";
	private static final String PP_SUCCESS = "SUCCES";
	
	private static String iName;
	private static String oName;
	
	private static void makeTempFileNames() throws IOException {
		if (iName != null) {
			// already done
			return;
		}
		iName = File.createTempFile("eventbin", null).getCanonicalPath();
		oName = File.createTempFile("eventbou", null).getCanonicalPath();
	}
	
	public static StringBuffer translateSequent(
			ITypeEnvironment typeEnvironment, Set<Predicate> hypothesis,
			Predicate goal) {
		
		StringBuffer result = new StringBuffer();
		SyntaxVisitor visitor = new SyntaxVisitor();
		FormulaFactory factory = FormulaFactory.getDefault();
		if (typeEnvironment != null) {
			for (String name : typeEnvironment.getNames()) {
				final Type type = typeEnvironment.getType(name);
				type.toExpression(factory).accept(visitor);
				result.append(name + " : " + visitor.getString() + " & ");
				visitor.clear();
			}
		}
		boolean first = true;
		for(Predicate predicate : hypothesis) {
			predicate.accept(visitor);
			if(first)
				first = false;
			else
				result.append(" & ");
			result.append(visitor.getString());
			visitor.clear();
		}
		result.append(" => ");
		goal.accept(visitor);
		result.append(visitor.getString());
		return result;
	}
	
	private static void printPP(StringBuffer input) throws IOException {
		PrintStream stream = new PrintStream(iName);
		stream.printf("Flag(FileOn(\"%s\")) & Set(toto | ", oName);
		stream.print(input);
		stream.print(" )");
		stream.println();
	}
	
	public static boolean callPKforPP(StringBuffer input)
	throws IOException, InterruptedException {
		
		if (! ProverShell.areToolsPresent())
			return false;
		makeTempFileNames();
		printPP(input);
		return runPK(ProverShell.getPPParserCommand(iName));
	}
	
	public static boolean proveWithPP(StringBuffer input)
	throws IOException, InterruptedException {
		
		if (! ProverShell.areToolsPresent())
			return false;
		makeTempFileNames();
		printPP(input);
		final String[] cmdArray = ProverShell.getPPCommand(iName);
		final Process process = Runtime.getRuntime().exec(cmdArray);
		process.waitFor();
		// showOutput();
		return checkResult(PP_SUCCESS);
	}
	
	private static void printML(StringBuffer input) throws IOException {
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
		makeTempFileNames();
		printML(input);
		return runPK(ProverShell.getMLParserCommand(iName));
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
	
	public static boolean proveWithML(StringBuffer input)
	throws IOException, InterruptedException {
		
		if (! ProverShell.areToolsPresent())
			return false;

		makeTempFileNames();
		printML(input);
		String[] cmdArray = ProverShell.getMLCommand(iName);
		Process process = Runtime.getRuntime().exec(cmdArray);
		process.waitFor();
		// showOutput();
		return checkResult(ML_SUCCESS);
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
