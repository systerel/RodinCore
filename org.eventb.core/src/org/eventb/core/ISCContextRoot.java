/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked (SC) context files.
 * <p>
 * An SC context file has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event-B SC context are:
 * <ul>
 * <li>extends clauses (<code>ISCExtendsContext</code>)</li>
 * <li>internal contexts (<code>ISCInternalContext</code>)</li>
 * <li>carrier sets (<code>ISCCarrierSet</code>)</li>
 * <li>constants (<code>ISCConstant</code>)</li>
 * <li>axioms (<code>ISCAxiom</code>)</li>
 * <li>theorems (<code>ISCTheorem</code>)</li>
 * </ul>
 * </p>
 * <p>
 * The internal contexts are a local copy of the contents of the abstract
 * contexts of this context, i.e., the contexts which are, directly or
 * indirectly, extended by this context. The other child elements of this
 * context are the SC versions of the elements of the unchecked version of this
 * context. They are manipulated by means of {@link org.eventb.core.ISCContext}.
 * In addition, access methods for related file handles are provided.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCContextRoot extends ISCContext, IEventBRoot, IAccuracyElement, IConfigurationElement {

	IInternalElementType<ISCContextRoot> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scContextFile"); //$NON-NLS-1$

	/**
	 * Returns a handle to a child internal context with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the internal context
	 * @return a handle to a child internal context with the given element name
	 */
	ISCInternalContext getSCInternalContext(String elementName);

	/**
	 * Returns the internal SC contexts that are (transitively) contained in,
	 * i.e. extended by, this SC context.
	 * 
	 * @return an array of all internal contexts
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInternalContext[] getAbstractSCContexts() throws RodinDBException;

	/**
	 * Returns a handle to a child SC extends clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC extends clause
	 * @return a handle to a child SC extends clause with the given element name
	 */
	ISCExtendsContext getSCExtendsClause(String elementName);

	/**
	 * Returns an array of all SC extends clauses of this SC context.
	 * @return an array of SC extends clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCExtendsContext[] getSCExtendsClauses() throws RodinDBException;

	/**
	 * Returns the type environment defined by this context file. The returned
	 * type environment is made of all carrier sets and constants defined in
	 * this context and its abstractions.
	 * <p>
	 * It can be used subsequently to type-check the axioms and theorems of this
	 * context.
	 * </p>
	 * 
	 * @param factory
	 *            formula factory to use for building the result
	 * @return the type environment of this context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @since 3.0
	 */
	ITypeEnvironmentBuilder getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;

}
