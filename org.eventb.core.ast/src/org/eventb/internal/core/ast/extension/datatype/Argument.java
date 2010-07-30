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
package org.eventb.internal.core.ast.extension.datatype;

import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;

/**
 * @author Nicolas Beauger
 *
 */
public class Argument implements IArgument {

	
	private final String destructorName;
	private final IArgumentType type;
	
	public Argument(String destructorName, IArgumentType type) {
		this.destructorName = destructorName;
		this.type = type;
	}

	public Argument(IArgumentType type) {
		this(null, type);
	}

	@Override
	public IArgumentType getType() {
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

}
