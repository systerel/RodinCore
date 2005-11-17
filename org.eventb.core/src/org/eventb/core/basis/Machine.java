/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachine;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.RodinFile;

/**
 * Implementation of Event-B (unchecked) machines as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * file element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IMachine</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Machine extends RodinFile implements IMachine {
	
	static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".machine";

	/**
	 *  Constructor used by the Rodin database. 
	 */
	protected Machine(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
