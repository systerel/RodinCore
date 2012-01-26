/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - refactored according to new AutoElementNaming implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IAxiom;

public class AutoAxmNaming extends AutoElementNaming {
		
	public AutoAxmNaming() {
		super(IAxiom.ELEMENT_TYPE);
	}
}