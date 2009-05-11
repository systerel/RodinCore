/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;

public abstract class AbstractBooleanManipulation extends
		AbstractAttributeManipulation {

	protected final String TRUE;
	protected final String FALSE;
	private final String[] possiblesValues;

	public AbstractBooleanManipulation(String trueText, String falseText) {
		TRUE = trueText;
		FALSE = falseText;
		possiblesValues = new String[] { FALSE, TRUE };
	}

	public final String getText(boolean value) {
		return (value) ? TRUE : FALSE;
	}

	public final String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return possiblesValues.clone();
	}

}
