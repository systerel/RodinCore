/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.lexer;

import static org.eventb.internal.core.lexer.GenLexer.LAMBDA;
import static org.eventb.internal.core.lexer.GenLexer.META;
import static org.eventb.internal.core.lexer.LexStream.nextCodePoint;
import static org.eventb.internal.core.parser.AbstractGrammar._EOF;
import static org.eventb.internal.core.parser.AbstractGrammar._IDENT;
import static org.eventb.internal.core.parser.AbstractGrammar._INTLIT;
import static org.eventb.internal.core.parser.BMath._PREDVAR;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.core.lexer.GenLexer.LexemReader;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.IndexedSet;

/**
 * Lexical classes for any {@link BMath} grammar (potentially extended).
 * 
 * @author Nicolas Beauger
 * 
 */
public enum LexicalClass {
	IDENTIFIER {

		private boolean isExcluded(int codePoint) {
			return codePoint == LAMBDA || codePoint == META;
		}

		@Override
		public boolean isStart(int codePoint) {
			return Character.isJavaIdentifierStart(codePoint)
					&& !isExcluded(codePoint);
		}

		@Override
		public boolean isPart(int codePoint) {
			return Character.isJavaIdentifierPart(codePoint)
					&& !isExcluded(codePoint);
		}

		@Override
		public int getKind(String image, AbstractGrammar grammar) {
			final int kind = grammar.getTokens().getIndex(image);
			if (kind == IndexedSet.NOT_AN_INDEX) {
				return _IDENT;
			} else {
				return kind;
			}
		}

	},
	SYMBOL {
		@Override
		public boolean isStart(int codePoint) {
			return !IDENTIFIER.isStart(codePoint)
					&& !WHITESPACE.isStart(codePoint)
					&& !INTEGER_LITERAL.isStart(codePoint)
					&& !META_VAR.isStart(codePoint);
		}

		@Override
		public boolean isPart(int codePoint) {
			return !IDENTIFIER.isPart(codePoint)
					&& !WHITESPACE.isPart(codePoint);
		}

		@Override
		public int getKind(String image, AbstractGrammar grammar) {
			return grammar.getTokens().getIndex(image);
		}

		@Override
		public boolean read(LexStream stream, AbstractGrammar grammar) {
			int lastAccepted = -1;
			if (isKnownSymbol(stream, grammar)) {
				lastAccepted = stream.getCurPos();
			}

			while (!stream.isEOF() && isPart(stream.curCodePoint())) {
				stream.goForward();
				if (isKnownSymbol(stream, grammar)) {
					lastAccepted = stream.getCurPos();
				}
			}
			if (lastAccepted == -1) {
				stream.resetCurPos();
				return false;
			}
			stream.setCurPos(lastAccepted);
			return true;
		}

		private boolean isKnownSymbol(LexStream stream, AbstractGrammar grammar) {
			return grammar.getTokens().contains(stream.getLexem());
		}

	},
	WHITESPACE {

		@Override
		public boolean isStart(int codePoint) {
			return isWhitespace(codePoint);
		}

		@Override
		public boolean isPart(int codePoint) {
			return isWhitespace(codePoint);
		}

		@Override
		public int getKind(String image, AbstractGrammar grammar) {
			assert false;
			return _EOF;
		}

	},
	INTEGER_LITERAL {
		@Override
		public boolean isStart(int codePoint) {
			return isDigit(codePoint);
		}

		@Override
		public boolean isPart(int codePoint) {
			return isDigit(codePoint);
		}

		@Override
		public int getKind(String image, AbstractGrammar grammar) {
			return _INTLIT;
		}
	},
	META_VAR {
		@Override
		public boolean isStart(int codePoint) {
			return codePoint == META;
		}

		@Override
		public boolean isPart(int codePoint) {
			// second character must be identifier start, then others
			// identifier part, thus requiring specific processing
			assert false;
			return false;
		}

		@Override
		public int getKind(String image, AbstractGrammar grammar) {
			return _PREDVAR;
		}

		@Override
		public boolean read(LexStream stream, AbstractGrammar grammar) {
			if (stream.isEOF()) {
				return false;
			}
			final LexemReader reader = new LexemReader(grammar);
			final int metaStart = stream.getTokenStart();
			final LexicalClass lexClass = reader.read(stream);
			stream.setTokenStart(metaStart);
			if (lexClass != LexicalClass.IDENTIFIER) {
				return false;
			}
			return true;
		}
	};

	protected static boolean isWhitespace(int codePoint) {
		return Character.isWhitespace(codePoint)
				|| FormulaFactory.isEventBWhiteSpace(codePoint);
	}

	protected static boolean isDigit(int codePoint) {
		return Character.isDigit(codePoint);
	}

	/**
	 * Returns whether the given code point is a valid first code point for a
	 * lexem of this lexical class.
	 * 
	 * @param codePoint
	 *            a code point
	 * @return <code>true</code> iff code point is valid as first of a lexem
	 */
	public abstract boolean isStart(int codePoint);

	/**
	 * Returns whether the given code point is a valid part of a lexem of this
	 * lexical class. The part here means from the second to the last code
	 * point.
	 * 
	 * @param codePoint
	 *            a code point
	 * @return <code>true</code> iff code point is valid as part of a lexem
	 */
	public abstract boolean isPart(int codePoint);

	/**
	 * Returns the grammatical kind of the given image. The given image MUST be
	 * a valid lexem of this lexical class.
	 * 
	 * @param lexem
	 *            a lexem of this class
	 * @param grammar
	 *            a grammar
	 * @return a grammatical kind
	 */
	public abstract int getKind(String lexem, AbstractGrammar grammar);

	/**
	 * Reads the stream so as to make the longest lexem of this class. The
	 * following must hold when calling the method: <li>
	 * <code>isStart(stream.getTokenStart())</code></li> <li>
	 * the stream remained unchanged since latest call to
	 * <code>stream.startNext()</code></li>
	 * 
	 * @param stream
	 *            a stream
	 * @param grammar
	 *            a grammar
	 * @return <code>true</code> iff reading succeeded
	 */
	public boolean read(LexStream stream, AbstractGrammar grammar) {
		while (!stream.isEOF() && isPart(stream.curCodePoint())) {
			stream.goForward();
		}
		return true;
	}

	/**
	 * Returns whether the given string is a lexem of this lexical class.
	 * 
	 * @param str
	 *            a string
	 * @return <code>true</code> iff the string is a lexem of this lexical class
	 */
	public boolean contains(String str) {
		if (str.isEmpty()) {
			return false;
		}
		if (!isStart(str.codePointAt(0))) {
			return false;
		}
		final int start = nextCodePoint(str, 0);
		for (int i = start; i < str.length(); i = nextCodePoint(str, i)) {
			if (!isPart(str.codePointAt(i))) {
				return false;
			}
		}
		return true;
	}
}