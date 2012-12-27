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
	public boolean equals(Object obj) {
		if (obj == null)
			return this == null;
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) obj;
			return pair.getFirst().equals(obj1)
					&& pair.getSecond().equals(obj2);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "(" + obj1 + "," + obj2 + ")";
	}

}