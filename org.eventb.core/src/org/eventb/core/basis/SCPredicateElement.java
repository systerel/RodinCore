/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Mathematical Language V2
 *     Systerel - added origin of predicates in proof
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation of Event-B SC elements that contain a predicate, as
 * an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCPredicateElement</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public abstract class SCPredicateElement extends EventBElement 
implements ISCPredicateElement {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCPredicateElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/**
	 * @since 3.0
	 */
	@Override
	public Predicate getPredicate(ITypeEnvironment typenv)
			throws RodinDBException {
		
		String contents = getPredicateString();
		final IRodinElement source;
		if (hasAttribute(EventBAttributes.SOURCE_ATTRIBUTE)) {
			source = getAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE);
		} else {
			source = null;
		}
		final FormulaFactory factory = typenv.getFormulaFactory();
		IParseResult parserResult = factory
				.parsePredicate(contents, V2, source);
		if (parserResult.hasProblem()) {
			throw Util.newRodinDBException(
					Messages.database_SCPredicateParseFailure, this);
		}
		Predicate result = parserResult.getParsedPredicate();
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (! tcResult.isSuccess())  {
			throw Util.newRodinDBException(
					Messages.database_SCPredicateTCFailure,
					this
			);
		}
		assert result.isTypeChecked();
		return result;
	}
	
	@Override
	public void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException {
		setPredicateString(predicate.toStringWithTypes(), monitor);
	}

}

