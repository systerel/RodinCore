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

import org.eventb.core.ast.Expression;

/**
 * Common protocol for expression extensions.
 * <p>
 * This interface adds a new method and should be implemented instead of
 * {@link IExpressionExtension}.
 *
 * @author Laurent Voisin
 * @since 3.9
 */
public interface IExpressionExtension2 extends IExpressionExtension {

	/**
	 * Indicates whether expressions using this extension should be decorated with a
	 * type annotation by the {@link Expression#toStringWithTypes()} method. Clients
	 * should return false by default, except in the rare cases where the type of
	 * the expression cannot be fully inferred from the type of children.
	 * <p>
	 * Said differently, returns <code>true</code> if and only if
	 * {@link #synthesizeType} may return <code>null</code> because it cannot
	 * compute a result type from type-checked children.
	 *
	 * @return <code>true</code> iff type annotations are needed
	 * @since 3.9
	 * @see #synthesizeType
	 */
	boolean needsTypeAnnotation();

}
