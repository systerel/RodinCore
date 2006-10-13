/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IContextPointerArray extends IState {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".contextPointerArray";

	public static int EXTENDS_POINTER = 1;
	public static int SEES_POINTER = 2;
	
	int getContextPointerType();
	
	void setError(int index);
	
	boolean hasError(int index);
	
	int size();
	
	/**
	 * Returns the index of the context with the name <code>contextName</code>.
	 * @param contextName the name of the context
	 * @return the index of the context
	 */
	int getPointerIndex(String contextName);
	
	IInternalElement getContextPointer(int index);
	
	ISCContextFile getSCContextFile(int index);
	
	List<ISCContext> getUpContexts(int index);
	
	List<IIdentifierSymbolInfo> getIdentifierSymbolInfos(int index);
	
	List<ISCContext> getValidContexts();
	
}
