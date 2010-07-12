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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;


/**
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IToStringMediator {
	// TODO remove unused methods

	void append(String string);
	
	void appendSpace();
	
	void appendImage(int kind);

	int getKind();
	
	void subPrint(Formula<?> child, boolean isRight);

	void forward(Formula<?> child, boolean withTypes);

	void subPrint(Formula<?> child, boolean isRightOvr,
			BoundIdentDecl[] boundDecls);
	
	void forward(Formula<?> child);
	
	FormulaFactory getFactory();

	void appendBoundIdent(int boundIndex);
	
	boolean isWithTypes();
}
