/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.prover.ICommandApplication;
import org.eventb.ui.prover.IProofCommand;

public class CommandApplication implements ICommandApplication {

	private final ImageDescriptor iconDesc;
	private final String tooltip;
	private final IProofCommand command;
	private volatile Image lazyIcon;

	public CommandApplication(IProofCommand command, ImageDescriptor iconDesc,
			String tooltip) {
		this.iconDesc = iconDesc;
		this.tooltip = tooltip;
		this.command = command;
	}

	@Override
	public Image getIcon() {
		/*
		 * This is thread-safe because lazyIcon is volatile and
		 * EventBImage.getImage() always returns the same value.
		 */
		if (lazyIcon == null) {
			lazyIcon = EventBImage.getImage(iconDesc);
		}
		return lazyIcon;
	}

	@Override
	public IProofCommand getProofCommand() {
		return command;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

}