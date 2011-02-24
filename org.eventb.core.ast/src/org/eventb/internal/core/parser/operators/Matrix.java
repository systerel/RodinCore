/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser.operators;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class Matrix {

	private final BitSet bits;
	private final int size;
	private final int offset;
	
	public Matrix(int size) {
		this(size, 0);
	}
	
	public Matrix(int size, int offset) {
		this.size = size;
		this.offset = offset;
		this.bits = new BitSet(size * size);
	}

	private void checkIndexes(int a, int b) {
		if (outOfBounds(a) || outOfBounds(b)) {
			throw new IndexOutOfBoundsException("Square Matrix of size " + size
					+ " cannot accept (" + a + "," + b + ")");
		}
	}

	private boolean outOfBounds(int a) {
		return a < 0 || a >= size;
	}

	public void set(int a, int b) {
		a -= offset;
		b -= offset;
		checkIndexes(a, b);
		bits.set(a * size + b);
	}

	public boolean get(int a, int b) {
		a -= offset;
		b -= offset;
		return bits.get(a * size + b);
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (get(i, j)) {
					sb.append('X');
				} else {
					sb.append(' ');
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	public <T> Map<T, Set<T>> toRelationMap(Instantiator<Integer, T> inst) {
		final Map<T, Set<T>> relMap = new HashMap<T, Set<T>>();
		for (int i = 0; i < getSize(); i++) {
			final T leftOp = inst.instantiate(i + offset);
			final Set<T> rightOps = new HashSet<T>();
			for (int j = 0; j < getSize(); j++) {
				if (get(i, j)) {
					final T rightOp = inst.instantiate(j + offset);
					rightOps.add(rightOp);
				}
			}
			relMap.put(leftOp, rightOps);
		}
		return relMap;
	}

}
