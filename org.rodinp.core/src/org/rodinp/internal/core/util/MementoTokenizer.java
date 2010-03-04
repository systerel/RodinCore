/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.internal.core.util.MementoTokenizer
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - removed occurrence count
 *     Systerel - now using Token objects
 *******************************************************************************/
package org.rodinp.internal.core.util;

import org.rodinp.core.basis.RodinElement;

public class MementoTokenizer {
	
	public static class Token {

		public static Token	EXTERNAL = new Token(Character.toString(RodinElement.REM_EXTERNAL));
		public static Token	INTERNAL = new Token(Character.toString(RodinElement.REM_INTERNAL));
		public static Token	TYPE_SEP = new Token(Character.toString(RodinElement.REM_TYPE_SEP));
		
		private final String representation;
		
		Token(String representation) {
			this.representation = representation;
		}

		public String getRepresentation() {
			return representation;
		}

	}

	private final char[] memento;
	private final int length;
	private int index = 0;
	
	public MementoTokenizer(String memento) {
		this.memento = memento.toCharArray();
		this.length = this.memento.length;
	}
	
	public boolean hasMoreTokens() {
		return this.index < this.length;
	}
	
	public Token nextToken() {
		int start = this.index;
		StringBuffer buffer = null;
		switch (this.memento[this.index++]) {
			case RodinElement.REM_ESCAPE:
				buffer = new StringBuffer();
				buffer.append(this.memento[this.index]);
				start = ++this.index;
				break;
			case RodinElement.REM_EXTERNAL:
				return Token.EXTERNAL;
			case RodinElement.REM_INTERNAL:
				return Token.INTERNAL;
			case RodinElement.REM_TYPE_SEP:
				return Token.TYPE_SEP;
		}
		loop: while (this.index < this.length) {
			switch (this.memento[this.index]) {
				case RodinElement.REM_ESCAPE:
					if (buffer == null) buffer = new StringBuffer();
					buffer.append(this.memento, start, this.index - start);
					start = ++this.index;
					break;
				case RodinElement.REM_EXTERNAL:
				case RodinElement.REM_INTERNAL:
				case RodinElement.REM_TYPE_SEP:
					break loop;
			}
			this.index++;
		}
		if (buffer != null) {
			buffer.append(this.memento, start, this.index - start);
			return new Token(buffer.toString());
		} else {
			return new Token(new String(this.memento, start, this.index - start));
		}
	}
	
}
