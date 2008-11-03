/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eventb.core.IPredicateElement;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.PredicateAttributeFactory;

public class PredicateLabelManipulation extends
		AbstractInternalElementLabelManipulation<IPredicateElement> {

	private static final IAttributeFactory<IPredicateElement> factory = new PredicateAttributeFactory();

	@Override
	IPredicateElement getElement(Object obj) {
		if (obj instanceof IPredicateElement) {
			return (IPredicateElement) obj;
		}
		return null;
	}

	@Override
	IAttributeFactory<IPredicateElement> getFactory(IPredicateElement element) {
		return factory;
	}

}
