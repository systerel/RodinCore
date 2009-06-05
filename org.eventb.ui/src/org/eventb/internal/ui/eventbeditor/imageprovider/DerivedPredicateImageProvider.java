/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.imageprovider;

import static org.eventb.ui.IEventBSharedImages.IMG_THEOREM_PATH;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.IDerivedPredicateElement;
import org.eventb.internal.ui.EventBImage;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * The abstract base class used to provide images for predicate elements that
 * can be theorems.
 */
public abstract class DerivedPredicateImageProvider implements IImageProvider {

	private final String defaultImagePath;

	public DerivedPredicateImageProvider(String defaultImagePath) {
		this.defaultImagePath = defaultImagePath;
	}

	public ImageDescriptor getImageDescriptor(IRodinElement element) {
		assert element instanceof IDerivedPredicateElement;

		final IDerivedPredicateElement predicate = (IDerivedPredicateElement) element;
		try {
			if (predicate.hasTheorem() && predicate.isTheorem()) {
				return EventBImage.getImageDescriptor(IMG_THEOREM_PATH);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return EventBImage.getImageDescriptor(defaultImagePath);
	}
}