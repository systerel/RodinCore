/*
 * Created on 03-may-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eventb.internal.core.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;

/**
 * This class maps the JFlex lexer with the Coco/R scanner. Note that
 * the lexer returns the first recognized token of a string.
 * It respects the specification of a Coco/R scanner, described in the Coco/R user manual.
 * 
 * @author François Terrier
 */
public class Scanner {
	// list of the tokens which were looked-ahead
	private List<Token> list = new Vector<Token>();

	// iterator on the look-ahead list
	private ListIterator<Token> iterator = list.listIterator();

	private Lexer lexer = null;

	/**
	 * Creates a new scanner that takes its input from <code>str</code>.
	 * 
	 * @param str the string to read from.
	 * @param result the result of this scan and parse
	 */
	public Scanner(String str, ParseResult result) {
		lexer = new Lexer(new StringReader(str));
		lexer.result = result;
	}
	
	private Token getNextToken() {
		try {
			return lexer.next_token();
		} catch (IOException e) {
			// Can never happen when reading from a String.
			assert(false);
			return null;
		}
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
	
	public static boolean isValidIdentifierName(FormulaFactory factory,
			String name) {
		// just to get problems
		final ParseResult result = new ParseResult(factory,
				LanguageVersion.LATEST, name);

		final Scanner scanner = new Scanner(name, result);

		final Token token = scanner.Peek();
		return (!result.hasProblem() && token != null
				&& token.kind == Parser._IDENT && token.val.equals(name));
	}

}