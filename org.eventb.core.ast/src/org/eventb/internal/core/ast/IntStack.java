/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Arrays;

/**
 * A stack of integers.
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
	
	public void incrementTop() {
		++ array[index];
	}
	
	public void replaceTop(int i) {
		array[index] = i;
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
