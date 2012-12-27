/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.autocompletion;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class TypeFilter extends AbstractFilter {

	private final IInternalElementType<?>[] keepTypes;

	public TypeFilter(IInternalElementType<?>... keepTypes) {
		this.keepTypes = keepTypes;
	}

	@Override
	public boolean propose(IDeclaration declaration) {
		final IInternalElementType<?> elemType = declaration.getElement()
				.getElementType();
		return TypeFilter.isOneOf(elemType, keepTypes);
	}

	private static boolean isOneOf(IInternalElementType<?> typeToCheck, IInternalElementType<?>... types) {
		for (IInternalElementType<?> type : types) {
			if (typeToCheck == type) {
				return true;
			}
		}
		return false;
	}

}
