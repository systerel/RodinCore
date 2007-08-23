/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;

public final class Sort implements Comparable<Sort> {
	
	public static Sort NATURAL = new Sort(FormulaFactory.getDefault().makeIntegerType());
	public static Sort BOOLEAN = new Sort(FormulaFactory.getDefault().makeBooleanType());

	final private Type type;
	
	public Sort(Type type) {
		this.type = type;
	}
	
	public String getName() {
		return type.toString();
	}
	
	@Override
	public int hashCode() {
		return type.hashCode();
	}

	public boolean isSetSort() {
		return type.getBaseType() != null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Sort) {
			Sort temp = (Sort) obj;
			return type.equals(temp.type);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

	public int compareTo(Sort o) {
		return type.toString().compareTo(o.type.toString());
	}
}
