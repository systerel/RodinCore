/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.lexer;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.internal.core.lexer.GenScan.ScanState;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.ParseResult;

/**
 * This class maps the JFlex lexer with the Coco/R scanner. Note that the lexer
 * returns the first recognized token of a string. It respects the specification
 * of a Coco/R scanner, described in the Coco/R user manual.
 * 
 * @author François Terrier
 * 
 * FIXME update comment
 */
public class Scanner {
	
	// list of the tokens which were looked-ahead
	private final List<Token> list = new Vector<Token>();

	// iterator on the look-ahead list
	private ListIterator<Token> iterator = list.listIterator();

	private GenScan lexer = null;
	
	/**
	 * Creates a new scanner that takes its input from <code>str</code>.
	 * 
	 * @param str
	 *            string to read from.
	 * @param result
	 *            result of this scan and parse
	 * @param grammar
	 *            grammar defining tokens to recognize
	 */
	public Scanner(String str, ParseResult result, AbstractGrammar grammar) {
		lexer = GenScan.getLexer(grammar);
		lexer.parse(str);
		lexer.result = result;
	}

	private Token getNextToken() {
		return lexer.nextToken();
	}

	// Returns the next token.
	public Token Scan() {
		ResetPeek();
		if (iterator.hasNext()) {
			Token temp = iterator.next();
			iterator.remove();
			return temp;
		} else {
			return getNextToken();
		}
	}

	// Looks ahead the next token.
	protected Token Peek() {
		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			Token result = getNextToken();
			iterator.add(result);
			return result;
		}
	}

	protected void ResetPeek() {
		iterator = list.listIterator(0);
	}

	// Returns the lexer result.
	// Used by class Parser to share common problems (error reports).
	protected ParseResult getResult() {
		return lexer.result;
	}

	public static boolean isValidIdentifierName(
			FormulaFactory factory,
			String name) {
		// just to get problems
		final ParseResult result = new ParseResult(
				factory,
				LanguageVersion.LATEST,
				name);

		final Scanner scanner = new Scanner(name, result, factory.getGrammar());

		final Token token = scanner.Peek();
		return (!result.hasProblem() && token != null
				&& token.kind == BMath._IDENT && token.val.equals(name));
	}

	public ScanState save() {
		return lexer.save();
	}
	
	public void restore(ScanState state) {
		lexer.restore(state);
	}
}