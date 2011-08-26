/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

/**
 * Common protocol for preference units. TODO replace by IPrefManEntry, adding support for references
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IPreferenceUnit<T> {

	String getName();
	
	T getElement();

	T getReference(IReferenceMaker<T> refMaker);

	void setName(String name);
	
	void setElement(T element);

}
