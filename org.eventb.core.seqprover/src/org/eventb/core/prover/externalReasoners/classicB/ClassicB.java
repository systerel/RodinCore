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
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

/**
 * @author halstefa
 *
 */
public class ClassicB {
	
	private static String iName;
	private static String oName;
	
	@SuppressWarnings("unused")
	private static ClassicB b = new ClassicB();
	
	private ClassicB() {
		try {
			iName = File.createTempFile("eventbin", null).getCanonicalPath();
			oName = File.createTempFile("eventbou", null).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static StringBuffer translateSequent(ITypeEnvironment typeEnvironment, Set<Predicate> hypothesis, Predicate goal) {
		StringBuffer result = new StringBuffer();
		SyntaxVisitor visitor = new SyntaxVisitor();
		FormulaFactory factory = FormulaFactory.getDefault();
		if (typeEnvironment != null) {
			for (String name : typeEnvironment.getNames()) {
				typeEnvironment.getType(name).toExpression(factory).accept(visitor);
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
	
	public static boolean callPKforPP(StringBuffer input) throws IOException, InterruptedException {
		printPP(input);
		String callString = ProverShell.getDefault().getCommandForPK() + iName;
		Process process = Runtime.getRuntime().exec(callString);
		int i = process.waitFor();
		String result = new BufferedReader(new InputStreamReader(process.getErrorStream())).readLine();
		return result == null && i == 0;
	}
	
	public static boolean proveWithPP(StringBuffer input) throws IOException, InterruptedException {
		printPP(input);
		String callString = ProverShell.getDefault().getCommandForPP() + iName;
		Process process = Runtime.getRuntime().exec(callString);
		int i = process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(oName)));
		CharBuffer buffer = CharBuffer.allocate(32);
		buffer.put(0, 'E');
		@SuppressWarnings("unused") int n = reader.read(buffer);
		return buffer.get(0) == 'S' && i == 0;
	}
	
//	public static boolean callPK(StringBuffer input) throws IOException, InterruptedException {
//		File ifile = File.createTempFile("eventbin", null);
//		File ofile = File.createTempFile("eventbou", null);
//		PrintStream stream = new PrintStream(ifile);
//		stream.printf("Flag(FileOn(\"%s\")) & Set(toto | ", ofile.getCanonicalPath());
//		stream.print(input);
//		stream.print(" )");
//		stream.println();
//		String callString = "/home/halstefa/bin/pk -s /home/halstefa/bin/PP_ST " + ifile.getCanonicalPath();
//		Process process = Runtime.getRuntime().exec(callString);
//		int i = process.waitFor();
//		String result = new BufferedReader(new InputStreamReader(process.getErrorStream())).readLine();
//		return result == null;
//	}
	
	private static void printML(StringBuffer input) throws IOException {
		PrintStream stream = new PrintStream(iName);
		stream.print("THEORY Lemma;Unproved IS ");
		stream.print(input);
		stream.print("WHEN Force IS (0;1;2;3) WHEN FileOut IS \"");
		stream.print(oName);
		stream.print("\" WHEN Options IS ? & ? & ? & OK & \"\" & MaxNumberOfUniversalHypothesisInstantiation & KO END");
		stream.println();
	}
	
	public static boolean callPKforML(StringBuffer input)  throws IOException, InterruptedException {
		printML(input);
		String callString = ProverShell.getDefault().getCommandForPK() + iName;
		Process process = Runtime.getRuntime().exec(callString);
		int i = process.waitFor();
		String result = new BufferedReader(new InputStreamReader(process.getErrorStream())).readLine();
		return result == null && i == 0;
	}
	
	public static boolean proveWithML(StringBuffer input)  throws IOException, InterruptedException {
		printML(input);
		String callString = ProverShell.getDefault().getCommandForML() + iName;
		Process process = Runtime.getRuntime().exec(callString);
		int i = process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(oName)));
		CharBuffer buffer = CharBuffer.allocate(32);
		buffer.put(15, 'U');
		@SuppressWarnings("unused") int n = reader.read(buffer);
		return buffer.get(15) == 'P' && i == 0;
	}
	
}
