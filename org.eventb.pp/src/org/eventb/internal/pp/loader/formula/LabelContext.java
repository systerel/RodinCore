/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

public class LabelContext extends AbstractContext {

	private boolean isPositiveLabel;
	private boolean isNegativeLabel;
	
	boolean isPositiveLabel() {
		return isPositiveLabel;
	}
	
	void setPositiveLabel(boolean isPositiveLabel) {
		this.isPositiveLabel = isPositiveLabel;
	}
	
	boolean isNegativeLabel() {
		return isNegativeLabel;
	}
	
	void setNegativeLabel(boolean isNegativeLabel) {
		this.isNegativeLabel = isNegativeLabel;
	}
	
}
