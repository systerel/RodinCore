/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;

public class Declaration implements IDeclaration {

	private final IInternalElement element;
	private final String name;

	public Declaration(IInternalElement element, String name) {
		this.element = element;
		this.name = name;
	}

	@Override
	public IInternalElement getElement() {
		return element;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		int result;
		final int prime = 31;
		result = prime + element.hashCode();
		result = prime * result + name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final Declaration other = (Declaration) obj;
		if (!element.equals(other.element))
			return false;
		if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("element: " + element);
		sb.append(", ");
		sb.append("name: " + name);
		sb.append(")");
		return sb.toString();
	}
}
