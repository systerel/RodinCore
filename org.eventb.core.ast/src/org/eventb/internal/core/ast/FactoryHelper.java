/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Collection;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * Utility class for formula factories. Contains methods for creating fresh
 * arrays from collections.
 * 
 * @author Laurent Voisin
 */
public class FactoryHelper {

	/*
	 * Since Java generics use erased types, we need to make one method for each
	 * kind of array.
	 */

	public static Expression[] toExprArray(Collection<Expression> coll) {
		final Expression[] model = new Expression[coll.size()];
		return coll.toArray(model);
	}

	public static FreeIdentifier[] toIdentArray(Collection<FreeIdentifier> coll) {
		final FreeIdentifier[] model = new FreeIdentifier[coll.size()];
		return coll.toArray(model);
	}

	public static Predicate[] toPredArray(Collection<Predicate> coll) {
		final Predicate[] model = new Predicate[coll.size()];
		return coll.toArray(model);
	}

	public static BoundIdentDecl[] toBIDArray(Collection<BoundIdentDecl> coll) {
		final BoundIdentDecl[] model = new BoundIdentDecl[coll.size()];
		return coll.toArray(model);
	}

	public static Type[] toTypeArray(Collection<Type> coll) {
		final Type[] model = new Type[coll.size()];
		return coll.toArray(model);
	}

}
