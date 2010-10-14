/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eventb.internal.ui.Pair;

public class Triplet<X extends Object, Y extends Object, Z extends Object> {
	private final Pair<X, Pair<Y, Z>> obj;

	public Triplet(X x, Y y, Z z) {
		obj = new Pair<X, Pair<Y, Z>>(x, new Pair<Y, Z>(y, z));
	}

	public X getFirst() {
		return obj.getFirst();
	}

	public Y getSecond() {
		return obj.getSecond().getFirst();
	}

	public Z getThird() {
		return obj.getSecond().getSecond();
	}

	@Override
	public boolean equals(Object o) {
		return obj.equals(o);
	}

	@Override
	public String toString() {
		return obj.toString();
	}
}