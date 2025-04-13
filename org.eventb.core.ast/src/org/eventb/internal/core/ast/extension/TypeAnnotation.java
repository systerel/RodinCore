/*******************************************************************************
 * Copyright (c) 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeAnnotation;

/**
 * Common implementation of type annotations.
 */
public class TypeAnnotation implements ITypeAnnotation {

	private final Type type;

	public TypeAnnotation(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

}
