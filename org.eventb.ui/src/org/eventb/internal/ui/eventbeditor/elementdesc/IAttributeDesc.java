/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;

public interface IAttributeDesc extends IItemDesc {

	public String getSuffix();

	public IEditComposite createWidget();

	/**
	 * This indicates that the editing area should expand horizontally.
	 */
	public boolean isHorizontalExpand();

	public IAttributeManipulation getManipulation();

	public IAttributeType getAttributeType();

}
