/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.util.MementoTokenizer.java which is
 * 
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.util;

import org.rodinp.core.basis.RodinElement;

public class MementoTokenizer {
	private static final String EXTERNAL = Character.toString(RodinElement.REM_EXTERNAL);
	private static final String INTERNAL = Character.toString(RodinElement.REM_INTERNAL);
	private static final String COUNT = Character.toString(RodinElement.REM_COUNT);

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
	
	public String nextToken() {
		int start = this.index;
		StringBuffer buffer = null;
		switch (this.memento[this.index++]) {
			case RodinElement.REM_ESCAPE:
				buffer = new StringBuffer();
				buffer.append(this.memento[this.index]);
				start = ++this.index;
				break;
			case RodinElement.REM_EXTERNAL:
				return EXTERNAL;
			case RodinElement.REM_INTERNAL:
				return INTERNAL;
			case RodinElement.REM_COUNT:
				return COUNT;
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
				case RodinElement.REM_COUNT:
					break loop;
			}
			this.index++;
		}
		if (buffer != null) {
			buffer.append(this.memento, start, this.index - start);
			return buffer.toString();
		} else {
			return new String(this.memento, start, this.index - start);
		}
	}
	
}
