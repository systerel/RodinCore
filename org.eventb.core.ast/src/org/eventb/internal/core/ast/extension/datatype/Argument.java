/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import org.eventb.core.ast.extension.datatype.IArgument;

/**
 * @author Nicolas Beauger
 *
 */
public class Argument implements IArgument {

	
	private final String destructorName;
	private final ArgumentType type;
	
	public Argument(String destructorName, ArgumentType type) {
		this.destructorName = destructorName;
		this.type = type;
	}

	public Argument(ArgumentType type) {
		this(null, type);
	}

	@Override
	public ArgumentType getType() {
		return type;
	}

	@Override
	public boolean hasDestructor() {
		return destructorName != null;
	}

	@Override
	public String getDestructor() {
		return destructorName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destructorName == null) ? 0 : destructorName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (!(obj instanceof Argument)) {
			return false;
		}
		Argument other = (Argument) obj;
		if (destructorName == null) {
			if (other.destructorName != null) {
				return false;
			}
		} else if (!destructorName.equals(other.destructorName)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

}
