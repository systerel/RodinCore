/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.ui.proofSkeletonView;

/**
 * @author Nicolas Beauger
 *
 */
public interface IViewerInput {

	/**
	 * Returns the elements to be used by the viewer's content provider.
	 * 
	 * @return an array of objects
	 */
	Object[] getElements();

	
	/**
	 * Returns the title tooltip to display.
	 * 
	 * @return a String, or <code>null</code> to clear tooltip
	 */
	String getTitleTooltip();
}
