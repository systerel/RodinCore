/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.paramTactics;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.seqprover.IParameterDesc;

public class ParameterDesc implements IParameterDesc {

	private final String label;
	private final ParameterType type;
	private final Object defaultValue;
	private final String description;

	public ParameterDesc(String label, ParameterType type,
			Object defaultValue, String description) {
		this.label = label;
		this.type = type;
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public static IParameterDesc load(IConfigurationElement element) {
		final String label = element.getAttribute("label");
		final String sType = element.getAttribute("type");
		final ParameterType type = getType(sType);
		final String sDefault = element.getAttribute("default");
		final Object defaultValue = type.parse(sDefault);
		String description = element.getAttribute("description");
		if (description == null) description = "";
		return new ParameterDesc(label, type, defaultValue, description);
	}

	private static ParameterType getType(String typeName) {
		if (typeName.equals("Boolean")) {
			return ParameterType.BOOL;
		}
		if (typeName.equals("Integer")) {
			return ParameterType.INT;
		}
		if (typeName.equals("Long")) {
			return ParameterType.LONG;
		}
		if (typeName.equals("String")) {
			return ParameterType.STRING;
		}
		throw new IllegalArgumentException(
				"invalid tactic parameter type name: " + typeName);
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public ParameterType getType() {
		return type;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParameterDesc other = (ParameterDesc) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null) {
				return false;
			}
		} else if (!defaultValue.equals(other.defaultValue)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
