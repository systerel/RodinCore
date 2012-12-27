/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

/**
 * A context pointer array serves to manage references to contexts. The
 * structure of context relationships is a directed acyclic graph which is
 * flattened by the static checker. The context pointer array is used to keep
 * track of conflicts caused by multiple sees in a machine or extends clauses in
 * a context. Conflicts are caused whenever an identifier, carrier set or
 * constant, is declared in two <b>different</b> contexts that are
 * (transitively) referenced by a context or machine.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IContextPointerArray extends ISCState {

	final static IStateType<IContextPointerArray> STATE_TYPE = SCCore
			.getToolStateType(EventBPlugin.PLUGIN_ID + ".contextPointerArray");

	/**
	 * The enumerated type <code>PointerType</code> is used determine whether
	 * this context pointer array is used with sees clauses in a machine or
	 * extends clauses in a context.
	 * 
	 */
	public enum PointerType {
		EXTENDS_POINTER, SEES_POINTER
	}

	/**
	 * Returns the type of context pointers used with this context pointer
	 * array.
	 * 
	 * @return the type of context pointers used with this context pointer array
	 */
	PointerType getContextPointerType();

	/**
	 * Returns whether the error flag of the context pointer with the specified
	 * index is set.
	 * 
	 * @param index
	 *            the index of the context pointer
	 * @return whether the error flag of the context pointer with the specified
	 *         index is set
	 */
	boolean hasError(int index);

	/**
	 * Returns the number of context pointers contained in this context pointer
	 * array.
	 * 
	 * @return the number of context pointers contained in this context pointer
	 *         array
	 */
	int size();

	/**
	 * Returns the index in this context pointer array of the context with the
	 * specified name.
	 * 
	 * @param contextName
	 *            the name of the context
	 * @return the index of the context
	 */
	int getPointerIndex(String contextName);

	/**
	 * Returns the context pointer associated with the specified index. This is
	 * either {@link IExtendsContext} or {@link ISeesContext} depending on the
	 * pointer type ({@link PointerType}) of this context pointer array.
	 * 
	 * @param index
	 *            the index of the context pointer
	 * @return the context pointer with the specified index
	 */
	IInternalElement getContextPointer(int index);

	/**
	 * Returns a reference to the statically checked context file associated
	 * with the specified index. This is the statically checked context
	 * retrieved by means of {@link IExtendsContext} or {@link ISeesContext}.
	 * This is a convenience method to facilitate access to the referenced
	 * (statically checked) contexts.
	 * <p>
	 * If the returned value is <code>null</code> for some index, this means
	 * that no file has been specified and the index should be ignored.
	 * </p>
	 * 
	 * @param index
	 *            the index of the statically checked context file
	 * @return a reference to the statically checked context file file
	 *         associated with the specified index
	 */
	IRodinFile getSCContextFile(int index);

}
