/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pom;


/**
 * Computes the longest increasing subsequence of the array of integers given to
 * the constructor.
 * <p>
 * The algorithm used is the one described in <a
 * href-"http://en.wikipedia.org/wiki/Longest_increasing_subsequence_problem">Wikipedia</a>.
 * 
 * @author Laurent Voisin
 */
public class LongestIncrSubseq {

	// Length and original sequence
	private final int length;
	private final int[] sequence;
	
	// Maps each increasing subsequence length to the index of its last element
	// or 0 (if there is not increasing subsequence of that length).
	private final int[] bestIdx;

	// Maps the index of an element in the longest increasing subsequence to the
	// index of the previous element in that sequence.
	private final int[] prevIdx;

	// Length of the longest increasing subsequence.
	private int subLen = 0;
	
	public LongestIncrSubseq(int[] sequence) {
		this.length = sequence.length;
		this.sequence = sequence;
		this.bestIdx = new int[length+1];
		this.prevIdx = new int[length];
		compute();
	}

	private void compute() {
		for (int i = 0; i < length; ++ i) {
			final int cur = sequence[i];
			final int j = best(cur);
			prevIdx[i] = bestIdx[j];
			final int j1 = j+1;
			if (j == subLen || cur < sequence[bestIdx[j1]]) {
				bestIdx[j1] = i;
				if (j1 > subLen) {
					subLen = j1;
				}
			}
		}
	}

	// TODO optimize as seq[bestIdx[]] is non decreasing (use binary search).
	private int best(int cur) {
		for (int j = subLen; 0 < j; -- j) {
			if (sequence[bestIdx[j]] < cur) {
				return j;
			}
		}
		return 0;
	}
	
	public int[] result() {
		int[] result = new int[subLen];
		if (subLen != 0) {
			int idx = bestIdx[subLen];
			for (int i = subLen-1; 0 <= i; -- i) {
				result[i] = sequence[idx];
				idx = prevIdx[idx];
			}
		}
		return result;
	}

}
