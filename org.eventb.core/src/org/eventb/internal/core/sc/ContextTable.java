/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;

import org.eventb.core.ISCContext;
import org.eventb.core.sc.IContextTable;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextTable implements IContextTable {

	private final Hashtable<String, ISCContext> contexts;
	
	public ContextTable(int size) {
		contexts = new Hashtable<String, ISCContext>(size);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextTable#addContext(java.lang.String, org.eventb.core.ISCContext)
	 */
	public void addContext(String name, ISCContext context) {
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
		// TODO Auto-generated method stub
		return contexts.size();
	}

}
