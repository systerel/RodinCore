/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinFile;

/**
 * Implementation of an (unchecked) Event-B Context.
 * 
 * @author Laurent Voisin
 */
public class Context extends RodinFile {
	
	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".context";

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public Context(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
