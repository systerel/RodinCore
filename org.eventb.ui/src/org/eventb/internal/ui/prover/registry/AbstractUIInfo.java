/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eventb.internal.ui.EventBImage;

/**
 * Common superclass for info classes of the tactic registry that carry a name
 * and an icon.
 * 
 * @author beauger
 * 
 */
public class AbstractUIInfo extends AbstractInfo {

	private final String name;
	private final ImageDescriptor iconDesc;

	public AbstractUIInfo(String id, String name, ImageDescriptor iconDesc) {
		super(id);
		this.name = name;
		this.iconDesc = iconDesc;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the icon associated to the tactic.
	 * 
	 * @return an image, or <code>null</code> if the image could not be created
	 */
	public Image getIcon() {
		return EventBImage.getImage(iconDesc);
	}

}
