/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for variables in Event-B statically checked (SC) machines.
 * <p>
 * An SC variable is a variable that has been statically checked. An SC variable
 * has a name that is returned by
 * {@link IRodinElement#getElementName()} and contains a type
 * that is accessed and manipulated via
 * {@link ISCIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinElement#getElementName()
 * @see ISCIdentifierElement#getType(FormulaFactory)
 * @see ISCIdentifierElement#setType(Type, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface ISCVariable extends ITraceableElement, ISCIdentifierElement {

	IInternalElementType<ISCVariable> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scVariable"); //$NON-NLS-1$

	/**
	 * <p>
	 * A variable that was declared in an abstract machine but not
	 * in a refined machine is <i>forbidden</i> in the refined machine and all its
	 * refinements, i.e. the variable can never be used again.
	 * </p>
	 * <p>
	 * A variable cannot be preserved and forbidden at the same time.
	 * </p>
	 * 
	 * @param value whether the variable is forbidden or not
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated instead of <code>setForbidden(b,pm)</code> use <code>setConcrete(!b,pm)</code>
	 */
	@Deprecated
	void setForbidden(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the variable name is forbidden or not.
	 * <p>
	 * A variable that was declared in an abstract machine but not
	 * in a refined machine is <i>forbidden</i> in the refined machine and all its
	 * refinements, i.e. the variable can never be used again.
	 * </p>
	 * <p>
	 * A variable cannot be preserved and forbidden at the same time.
	 * </p>
	 * 
	 * @return whether the variable name is forbidden or not
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>!isConcrete()</code>
	 */
	@Deprecated
	boolean isForbidden() throws RodinDBException;
	
	/**
	 * <p>
	 * A variable that was declared in an abstract machine and is declared again
	 * in a refined machine is <i>preserved</i> in the refined machine.
	 * </p>
	 * <p>
	 * A variable cannot be preserved and forbidden at the same time.
	 * </p>
	 * 
	 * @param value whether the variable is preserved or not
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated instead of <code>setPreserved(b,pm)</code> use <code>setAbstract(b,pm);setConcrete(b,pm)</code>
	 */
	@Deprecated
	void setPreserved(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the variable name is preserved or not.
	 * <p>
	 * A variable that was declared in an abstract machine and is declared again
	 * in a refined machine is <i>preserved</i> in the refined machine.
	 * </p>
	 * <p>
	 * A variable cannot be preserved and forbidden at the same time.
	 * </p>
	 * 
	 * @return whether the variable name is preserved or not
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>isAbstract() && isConcrete()</code>
	 */
	@Deprecated
	boolean isPreserved() throws RodinDBException;

	/**
	 * <p>
	 * A variable that was declared in the (immediate) abstract machine is "abstract".
	 * </p>
	 * 
	 * @param value whether the variable is abstract or not
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setAbstract(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the variable name is abstract or not.
	 * 
	 * @return whether the variable name is forbidden or not
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean isAbstract() throws RodinDBException;
	
	/**
	 * A variable that is declared in the concrete machine is "concrete".
	 * 
	 * @param value whether the variable is preserved or not
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setConcrete(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the variable name is concrete or not.
	 * 
	 * @return whether the variable name is preserved or not
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean isConcrete() throws RodinDBException;

}
