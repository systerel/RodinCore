/*******************************************************************************
 * Copyright (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Heinrich Heine Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/

package org.eventb.ui.symboltable.internal;

public class Symbol {
	public enum Category {
		Comparison, Relations, Assignment, Sets, Other
	};

	public final String text;
	public final String description;
	public final String asciiCombo;
	public final Category category;

	public Symbol(final String asciiCombo, final String text,
			final String description, final Category category) {
		this.text = text;
		this.description = description;
		this.asciiCombo = asciiCombo;
		this.category = category;
	}
}
