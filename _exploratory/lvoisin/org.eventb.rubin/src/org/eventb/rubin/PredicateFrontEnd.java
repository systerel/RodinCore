/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.rubin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eventb.internal.rubin.Parser;
import org.eventb.internal.rubin.Scanner;

/**
 * API class for the Predicate Calculus Front-End.
 * 
 * @author Laurent Voisin
 */
public abstract class PredicateFrontEnd {

	public static Sequent[] parseString(String input) {
		final char[] charArray = input.toCharArray();
		final byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		InputStream stream = new ByteArrayInputStream(byteArray); 
		Scanner scanner = new Scanner(stream);
		Parser parser = new Parser(scanner);
		parser.Parse();
		return parser.getResult();
	}
	
	public static Sequent[] parseFile(String fileName) {
		Scanner scanner = new Scanner(fileName);
		Parser parser = new Parser(scanner);
		parser.Parse();
		return parser.getResult();
	}
	
}
