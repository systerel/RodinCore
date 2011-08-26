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
package org.eventb.internal.core.preferences;

import org.eventb.core.preferences.IPreferenceUnit;
import org.eventb.core.preferences.IReferenceMaker;

/**
 * @author Nicolas Beauger
 */
public class PrefUnit<T> implements IPreferenceUnit<T> {

	private String name;
	private T element;
	private T reference = null;

	public PrefUnit(String name, T element) {
		this.name = name;
		this.element = element;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getElement() {
		return element;
	}

	@Override
	public T getReference(IReferenceMaker<T> refMaker) {
		if (reference == null) {
			reference = refMaker.makeReference(this);
		}
		return reference;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void setElement(T element) {
		this.element = element;

	}
}
