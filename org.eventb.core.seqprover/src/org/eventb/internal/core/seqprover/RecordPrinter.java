/*******************************************************************************
 * Copyright (c) 2024 Systerel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static java.util.Arrays.asList;

import java.util.Collection;

/**
 * A class for easier development of toString() methods for debugging purposes.
 * 
 * It is particularly useful on deeply structured objects.
 * 
 * Newlines are printed lazily, that is only when followed by some other string.
 * 
 * @author Laurent Voisin
 */
public class RecordPrinter {

	/*
	 * Implementation notes:
	 * 
	 * New lines are not printed directly, but are delayed. Calling the newline()
	 * method will just record the request for a new line. The new line character
	 * will be appended to the builder only when something is printed after.
	 */

	private static final String INDENT = "  ";

	private final StringBuilder builder = new StringBuilder();

	private boolean pendingNewline = false;

	private String indent = "";

	/**
	 * Prints the string representation of the given object, followed by a newline
	 * indication.
	 * 
	 * @param value the object to print
	 * @return this printer
	 */
	public RecordPrinter println(Object value) {
		print(value);
		return newline();
	}

	/**
	 * Increases the indentation level of subsequent lines.
	 * 
	 * @return this printer
	 */
	public RecordPrinter indent() {
		indent += INDENT;
		return this;
	}

	/**
	 * Decreases the indentation level of subsequent lines.
	 * 
	 * @return this printer
	 */
	public RecordPrinter dedent() {
		indent = indent.substring(INDENT.length());
		return this;
	}

	/**
	 * Prints the field with the given name and value on one line, followed by a new
	 * line indication.
	 * 
	 * @param name  the name of the field
	 * @param value the value of the field
	 * @return this printer
	 */
	public RecordPrinter print(String name, Object value) {
		printHeader(name);
		print(" ");
		println(value);
		return this;
	}

	/**
	 * Prints the field with the given name and collection of values, followed by a
	 * new line indication. The values are printed by reflection. Each value must
	 * implement the {@code print(RecordPrinter)} method for printing itself.
	 * 
	 * @param name   the name of the field
	 * @param values the values of the field
	 * @return this printer
	 */
	public <E> RecordPrinter printRec(String name, Collection<E> values) {
		if (values.isEmpty()) {
			return this;
		}
		printHeader(name);
		indent();
		for (var value : values) {
			doNewline();
			try {
				value.getClass().getMethod("print", getClass()).invoke(value, this);
			} catch (Exception e) {
				// This code is intended for debugging, do not spend more time on exceptions
				e.printStackTrace();
			}
		}
		dedent();
		newline();
		return this;
	}

	/**
	 * Prints the field with the given name and array of values, followed by a new
	 * line indication. The values are printed by reflection. Each value must
	 * implement the {@code print(RecordPrinter)} method for printing itself.
	 * 
	 * @param name   the name of the field
	 * @param values the values of the field
	 * @return this printer
	 */
	public <E> RecordPrinter printRec(String name, E[] values) {
		return printRec(name, asList(values));
	}

	/**
	 * Prints the field with the given name and collection of values, followed by a
	 * new line indication. The values are printed one per line, with increased
	 * indentation.
	 * 
	 * @param name   the name of the field
	 * @param values the values of the field
	 * @return this printer
	 */
	public <E> RecordPrinter printLines(String name, Collection<E> values) {
		if (values.isEmpty()) {
			return this;
		}
		printHeader(name);
		indent();
		for (var value : values) {
			doNewline();
			print(value);
		}
		dedent();
		newline();
		return this;
	}

	/**
	 * Prints the field with the given name and array of values, followed by a new
	 * line indication. The values are printed one per line, with increased
	 * indentation.
	 * 
	 * @param name   the name of the field
	 * @param values the values of the field
	 * @return this printer
	 */
	public <E> RecordPrinter printLines(String name, E[] values) {
		return printLines(name, asList(values));
	}

	/**
	 * Prints the field with the given name and collection of values, followed by a
	 * new line indication. The name and values are all printed on the same line.
	 * The values are separated by commas.
	 * 
	 * @param name   the name of the field
	 * @param values the values of the field
	 * @return this printer
	 */
	public <E> RecordPrinter print(String name, Collection<E> values) {
		if (values.isEmpty()) {
			return this;
		}
		printHeader(name);
		String sep = " ";
		for (var value : values) {
			print(sep);
			sep = ", ";
			print(value);
		}
		newline();
		return this;
	}

	/**
	 * Prints the field with the given name and array of values, followed by a new
	 * line indication. The name and values are all printed on the same line. The
	 * values are separated by commas.
	 * 
	 * @param name   the name of the field
	 * @param values the values of the field
	 * @return this printer
	 */
	public <E> RecordPrinter print(String name, E[] values) {
		return print(name, asList(values));
	}

	/**
	 * Returns the contents of the printer.
	 */
	@Override
	public String toString() {
		return builder.toString();
	}

	private void printHeader(String name) {
		print(name);
		print(":");
	}

	private RecordPrinter print(Object value) {
		if (pendingNewline) {
			doNewline();
		}
		builder.append(value);
		return this;
	}

	private RecordPrinter newline() {
		pendingNewline = true;
		return this;
	}

	private RecordPrinter doNewline() {
		pendingNewline = false;
		return print("\n").print(indent);
	}
}
