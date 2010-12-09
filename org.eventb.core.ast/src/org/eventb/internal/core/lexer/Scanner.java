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

import static org.eventb.internal.core.parser.AbstractGrammar._EOF;
import static org.eventb.internal.core.parser.AbstractGrammar._IDENT;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.internal.core.lexer.GenLexer.LexState;
import org.eventb.internal.core.parser.AbstractGrammar;
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
	private final List<Token> list = new ArrayList<Token>();

	// iterator on the look-ahead list
	private ListIterator<Token> iterator = list.listIterator();

	private GenLexer lexer = null;
	
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
		lexer = new GenLexer(str, result, grammar);
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
				&& token.kind == _IDENT && token.val.equals(name));
	}

	public static class ScannerState {

		final LexState lexState;
		final List<Token> lookedAhead;

		public ScannerState(LexState lexState, List<Token> lookedAhead) {
			this.lexState = lexState;
			this.lookedAhead = new ArrayList<Token>(lookedAhead);
		}
		
	}
	
	public ScannerState save() {
		return new ScannerState(lexer.save(), list);
	}
	
	public void restore(ScannerState state) {
		lexer.restore(state.lexState);
		list.clear();
		// FIXME if Peek() has been called after the call to save() that
		// produced the given ScannerSate.lookedAhead, then peeked tokens will
		// be forgotten ! Moreover, if they have been peeked and consumed, they
		// are completely lost, because they are no more in the current list !
		// => either memorize all peeked tokens without ever erasing them (not
		// part of the saved/restored data), then save the current index
		// => or enforce a constraint not to call lookAheadFor more than once
		list.addAll(new ArrayList<Token>(state.lookedAhead));
	}

	public boolean lookAheadFor(int searchedKind) {
		ResetPeek();
		Token peek = Peek();
		while (peek.kind != _EOF) {
			if (peek.kind == searchedKind) {
				return true;
			}
			peek = Peek();
		}
		return false;
	}
}