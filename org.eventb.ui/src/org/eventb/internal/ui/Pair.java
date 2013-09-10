/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

public class Pair<S extends Object, T extends Object> {

	S obj1;

	T obj2;

	public Pair(S obj1, T obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public S getFirst() {
		return obj1;
	}

	public T getSecond() {
		return obj2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
		result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (obj1 == null) {
			if (other.obj1 != null) {
				return false;
			}
		} else if (!obj1.equals(other.obj1)) {
			return false;
		}
		if (obj2 == null) {
			if (other.obj2 != null) {
				return false;
			}
		} else if (!obj2.equals(other.obj2)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + obj1 + "," + obj2 + ")";
	}

}