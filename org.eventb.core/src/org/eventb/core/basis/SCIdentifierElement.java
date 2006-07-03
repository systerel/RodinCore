/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B SC identifiers as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCIdentifierElement</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public abstract class SCIdentifierElement extends InternalElement
		implements ISCIdentifierElement {

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCIdentifierElement#getIdentifierName()
	 */
	public String getIdentifierName() throws RodinDBException {
		return getElementName();
	}

	public SCIdentifierElement(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	public Type getType(FormulaFactory factory) throws RodinDBException {
		String contents = getContents();
		IParseResult parserResult = factory.parseType(contents);
		if (parserResult.getProblems().size() != 0) {
			throw Util.newRodinDBException(
					Messages.database_SCIdentifierTypeParseFailure,
					this
			);
		}
		return parserResult.getParsedType();
	}

	public void setType(Type type) throws RodinDBException {
		setContents(type.toString());
	}

	public FreeIdentifier getIdentifier(FormulaFactory factory)
			throws RodinDBException {

		final Type type = getType(factory);
		final String myName = getElementName();
		if (! factory.isValidIdentifierName(myName)) {
			throw Util.newRodinDBException(
					Messages.database_SCIdentifierNameParseFailure,
					this
			);
		}
		// TODO enquire about what source location to put
		return factory.makeFreeIdentifier(getElementName(), null, type);
	}

}
