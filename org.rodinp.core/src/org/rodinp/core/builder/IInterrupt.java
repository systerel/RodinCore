/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.builder;

/**
 * @author Stefan Hallerstede
 * 
 * Interface used with tools to signal
 * an interrupt request. <code>isInterrupted()</code>
 * should be polled regularly. If its value is true
 * the tool will be re-run in any case.
 * 
 * @see org.rodinp.core.builder.IAutomaticTool
 *
 */
public interface IInterrupt {

	boolean isInterrupted();
	
}
