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
package org.eventb.core.ast.extension;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;

/**
 * Common protocol encapsulating a type annotation.
 * <p>
 * This annotation corresponds to an occurrence of a type after an ⦂ (oftype)
 * operator.
 * <p>
 * This interface is normally used by the formula parser only. It is
 * nevertheless published because it occurs in the {@link FormulaFactory} API.
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 3.9
 */
public interface ITypeAnnotation {

	/**
	 * Returns the type in the annotation.
	 *
	 * @return the type in the annotation
	 */
	Type getType();

}
