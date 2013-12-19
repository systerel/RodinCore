/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Mathematical Language V2
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.Util.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

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
 * @since 1.0
 */
public abstract class SCIdentifierElement extends EventBElement
		implements ISCIdentifierElement {

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCIdentifierElement#getIdentifierString()
	 */
	@Override
	public String getIdentifierString() throws RodinDBException {
		return getElementName();
	}

	public SCIdentifierElement(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public Type getType(FormulaFactory factory) throws CoreException {
		final String contents = getAttributeValue(EventBAttributes.TYPE_ATTRIBUTE);
		final IParseResult parserResult = factory.parseType(contents);
		if (parserResult.hasProblem()) {
			throw newCoreException(
					Messages.database_SCIdentifierTypeParseFailure, this);
		}
		return parserResult.getParsedType();
	}

	@Override
	public void setType(Type type, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.TYPE_ATTRIBUTE, type.toString(), monitor);
	}

	@Override
	public FreeIdentifier getIdentifier(FormulaFactory factory)
			throws CoreException {
		final Type type = getType(factory);
		final String name = getElementName();
		if (!factory.isValidIdentifierName(name)) {
			throw newCoreException(
					Messages.database_SCIdentifierNameParseFailure, this);
		}
		final IRodinElement source = getSourceIfExists();
		final int start = 0;
		final int end = name.length() - 1;
		final SourceLocation sloc = new SourceLocation(start, end, source);
		return factory.makeFreeIdentifier(name, sloc, type);
	}

}
