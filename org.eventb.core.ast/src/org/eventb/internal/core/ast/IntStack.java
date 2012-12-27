/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Arrays;

/**
 * A stack of non-negative integers.
 *
 * @author Laurent Voisin
 */
public class IntStack {

	private static final int DEFAULT_SIZE = 11;
	
	// Array of integers in the stack.
	private int[] array;
	
	// Index of the top element
	private int index; 
	
	public IntStack() {
		array = new int[DEFAULT_SIZE];
		index = -1;
	}
	
	public boolean isEmpty() {
		return index < 0;
	}
	
	public void push(int i) {
		if (i < 0) throw new IllegalArgumentException("Negative index " + i);
		if (++ index >= array.length) {
			int[] newArray = new int[array.length + DEFAULT_SIZE];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}
		array[index] = i;
	}
	
	public void pop() {
		if (index < 0) {
			throw new IllegalStateException("Stack is empty");
		}
		-- index;
	}
	
	public int top() {
		return array[index];
	}
	
	// In general, we should check that we don't go beyond Integer.MAX_VALUE,
	// but in practice, that can't happen in the AST library.
	public void incrementTop() {
		++ array[index];
	}
	
	public int[] toArray() {
		final int len = index + 1;
		int[] result = new int[len];
		System.arraycopy(array, 0, result, 0, len);
		return result;
	}
	
	// Quite inefficient, but only for debugging purpose.
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}
}
