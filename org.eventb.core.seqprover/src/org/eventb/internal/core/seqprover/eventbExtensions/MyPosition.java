/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KRAN;

/**
 * Class used for the reasoner MembershipGoal, as well as the associated tactic
 * MembershipGoalTac.
 * 
 * @author Emmanuel Billaud
 */

public class MyPosition {
	private int[] pos = {};

	public MyPosition() {
		pos = new int[0];
	}

	private MyPosition(int[] t) {
		pos = t;
	}

	public int[] getPos() {
		return pos;
	}

	public boolean isRoot() {
		return pos.length == 0;
	}

	private MyPosition addChildEnd(int childNumber) {
		int[] p = new int[pos.length + 1];
		System.arraycopy(pos, 0, p, 0, pos.length);
		p[pos.length] = childNumber;
		return new MyPosition(p);
	}

	public MyPosition addChildEndNorm(int childNumber) {
		return addChildEnd(childNumber).normalize();
	}

	private MyPosition addChildStart(int childNumber) {
		int[] p = new int[pos.length + 1];
		p[0] = childNumber;
		System.arraycopy(pos, 0, p, 1, pos.length);
		return new MyPosition(p);
	}

	public MyPosition addChildStartNorm(int childNumber) {
		return addChildStart(childNumber).normalize();
	}

	private MyPosition addChildrenStart(MyPosition position) {
		if (position.isRoot()) {
			return this;
		}
		final int[] arrayPos = position.pos;
		final int arrayPosLength = arrayPos.length;
		final int posLength = pos.length;

		int[] p = new int[posLength + arrayPosLength];
		System.arraycopy(arrayPos, 0, p, 0, arrayPosLength);
		System.arraycopy(pos, 0, p, arrayPosLength, posLength);
		return new MyPosition(p);
	}

	public MyPosition addChildrenStartNorm(MyPosition position) {
		return addChildrenStart(position).normalize();
	}

	public MyPosition removeFirstChild() {
		if (isRoot()) {
			return null;
		}
		int[] p = new int[pos.length - 1];
		System.arraycopy(pos, 1, p, 0, p.length);
		return new MyPosition(p);
	}

	public int getFirstChild() {
		if (isRoot()) {
			return -1;
		}
		return pos[0];
	}

	public int getLastChild() {
		if (isRoot()) {
			return -1;
		}
		return pos[pos.length - 1];
	}

	public MyPosition removeLastChild() {
		if (isRoot()) {
			return null;
		}
		int[] p = new int[pos.length - 1];
		System.arraycopy(pos, 0, p, 0, p.length);
		return new MyPosition(p);
	}

	public boolean equals(MyPosition position) {
		if (pos.length != position.pos.length) {
			return false;
		}
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] != position.pos[i]) {
				return false;
			}
		}
		return true;
	}

	public MyPosition sub(MyPosition position) {
		final int positionLength = position.pos.length;
		if (positionLength > pos.length) {
			return null;
		}
		int[] currentPosition = new int[positionLength];
		System.arraycopy(pos, 0, currentPosition, 0, positionLength);
		if (!position.equals(new MyPosition(currentPosition))) {
			return null;
		}
		final int resultLength = pos.length - positionLength;
		int[] result = new int[resultLength];
		System.arraycopy(pos, positionLength, result, 0, resultLength);
		return new MyPosition(result);
	}

	private MyPosition normalize() {
		MyPosition norm = new MyPosition();
		for (int i = 0; i < pos.length; i++) {
			final int currentValue = pos[i];
			if (i == pos.length - 1) { // if it is the last element of the
				// array <code>pos</code>, it is added whatever its value.
				norm = norm.addChildEnd(currentValue);
				continue;
			}
			if (currentValue != CONVERSE) {
				norm = norm.addChildEnd(currentValue);
				continue;
			}
			i++;
			final int nextValue = pos[i];
			if (nextValue == KDOM) {
				norm = norm.addChildEnd(KRAN);
			} else if (nextValue == KRAN) {
				norm = norm.addChildEnd(KDOM);
			} else if (nextValue == CONVERSE) {
			} else {
				norm = norm.addChildEnd(CONVERSE);
				i--;
			}
		}
		return norm;
	}

}