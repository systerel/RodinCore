/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

/**
 * An accuracy info for an element keeps information whether the statically
 * checked element (including its children) is accurate, i.e. it corresponds to
 * an exact copy of the unchecked element, or whether some attribute or child
 * element has been added, removed, or modified.
 * <p>
 * The accuracy information is only an estimation of the actual accuracy. Its
 * intended to be used downstream in derived resources, e.g., proof obligation
 * files. It can be useful to alert the user to inaccuracy of some resource.
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IAccuracyInfo extends ISCState {

	/**
	 * Returns whether the corresponding element is accurate or not.
	 * 
	 * @return whether the corresponding element is accurate or not
	 */
	boolean isAccurate();
	
	/**
	 * Mark the corresponding element <i>not</i> accurate.
	 * 
	 */
	void setNotAccurate();
}
