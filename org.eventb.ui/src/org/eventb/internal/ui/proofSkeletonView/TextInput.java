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
 * Common protocol for text input objects.
 * 
 * @author Nicolas Beauger
 * 
 */
public abstract class TextInput implements IViewerInput {

	abstract String getText();

	public Object[] getElements() {
		return new Object[] { getText() };
	}

}
