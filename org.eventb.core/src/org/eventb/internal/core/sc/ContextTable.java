/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCContext;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextTable extends State implements IContextTable {

	@Override
	public String toString() {
		return contexts.keySet().toString();
	}

	private final Hashtable<String, ISCContext> contexts;
	
	public ContextTable(int size) {
		contexts = new Hashtable<String, ISCContext>(size);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextTable#addContext(java.lang.String, org.eventb.core.ISCContext)
	 */
	public void addContext(String name, ISCContext context) throws CoreException {
		assertMutable();
		contexts.put(name, context);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextTable#containsContext(java.lang.String)
	 */
	public boolean containsContext(String name) {
		return contexts.containsKey(name);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextTable#getContext(java.lang.String)
	 */
	public ISCContext getContext(String name) {
		return contexts.get(name);
	}

	public int size() {
		return contexts.size();
	}

}
